package com.kairos.core.repository

import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun observeSearch(query: String): Flow<List<SearchResult>>
}

data class SearchResult(
    val caseId: Long,
    val patientName: String,
    val patientAge: Int?,
    val phoneNumbers: List<String>,
    val caseDate: Long,
    val mechanism: String?,
    val diagnosisNames: List<String>,
    val notesHtml: String?,
)
