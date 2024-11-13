package uz.foursquare.todoapp.todolist_screen.view_model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import uz.foursquare.todoapp.network.ApiService
import uz.foursquare.todoapp.network.Network
import uz.foursquare.todoapp.types.TodoItem
import kotlin.coroutines.cancellation.CancellationException

class ToDoListRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("todo_list", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    // Load lastKnownRevision from SharedPreferences or use 0 if not set
    private var lastKnownRevision: Int
        get() = sharedPreferences.getInt("lastKnownRevision", 0)
        set(value) {
            editor.putInt("lastKnownRevision", value).apply()
        }

    suspend fun getTasks(): Result<List<TodoItem>> {
        try {
            val response = Network.buildService(ApiService::class.java).getTasks()
            Log.d("ToDoListRepository", "Response: ${response.body()}")
            if (response.isSuccessful) {
                val tasks = response.body()?.list ?: emptyList()

                // Update lastKnownRevision and save it to SharedPreferences
                lastKnownRevision = response.body()?.revision ?: lastKnownRevision

                return Result.success(tasks)
            } else {
                throw Exception("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Result.failure(e)
        }
    }

    suspend fun addTask(note: TodoItem) {
        try {
            Log.d("ToDoListRepository", "addTask: json: ${createJsonObject(note)}")
            val response =
                Network.buildService(ApiService::class.java)
                    .addTask(lastKnownRevision.toString(), createJsonObject(note))

            if (response.isSuccessful) {
                Log.d("ToDoListRepository", "Task added successfully")
            } else {
                throw Exception("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e
        }

    }

    suspend fun deleteTask(id: String) {
        try {
            val response =
                Network.buildService(ApiService::class.java)
                    .removeTask(id = id, lastKnownRevision = lastKnownRevision.toString())

            if (response.isSuccessful) {
                Log.d("ToDoListRepository", "Task deleted successfully")
            } else {
                throw Exception("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateTask(note: TodoItem) {
        try {
            val response =
                Network.buildService(ApiService::class.java)
                    .updateTask(lastKnownRevision.toString(), note.id, createJsonObject(note))

            if (response.isSuccessful) {
                Log.d("ToDoListRepository", "Task updated successfully")
            } else {
                throw Exception("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e
        }

    }

    private fun getTasksFromSharedPreferences(): List<TodoItem> {
        val serializedTasks = sharedPreferences.getString("tasks", "[]")
        return try {
            Gson().fromJson(serializedTasks, Array<TodoItem>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveTasksToSharedPreferences(tasks: List<TodoItem>) {
        val serializedTasks = Gson().toJson(tasks)
        editor.putString("tasks", serializedTasks).apply()
    }

    private fun createJsonObject(note: TodoItem): JsonObject {

        val element = JsonObject().apply {
            addProperty("id", note.id)
            addProperty("text", note.text)
            addProperty("importance", note.importance)
            addProperty("deadline", note.deadline)
            addProperty("done", note.done)
            addProperty("color", note.color)
            addProperty("created_at", note.createdAt)
            addProperty("changed_at", note.changedAt)
            addProperty("last_updated_by", note.lastUpdatedBy)
        }

        val jsonObject = JsonObject().apply {
            add("element", element)
        }

        return jsonObject
    }

    suspend fun getTaskById(id: String): Result<TodoItem> {
        try {
            val response =
                Network.buildService(ApiService::class.java)
                    .getTaskById(lastKnownRevision = lastKnownRevision.toString(), id = id)

            if (response.isSuccessful) {
                return response.body()?.let { Result.success(it.element) }
                    ?: Result.failure(Exception("Response body is null"))
            } else {
                throw Exception("Network request unsuccessful: ${response.code()}, ${response.message()}")
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }


}