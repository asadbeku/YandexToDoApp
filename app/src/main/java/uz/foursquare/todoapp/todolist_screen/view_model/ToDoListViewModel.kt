package uz.foursquare.todoapp.todolist_screen.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import uz.foursquare.todoapp.types.TodoItem
import uz.foursquare.todoapp.utils.Result

class ToDoListViewModel(context: Context) : ViewModel() {

    private val repository = ToDoListRepository(context)

    private val _tasks = MutableStateFlow<List<TodoItem>>(emptyList())
    val tasksStateFlow: StateFlow<List<TodoItem>> = _tasks.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val scope = viewModelScope + CoroutineExceptionHandler { context, throwable ->
        _errorMessage.value = throwable.message
        Log.e("ToDoListViewModel", "Coroutine exception", throwable)
    }


    init {
        scope.launch {
            withContext(Dispatchers.IO) {
                val state = repository.getTasks()
                if (state.isSuccess) {
                    Log.d("ToDoListViewModel", "Tasks fetched successfully: ${state.getOrNull()}")
                    _tasks.value = state.getOrNull() ?: emptyList()
                } else {
                    Log.e("ToDoListViewModel", "Failed to fetch tasks: ${state.exceptionOrNull()?.message}")
                    _errorMessage.value = state.exceptionOrNull()?.message
                }
            }
        }
    }

    fun toggleTaskCompletion(task: TodoItem) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val state = repository.updateTask(task)
            }
        }
//        repository.updateTaskCompletion(task.id, isCompleted)
//        _tasks.value = repository.getTasks()
    }
}
