package uz.foursquare.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.foursquare.todoapp.note.NotesScreen
import uz.foursquare.todoapp.note.view_model.NotesViewModel
import uz.foursquare.todoapp.todolist_screen.GreetingPreview
import uz.foursquare.todoapp.todolist_screen.ToDoListScreen
import uz.foursquare.todoapp.todolist_screen.view_model.ToDoListViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "todoListScreen") {
                composable("todoListScreen") {
                    ToDoListScreen(
                        viewModel = ToDoListViewModel(applicationContext),
                        navController,
                        applicationContext
                    )
                }

                composable("notesScreen/{taskId}") { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId")
                    NotesScreen(
                        navController,
                        viewModel = NotesViewModel(applicationContext),
                        taskId
                    ) // Replace with your NotesScreen composable function
                }
            }
        }
    }
}


