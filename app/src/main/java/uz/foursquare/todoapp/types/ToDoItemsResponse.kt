package uz.foursquare.todoapp.types


import com.google.gson.annotations.SerializedName

data class ToDoItemsResponse(
    @SerializedName("list")
    val list: List<TodoItem>,
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("status")
    val status: String
)