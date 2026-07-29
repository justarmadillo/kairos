package com.taha.kairos.features.cases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.model.Case
import com.taha.kairos.core.repository.CaseRepository
import com.taha.kairos.core.repository.DataSafetyCoordinator
import com.taha.kairos.core.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CaseDetailUiState(
    val case: Case? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isExportingShare: Boolean = false,
    val sharePayload: CaseSharePayload? = null,
    val message: String? = null,
)

data class CaseSharePayload(
    val file: File,
    val mimeType: String,
    val chooserTitle: String,
)

private data class ShareExportState(
    val isExporting: Boolean = false,
    val payload: CaseSharePayload? = null,
    val message: String? = null,
)

private data class ShareFileResult(
    val file: File,
    val skippedAttachmentCount: Int = 0,
)

private enum class ShareFormat { PDF, ZIP }

@HiltViewModel
class CaseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val caseRepo: CaseRepository,
    private val mediaRepo: MediaRepository,
    private val pdfExporter: CasePdfExporter,
    private val zipExporter: CaseZipExporter,
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : ViewModel() {

    private val caseId: Long = savedStateHandle.get<Long>("caseId") ?: run {
        // Log error and use -1 as sentinel; the UI will show empty/error state
        -1L
    }

    private val shareExportState = MutableStateFlow(ShareExportState())

    private val caseState = if (caseId == -1L) {
        flowOf(CaseDetailUiState(isLoading = false, isError = true))
    } else {
        caseRepo
            .observeById(caseId)
            .map { c -> CaseDetailUiState(case = c, isLoading = false) }
    }

    val ui: StateFlow<CaseDetailUiState> = combine(caseState, shareExportState) { caseUi, exportUi ->
        caseUi.copy(
            isExportingShare = exportUi.isExporting,
            sharePayload = exportUi.payload,
            message = exportUi.message,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CaseDetailUiState())

    fun softDelete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            caseRepo.softDelete(caseId)
            onDeleted()
        }
    }

    fun deleteMedia(mediaId: Long) {
        viewModelScope.launch { mediaRepo.delete(mediaId) }
    }

    fun setPrimaryMedia(mediaId: Long) {
        viewModelScope.launch { mediaRepo.setPrimary(caseId, mediaId) }
    }

    fun exportPdf() = export(ShareFormat.PDF)

    fun exportZip() = export(ShareFormat.ZIP)

    private fun export(format: ShareFormat) {
        val case = ui.value.case ?: return
        if (shareExportState.value.isExporting) return
        shareExportState.update {
            it.copy(isExporting = true, message = null, payload = null)
        }

        viewModelScope.launch {
            try {
                val result = dataSafetyCoordinator.withDataLock {
                    when (format) {
                        ShareFormat.PDF -> {
                            val file = pdfExporter.export(case)
                            ShareFileResult(file = file)
                        }
                        ShareFormat.ZIP -> zipExporter.export(case).let { zip ->
                            ShareFileResult(
                                file = zip.file,
                                skippedAttachmentCount = zip.skippedAttachmentCount,
                            )
                        }
                    }
                }
                val payload = when (format) {
                    ShareFormat.PDF -> CaseSharePayload(
                        file = result.file,
                        mimeType = "application/pdf",
                        chooserTitle = "Share PDF",
                    )
                    ShareFormat.ZIP -> CaseSharePayload(
                        file = result.file,
                        mimeType = "application/zip",
                        chooserTitle = "Share case ZIP",
                    )
                }
                val warning = result.skippedAttachmentCount
                    .takeIf { it > 0 }
                    ?.let { count ->
                        "ZIP created without $count unavailable " +
                            if (count == 1) "attachment." else "attachments."
                    }
                shareExportState.update {
                    ShareExportState(
                        isExporting = false,
                        payload = payload,
                        message = warning,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                shareExportState.update {
                    it.copy(
                        isExporting = false,
                        message = when (format) {
                            ShareFormat.PDF -> "Could not create PDF."
                            ShareFormat.ZIP -> "Could not create ZIP."
                        },
                    )
                }
            }
        }
    }

    fun clearSharePayload() =
        shareExportState.update { it.copy(payload = null) }

    fun reportShareFailure() =
        shareExportState.update { it.copy(message = "No app could share this file.") }

    fun clearMessage() =
        shareExportState.update { it.copy(message = null) }
}
