package com.golfpvcc.todolist.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfpvcc.todolist.tasks.domain.TaskLocalDataSource
import com.golfpvcc.todolist.tasks.domain.TaskSearchResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val taskLocalDataSource: TaskLocalDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _events = Channel<SearchEvent>()
    val events = _events.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) flowOf(emptyList()) else taskLocalDataSource.searchTasks(query)
                }
                .collect { results ->
                    _state.update { it.copy(results = results.map(TaskSearchResult::toSearchResultUi), isLoading = false) }
                }
        }
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChange -> {
                _state.update { it.copy(query = action.query, isLoading = action.query.isNotBlank()) }
                queryFlow.value = action.query
            }
            is SearchAction.OnResultClick -> {
                viewModelScope.launch {
                    _events.send(SearchEvent.NavigateToList(action.result.listId))
                }
            }
        }
    }
}

private fun TaskSearchResult.toSearchResultUi(): SearchResultUi = SearchResultUi(
    taskId = task.id,
    listId = task.listId,
    title = task.title,
    listName = listName,
    isDone = task.isDone
)