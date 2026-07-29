package com.taha.kairos.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.repository.SearchRepository
import com.taha.kairos.core.repository.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: SearchRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val results = query
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { repo.observeSearch(it) }

    val ui: StateFlow<SearchUiState> = combine(query, results) { currentQuery, currentResults ->
        SearchUiState(
            query = currentQuery,
            results = currentResults,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState(),
    )

    fun setQuery(value: String) =
        query.update { value }

    fun clearQuery() =
        query.update { "" }
}
