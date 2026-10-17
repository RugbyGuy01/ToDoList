package com.golfpvcc.todolist.tasks.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golfpvcc.todolist.core.presentation.ObserveAsEvents
import com.golfpvcc.todolist.tasks.domain.Priority
import com.golfpvcc.todolist.tasks.domain.TaskFilter
import com.golfpvcc.todolist.tasks.domain.TaskSort
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun TaskListRoot(
    onNavigateBack: () -> Unit,
    viewModel: TaskListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            TaskListEvent.ListNotFound -> onNavigateBack()
        }
    }

    TaskListScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    state: TaskListState,
    onAction: (TaskListAction) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.listName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(TaskListAction.OnAddTaskClick) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            FilterAndSortRow(state = state, onAction = onAction)

            if (state.tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks here.")
                }
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggleDone = { onAction(TaskListAction.OnToggleDone(task)) },
                            onClick = { onAction(TaskListAction.OnTaskClick(task)) },
                            onDeleteClick = { onAction(TaskListAction.OnDeleteTaskClick(task)) }
                        )
                    }
                }
            }
        }
    }

    if (state.editorVisible) {
        TaskEditorSheet(
            editingTask = state.editingTask,
            onSave = { title, description, priority, dueDate, reminderAt ->
                onAction(TaskListAction.OnSaveTask(title, description, priority, dueDate, reminderAt))
            },
            onDismiss = { onAction(TaskListAction.OnDismissEditor) }
        )
    }

    state.taskPendingDeletion?.let { task ->
        AlertDialog(
            onDismissRequest = { onAction(TaskListAction.OnDismissDeleteTask) },
            title = { Text("Delete \"${task.title}\"?") },
            confirmButton = {
                TextButton(onClick = { onAction(TaskListAction.OnConfirmDeleteTask) }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { onAction(TaskListAction.OnDismissDeleteTask) }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun FilterAndSortRow(
    state: TaskListState,
    onAction: (TaskListAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TaskFilter.entries.forEach { filter ->
                FilterChip(
                    selected = state.filter == filter,
                    onClick = { onAction(TaskListAction.OnFilterChange(filter)) },
                    label = { Text(filter.name) }
                )
            }
        }
        SortMenu(selected = state.sort, onSelect = { onAction(TaskListAction.OnSortChange(it)) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortMenu(selected: TaskSort, onSelect: (TaskSort) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        ) {
            Text("Sort: ${selected.name}")
        }
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TaskSort.entries.forEach { sort ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(sort.name) },
                    onClick = {
                        onSelect(sort)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: TaskUi,
    onToggleDone: () -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isDone, onCheckedChange = { onToggleDone() })
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Priority: ${task.priority.name}", style = MaterialTheme.typography.bodySmall)
                    task.formattedDueDate?.let {
                        Text("Due: $it", style = MaterialTheme.typography.bodySmall)
                    }
                }
                task.formattedReminder?.let {
                    Text("Reminder: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete task")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditorSheet(
    editingTask: TaskUi?,
    onSave: (title: String, description: String, priority: Priority, dueDate: Long?, reminderAt: Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    var title by rememberSaveable { mutableStateOf(editingTask?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(editingTask?.description ?: "") }
    var priority by rememberSaveable { mutableStateOf(editingTask?.priority ?: Priority.MEDIUM) }
    var dueDate by rememberSaveable { mutableStateOf(editingTask?.dueDate) }
    var reminderEnabled by rememberSaveable { mutableStateOf(editingTask?.reminderAt != null) }
    var reminderAt by rememberSaveable { mutableStateOf(editingTask?.reminderAt) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op: WorkManager/NotificationHelper will silently skip if denied */ }

    fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                if (editingTask == null) "New task" else "Edit task",
                style = MaterialTheme.typography.titleMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(4.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name) }
                    )
                }
            }

            DueDatePicker(
                dueDate = dueDate,
                onDueDateChange = { dueDate = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Remind me")
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = { enabled ->
                        reminderEnabled = enabled
                        if (enabled) {
                            requestNotificationPermissionIfNeeded()
                            if (reminderAt == null) reminderAt = dueDate ?: System.currentTimeMillis()
                        } else {
                            reminderAt = null
                        }
                    }
                )
            }

            TextButton(
                onClick = {
                    onSave(
                        title,
                        description,
                        priority,
                        dueDate,
                        if (reminderEnabled) reminderAt else null
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun DueDatePicker(dueDate: Long?, onDueDateChange: (Long?) -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(dueDate?.let { java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) } ?: "No due date")
        TextButton(onClick = {
            dueDate?.let { calendar.timeInMillis = it }
            android.app.DatePickerDialog(
                context,
                { _, year, month, day ->
                    calendar.set(year, month, day, 0, 0, 0)
                    onDueDateChange(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Pick date")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListScreenPreview() {
    TaskListScreen(
        state = TaskListState(
            listName = "Groceries",
            tasks = listOf(
                TaskUi(1, "Milk", "", false, Priority.LOW, null, null, null, null),
                TaskUi(2, "Eggs", "", true, Priority.MEDIUM, null, null, null, null)
            )
        ),
        onAction = {}
    )
}