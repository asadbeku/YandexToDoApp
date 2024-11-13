package uz.foursquare.todoapp.types

import java.util.Date

data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Date?,
    var isCompleted: Boolean,
    val createdAt: Date,
    val modifiedAt: Date?
)