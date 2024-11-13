package uz.foursquare.todoapp.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.convertToReadableFormat(): String {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

fun Long.convertMillisToDate(locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat("d MMMM yyyy", locale)
    val date = Date(this)
    return formatter.format(date)
}

fun Long.convertStringToDate(): Date? {
    return try {
        val dateFormat =
            SimpleDateFormat("d MMMM yyyy", Locale.getDefault()) // Adjust the format as needed
        dateFormat.parse(this.convertMillisToDate()) ?: Date()
    } catch (e: ParseException) {
        null
    }

}