package uz.foursquare.todoapp.note.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import uz.foursquare.todoapp.todolist_screen.view_model.ToDoListRepository
import uz.foursquare.todoapp.types.TodoItem
import java.util.Date

class NotesViewModel(context: Context) : ViewModel() {

    private val repository = ToDoListRepository(context)

    // Nullable task StateFlow, initially set to null
    private val _task = MutableStateFlow<TodoItem?>(null)
    val taskStateFlow: StateFlow<TodoItem?> = _task.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val scope =
        viewModelScope + CoroutineExceptionHandler { _, throwable ->
            _errorMessage.value = "Failed to fetch tasks"
            Log.e("NotesViewModel", "Coroutine exception", throwable)
        }

    // Adds a note asynchronously
    fun addNote(note: TodoItem) {
        Log.d("NotesViewModel", "Adding note: $note")
        scope.launch {
            repository.addTask(note)
        }
    }

    // Deletes a note by its ID asynchronously
    fun deleteNote(id: String) {
        scope.launch {
            repository.deleteTask(id)
        }
    }

    // Fetches a note by its ID asynchronously and updates _task
    fun getNoteById(id: String) {
        scope.launch {
            val result = repository.getTaskById(id)
            result.onSuccess {
                _task.value = it
                Log.d("NotesViewModel", "Task fetched successfully: $it")
            }.onFailure {
                Log.e("NotesViewModel", "Failed to fetch task", it)
            }
        }
    }

    // Updates a note asynchronously
    fun updateNote(note: TodoItem) {
        scope.launch {
            repository.updateTask(note)
        }
    }
}


