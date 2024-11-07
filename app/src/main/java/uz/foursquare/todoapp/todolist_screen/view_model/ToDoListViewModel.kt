package uz.foursquare.todoapp.todolist_screen.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.foursquare.todoapp.types.TodoItem

class ToDoListViewModel(context: Context) : ViewModel() {

    private val repository = ToDoListRepository(context)

    private val _tasks = MutableStateFlow(repository.getTasks())
    val tasks: StateFlow<List<TodoItem>> = _tasks.asStateFlow()

    fun addTask(task: TodoItem) {
        repository.addTask(task)
        _tasks.value = repository.getTasks()
    }

    fun deleteTask(task: TodoItem) {
        repository.deleteTask(task)
        _tasks.value = repository.getTasks()
    }

    fun toggleTaskCompletion(task: TodoItem, isCompleted: Boolean) {
        repository.updateTaskCompletion(task.id, isCompleted)
        _tasks.value = repository.getTasks()
    }

    fun refreshTasks() {
        _tasks.value = repository.getTasks()
    }
}
