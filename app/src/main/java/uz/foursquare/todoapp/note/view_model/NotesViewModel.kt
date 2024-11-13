package uz.foursquare.todoapp.note.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import uz.foursquare.todoapp.todolist_screen.view_model.ToDoListRepository
import uz.foursquare.todoapp.types.TodoItem

class NotesViewModel(context: Context) : ViewModel() {

    private val repository = ToDoListRepository(context = context)

    fun addNote(note: TodoItem) {
        repository.addTask(note)
    }

    fun deleteNote(note: TodoItem) {
        repository.deleteTask(note)
    }

    fun getNote(id: String): List<TodoItem> {
        return repository.getTasks()
    }

    fun updateNote(note: TodoItem) {
        repository.updateTask(note)
    }

    fun getTaskById(id: String) = repository.getTaskById(id)


}