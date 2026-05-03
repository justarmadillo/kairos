package com.kairos.features.cases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.core.model.Case
import com.kairos.core.repository.CaseRepository
import com.kairos.core.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
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
    val isExportingPdf: Boolean = false,
    val pdfToShare: File? = null,
    val message: String? = null,
)

private data class PdfExportState(
    val isExporting: Boolean = false,
    val pdfToShare: File? = null,
    val message: String? = null,
)

@HiltViewModel
class CaseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val caseRepo: CaseRepository,
    private val mediaRepo: MediaRepository,
    private val pdfExporter: CasePdfExporter,
) : ViewModel() {

    private val caseId: Long = savedStateHandle.get<Long>("caseId") ?: run {
        // Log error and use -1 as sentinel; the UI will show empty/error state
        -1L
    }

    private val pdfExportState = MutableStateFlow(PdfExportState())

    private val caseState = if (caseId == -1L) {
        flowOf(CaseDetailUiState(isLoading = false, isError = true))
    } else {
        caseRepo
            .observeById(caseId)
            .map { c -> CaseDetailUiState(case = c, isLoading = false) }
    }

    val ui: StateFlow<CaseDetailUiState> = combine(caseState, pdfExportState) { caseUi, exportUi ->
        caseUi.copy(
            isExportingPdf = exportUi.isExporting,
            pdfToShare = exportUi.pdfToShare,
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

    fun exportPdf() {
        val case = ui.value.case ?: return
        if (ui.value.isExportingPdf) return

        viewModelScope.launch {
            pdfExportState.update { it.copy(isExporting = true, message = null, pdfToShare = null) }
            try {
                val file = pdfExporter.export(case)
                pdfExportState.update { it.copy(isExporting = false, pdfToShare = file) }
            } catch (e: Exception) {
                pdfExportState.update {
                    it.copy(
                        isExporting = false,
                        message = e.message ?: "Could not export PDF",
                    )
                }
            }
        }
    }

    fun clearPdfShare() =
        pdfExportState.update { it.copy(pdfToShare = null) }

    fun clearMessage() =
        pdfExportState.update { it.copy(message = null) }
}
