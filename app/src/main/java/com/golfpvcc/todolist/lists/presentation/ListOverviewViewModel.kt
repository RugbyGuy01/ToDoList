package com.golfpvcc.todolist.lists.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golfpvcc.todolist.lists.domain.TodoList
import com.golfpvcc.todolist.lists.domain.TodoListLocalDataSource
import com.golfpvcc.todolist.lists.domain.TodoListOverview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListOverviewViewModel(
    private val todoListLocalDataSource: TodoListLocalDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(ListOverviewState())
    val state = _state.asStateFlow()

    private val _events = Channel<ListOverviewEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeLists()
    }

    fun onAction(action: ListOverviewAction) {
        when (action) {
            ListOverviewAction.OnAddListClick -> {
                _state.update { it.copy(newListDialogVisible = true) }
            }
            ListOverviewAction.OnDismissNewListDialog -> {
                _state.update { it.copy(newListDialogVisible = false) }
            }
            is ListOverviewAction.OnConfirmNewList -> createList(action.name)
            is ListOverviewAction.OnListClick -> {
                viewModelScope.launch {
                    _events.send(ListOverviewEvent.NavigateToTasks(action.listId))
                }
            }
            is ListOverviewAction.OnDeleteListClick -> {
                _state.update { it.copy(listPendingDeletion = action.list) }
            }
            ListOverviewAction.OnDismissDeleteList -> {
                _state.update { it.copy(listPendingDeletion = null) }
            }
            ListOverviewAction.OnConfirmDeleteList -> confirmDeleteList()
            ListOverviewAction.OnSearchClick -> {
                viewModelScope.launch {
                    _events.send(ListOverviewEvent.NavigateToSearch)
                }
            }
        }
    }

    private fun observeLists() {
        viewModelScope.launch {
            todoListLocalDataSource.getLists().collect { overviews ->
                _state.update { it.copy(lists = overviews.map(TodoListOverview::toTodoListUi), isLoading = false) }
            }
        }
    }

    private fun createList(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            _state.update { it.copy(newListDialogVisible = false) }
            return
        }
        viewModelScope.launch {
            todoListLocalDataSource.upsertList(
                TodoList(name = trimmed, createdAt = System.currentTimeMillis())
            )
            _state.update { it.copy(newListDialogVisible = false) }
        }
    }

    private fun confirmDeleteList() {
        val list = _state.value.listPendingDeletion ?: return
        viewModelScope.launch {
            todoListLocalDataSource.deleteList(list.id)
            _state.update { it.copy(listPendingDeletion = null) }
        }
    }
}

private fun TodoListOverview.toTodoListUi(): TodoListUi = TodoListUi(
    id = list.id,
    name = list.name,
    totalTaskCount = totalTaskCount,
    doneTaskCount = doneTaskCount
)