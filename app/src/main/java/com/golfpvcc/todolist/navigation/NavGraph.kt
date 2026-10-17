package com.golfpvcc.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.golfpvcc.todolist.lists.presentation.ListOverviewRoot
import com.golfpvcc.todolist.lists.presentation.ListsRoute
import com.golfpvcc.todolist.tasks.presentation.SearchRoot
import com.golfpvcc.todolist.tasks.presentation.SearchRoute
import com.golfpvcc.todolist.tasks.presentation.TaskListRoot
import com.golfpvcc.todolist.tasks.presentation.TasksRoute

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ListsRoute) {
        composable<ListsRoute> {
            ListOverviewRoot(
                onNavigateToTasks = { listId -> navController.navigate(TasksRoute(listId)) },
                onNavigateToSearch = { navController.navigate(SearchRoute) }
            )
        }
        composable<TasksRoute> {
            TaskListRoot(onNavigateBack = { navController.popBackStack() })
        }
        composable<SearchRoute> {
            SearchRoot(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToList = { listId ->
                    navController.navigate(TasksRoute(listId)) {
                        popUpTo(ListsRoute)
                    }
                }
            )
        }
    }
}