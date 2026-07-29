package com.taha.kairos.data.repository

import com.taha.kairos.core.model.toCapitalizedPatientName
import com.taha.kairos.core.repository.SearchRepository
import com.taha.kairos.core.repository.SearchResult
import com.taha.kairos.data.db.dao.CaseDao
import com.taha.kairos.data.db.dao.SearchCaseRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val caseDao: CaseDao,
) : SearchRepository {

    override fun observeSearch(query: String): Flow<List<SearchResult>> {
        val tokens = query.searchTokens()
        if (tokens.isEmpty()) return flowOf(emptyList())

        val anchor = tokens.maxBy { it.length }
        return caseDao.observeSearchCases("%${anchor.escapeLikeWildcards()}%").map { rows ->
            rows
                .filter { row ->
                    val haystack = row.searchableText()
                    tokens.all { token -> haystack.contains(token) }
                }
                .take(50)
                .map { it.toSearchResult() }
        }
    }
}

private fun SearchCaseRow.toSearchResult(): SearchResult =
    SearchResult(
        caseId = caseId,
        patientName = patientName.toCapitalizedPatientName(),
        patientAge = patientAge,
        phoneNumbers = phoneNumbers.toListField(),
        caseDate = caseDate,
        mechanism = mechanism,
        diagnosisNames = diagnosisNames.toListField(),
        notesHtml = notesHtml,
    )

private fun SearchCaseRow.searchableText(): String =
    listOfNotNull(
        patientName,
        patientAge?.toString(),
        phoneNumbers,
        mechanism,
        diagnosisNames,
        notesHtml,
    ).joinToString(separator = " ")
        .lowercase(Locale.getDefault())

private fun String.searchTokens(): List<String> =
    trim()
        .lowercase(Locale.getDefault())
        .split(Regex("\\s+"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()

private fun String.escapeLikeWildcards(): String =
    replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")

private fun String?.toListField(): List<String> =
    this
        ?.lineSequence()
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?.toList()
        ?: emptyList()
