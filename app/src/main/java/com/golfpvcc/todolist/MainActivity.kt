package com.golfpvcc.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.golfpvcc.todolist.navigation.AppNavGraph
import com.golfpvcc.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                ToDoListApp()
            }
        }
    }
}

@Composable
private fun ToDoListApp() {
    val navController = rememberNavController()
    AppNavGraph(navController = navController)
}