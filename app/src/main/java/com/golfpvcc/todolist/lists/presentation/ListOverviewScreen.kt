package com.golfpvcc.todolist.lists.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golfpvcc.todolist.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListOverviewRoot(
    onNavigateToTasks: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: ListOverviewViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ListOverviewEvent.NavigateToTasks -> onNavigateToTasks(event.listId)
            ListOverviewEvent.NavigateToSearch -> onNavigateToSearch()
        }
    }

    ListOverviewScreen(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOverviewScreen(
    state: ListOverviewState,
    onAction: (ListOverviewAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Lists") },
                actions = {
                    IconButton(onClick = { onAction(ListOverviewAction.OnSearchClick) }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search tasks")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(ListOverviewAction.OnAddListClick) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add list")
            }
        }
    ) { innerPadding ->
        if (state.lists.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No lists yet. Tap + to create one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.lists, key = { it.id }) { list ->
                    TodoListCard(
                        list = list,
                        onClick = { onAction(ListOverviewAction.OnListClick(list.id)) },
                        onDeleteClick = { onAction(ListOverviewAction.OnDeleteListClick(list)) }
                    )
                }
            }
        }
    }

    if (state.newListDialogVisible) {
        NewListDialog(
            onConfirm = { onAction(ListOverviewAction.OnConfirmNewList(it)) },
            onDismiss = { onAction(ListOverviewAction.OnDismissNewListDialog) }
        )
    }

    state.listPendingDeletion?.let { list ->
        AlertDialog(
            onDismissRequest = { onAction(ListOverviewAction.OnDismissDeleteList) },
            title = { Text("Delete \"${list.name}\"?") },
            text = { Text("This will delete the list and all its tasks.") },
            confirmButton = {
                TextButton(onClick = { onAction(ListOverviewAction.OnConfirmDeleteList) }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(ListOverviewAction.OnDismissDeleteList) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TodoListCard(
    list: TodoListUi,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(list.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete list")
                }
            }
            Text("${list.doneTaskCount}/${list.totalTaskCount} done")
            if (list.totalTaskCount > 0) {
                val progress = list.doneTaskCount.toFloat() / list.totalTaskCount.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewListDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New list") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("List name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ListOverviewScreenPreview() {
    ListOverviewScreen(
        state = ListOverviewState(
            lists = listOf(
                TodoListUi(1, "Groceries", totalTaskCount = 5, doneTaskCount = 2),
                TodoListUi(2, "Work", totalTaskCount = 3, doneTaskCount = 3)
            )
        ),
        onAction = {}
    )
}