package com.golfpvcc.todolist.tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.golfpvcc.todolist.core.domain.onFailure
import com.golfpvcc.todolist.core.domain.onSuccess
import com.golfpvcc.todolist.lists.domain.TodoListLocalDataSource
import com.golfpvcc.todolist.tasks.domain.Task
import com.golfpvcc.todolist.tasks.domain.TaskFilter
import com.golfpvcc.todolist.tasks.domain.TaskLocalDataSource
import com.golfpvcc.todolist.tasks.domain.TaskSort
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskListViewModel(
    savedStateHandle: SavedStateHandle,
    private val taskLocalDataSource: TaskLocalDataSource,
    private val todoListLocalDataSource: TodoListLocalDataSource
) : ViewModel() {

    private val listId = savedStateHandle.toRoute<TasksRoute>().listId

    private val _state = MutableStateFlow(TaskListState(listId = listId))
    val state = _state.asStateFlow()

    private val _events = Channel<TaskListEvent>()
    val events = _events.receiveAsFlow()

    private var allTasks: List<Task> = emptyList()

    init {
        loadListName()
        observeTasks()
    }

    fun onAction(action: TaskListAction) {
        when (action) {
            is TaskListAction.OnFilterChange -> {
                _state.update { it.copy(filter = action.filter) }
                applyFilterAndSort()
            }
            is TaskListAction.OnSortChange -> {
                _state.update { it.copy(sort = action.sort) }
                applyFilterAndSort()
            }
            TaskListAction.OnAddTaskClick -> {
                _state.update { it.copy(editorVisible = true, editingTask = null) }
            }
            is TaskListAction.OnTaskClick -> {
                _state.update { it.copy(editorVisible = true, editingTask = action.task) }
            }
            is TaskListAction.OnToggleDone -> toggleDone(action.task)
            is TaskListAction.OnDeleteTaskClick -> {
                _state.update { it.copy(taskPendingDeletion = action.task) }
            }
            TaskListAction.OnConfirmDeleteTask -> confirmDeleteTask()
            TaskListAction.OnDismissDeleteTask -> {
                _state.update { it.copy(taskPendingDeletion = null) }
            }
            TaskListAction.OnDismissEditor -> {
                _state.update { it.copy(editorVisible = false, editingTask = null) }
            }
            is TaskListAction.OnSaveTask -> saveTask(action)
        }
    }

    private fun loadListName() {
        viewModelScope.launch {
            todoListLocalDataSource.getListById(listId)
                .onSuccess { list -> _state.update { it.copy(listName = list.name) } }
                .onFailure { _events.send(TaskListEvent.ListNotFound) }
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            taskLocalDataSource.getTasksForList(listId).collect { tasks ->
                allTasks = tasks
                applyFilterAndSort()
            }
        }
    }

    private fun applyFilterAndSort() {
        val filter = _state.value.filter
        val sort = _state.value.sort

        val filtered = when (filter) {
            TaskFilter.ALL -> allTasks
            TaskFilter.ACTIVE -> allTasks.filter { !it.isDone }
            TaskFilter.DONE -> allTasks.filter { it.isDone }
        }

        val sorted = when (sort) {
            TaskSort.DUE_DATE -> filtered.sortedWith(compareBy(nullsLast()) { it.dueDate })
            TaskSort.PRIORITY -> filtered.sortedByDescending { it.priority.ordinal }
            TaskSort.CREATED_AT -> filtered.sortedByDescending { it.createdAt }
        }

        _state.update { it.copy(tasks = sorted.map(Task::toTaskUi), isLoading = false) }
    }

    private fun toggleDone(task: TaskUi) {
        viewModelScope.launch {
            taskLocalDataSource.setDone(task.id, !task.isDone)
        }
    }

    private fun confirmDeleteTask() {
        val task = _state.value.taskPendingDeletion ?: return
        viewModelScope.launch {
            taskLocalDataSource.deleteTask(task.id)
            _state.update { it.copy(taskPendingDeletion = null) }
        }
    }

    private fun saveTask(action: TaskListAction.OnSaveTask) {
        val title = action.title.trim()
        if (title.isEmpty()) {
            _state.update { it.copy(editorVisible = false, editingTask = null) }
            return
        }
        val editing = _state.value.editingTask
        viewModelScope.launch {
            taskLocalDataSource.upsertTask(
                Task(
                    id = editing?.id ?: 0L,
                    listId = listId,
                    title = title,
                    description = action.description.trim(),
                    isDone = editing?.isDone ?: false,
                    priority = action.priority,
                    dueDate = action.dueDate,
                    reminderAt = action.reminderAt,
                    createdAt = editing?.let { allTasks.firstOrNull { t -> t.id == it.id }?.createdAt }
                        ?: System.currentTimeMillis()
                )
            )
            _state.update { it.copy(editorVisible = false, editingTask = null) }
        }
    }
}

private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
private val dateTimeFormatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())

private fun Task.toTaskUi(): TaskUi = TaskUi(
    id = id,
    title = title,
    description = description,
    isDone = isDone,
    priority = priority,
    dueDate = dueDate,
    reminderAt = reminderAt,
    formattedDueDate = dueDate?.let { dateFormatter.format(Date(it)) },
    formattedReminder = reminderAt?.let { dateTimeFormatter.format(Date(it)) }
)