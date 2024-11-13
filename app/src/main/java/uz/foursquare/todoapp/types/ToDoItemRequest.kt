package uz.foursquare.todoapp.types


import com.google.gson.annotations.SerializedName

data class ToDoItemRequest(
    @SerializedName("element")
    val element: TodoItem
)