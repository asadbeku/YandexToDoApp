package uz.foursquare.todoapp.types


import com.google.gson.annotations.SerializedName

data class ToDoEditedResponse(
    @SerializedName("element")
    val element: TodoItem,
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("status")
    val status: String
)