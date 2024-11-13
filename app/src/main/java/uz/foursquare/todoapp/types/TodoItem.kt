package uz.foursquare.todoapp.types

import com.google.gson.annotations.SerializedName
import java.util.Date


data class TodoItem(
    @SerializedName("changed_at")
    val changedAt: Long,
    @SerializedName("color")
    val color: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("deadline")
    val deadline: Long?,
    @SerializedName("done")
    val done: Boolean,
    @SerializedName("files")
    val files: Any?,
    @SerializedName("id")
    val id: String,
    @SerializedName("importance")
    val importance: String,
    @SerializedName("last_updated_by")
    val lastUpdatedBy: String,
    @SerializedName("text")
    val text: String
)