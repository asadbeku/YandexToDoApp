package uz.foursquare.todoapp.todolist_screen.view_model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.foursquare.todoapp.types.Importance
import uz.foursquare.todoapp.types.TodoItem
import java.util.Date

class ToDoListRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ToDoApp", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Load tasks from SharedPreferences or use default sample tasks
    private var taskList: MutableList<TodoItem> = loadTasks()

    private fun loadTasks(): MutableList<TodoItem> {
        val json = sharedPreferences.getString("task_list", null)
        val type = object : TypeToken<MutableList<TodoItem>>() {}.type
        return if (json != null) gson.fromJson(json, type) else getDefaultTasks()
    }

    private fun saveTasks() {
        val json = gson.toJson(taskList)
        sharedPreferences.edit().putString("task_list", json).apply()
    }

    private fun getDefaultTasks(): MutableList<TodoItem> = mutableListOf(
        TodoItem("1", "Task 1", Importance.LOW, null, false, Date(), Date()),
        TodoItem("2", "Task 2", Importance.MEDIUM, Date(), true, Date(), Date()),
        TodoItem("3", "Task 3", Importance.HIGH, Date(), false, Date(), Date()),
        TodoItem("4", "Task 4", Importance.LOW, Date(), false, Date(), Date()),
        TodoItem("5", "Task 5", Importance.MEDIUM, Date(), true, Date(), Date()),
        TodoItem("6", "Task 6", Importance.HIGH, null, false, Date(), Date()),
        TodoItem("7", "Task 7", Importance.LOW, Date(), true, Date(), Date()),
        TodoItem("8", "Task 8", Importance.MEDIUM, Date(), false, Date(), Date()),
        TodoItem("9", "Task 9", Importance.HIGH, Date(), true, Date(), Date()),
        TodoItem("10", "Task 10", Importance.LOW, Date(), false, Date(), Date())
    )

    fun getTasks(): List<TodoItem> = taskList.toList()

    fun addTask(task: TodoItem) {
        taskList.add(task)
        saveTasks()
    }

    fun deleteTask(task: TodoItem) {
        taskList.remove(task)
        saveTasks()
    }

    fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        val taskIndex = taskList.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            taskList[taskIndex] = taskList[taskIndex].copy(isCompleted = isCompleted)
            saveTasks()
        }
    }

    fun updateTask(task: TodoItem) {
        val taskIndex = taskList.indexOfFirst { it.id == task.id }
        if (taskIndex != -1) {
            taskList[taskIndex] = task
            saveTasks()
        }
    }

    fun getTaskById(taskId: String): TodoItem? {
        return taskList.find { it.id == taskId }
    }
}