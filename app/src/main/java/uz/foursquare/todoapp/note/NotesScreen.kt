package uz.foursquare.todoapp.note

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.material.datepicker.MaterialDatePicker
import uz.foursquare.todoapp.note.view_model.NotesViewModel
import uz.foursquare.todoapp.types.Importance
import uz.foursquare.todoapp.types.TodoItem
import uz.foursquare.todoapp.ui.theme.ToDoAppTheme
import uz.foursquare.todoapp.utils.convertMillisToDate
import uz.foursquare.todoapp.utils.convertStringToDate
import uz.foursquare.todoapp.utils.convertToReadableFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel, taskId: String?) {
    var currentTodoItem by remember { mutableStateOf<TodoItem?>(null) }
    val task = taskId?.let { viewModel.getTaskById(it) }

    val backgroundColor = if (isSystemInDarkTheme()) Color(0xFF252528) else Color.White
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (taskId == null) currentTodoItem?.let { viewModel.addNote(it) } else currentTodoItem?.let {
                            viewModel.updateNote(
                                it
                            )
                        }
                        navController.popBackStack()
                    }) {
                        Text(
                            "Сохранить",
                            color = Color(0xFF007AFF),
                            fontSize = 16.sp,
                            modifier = Modifier
                        )

                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            TodoInputFields(task) {
                currentTodoItem = it
            }

            val isDeleteEnabled = task != null
            DeleteContainer(isDeleteEnabled, viewModel, task, navController)
        }
    }
}

@Composable
fun TodoInputFields(task: TodoItem?, onSave: (TodoItem) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        var taskText by remember { mutableStateOf("") }
        var selectedImportance by remember { mutableStateOf(Importance.LOW) }
        var isDueDateEnabled by remember { mutableStateOf(false) }
        var dueDate by remember { mutableStateOf("") }

        var isDatePickerVisible by remember { mutableStateOf(false) }

        if (task != null) {
            taskText = task.text
            selectedImportance = task.importance
            isDueDateEnabled = task.deadline != null
            dueDate = task.deadline?.convertToReadableFormat() ?: ""
        }

        OutlinedTextField(
            value = taskText,
            onValueChange = { taskText = it },
            label = { Text("Что надо сделать") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        MyComplexMenu {
            selectedImportance = it
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Сделать до",
                    fontSize = 16.sp,
                    color = Color(0xFF000000)
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (isDueDateEnabled) {
                    Text(
                        dueDate,
                        fontSize = 16.sp,
                        color = Color(0xFF007AFF),
                    )
                }

            }

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = isDueDateEnabled,
                onCheckedChange = {
                    isDueDateEnabled = it
                    isDatePickerVisible = it
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            if (isDatePickerVisible) {
                MyDatePickerDialog(onDateSelected = {
                    dueDate = it
                }, onDismiss = {
                    isDatePickerVisible = false
                })
            }

            val todoItem = TodoItem(
                id = UUID.randomUUID().toString(), // Generate unique ID
                text = taskText,
                importance = selectedImportance,
                deadline = if (isDueDateEnabled) dueDate.convertStringToDate() else null,
                isCompleted = false,
                createdAt = Date(),
                modifiedAt = null
            )

            onSave(todoItem)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= System.currentTimeMillis()
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.convertMillisToDate() ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
fun MyComplexMenu(
    onItemSelected: (Importance) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Нет") }
    val color = if (selectedItem == "!! Высокий") Color.Red else Color(0xFF000000)

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {

            Column(
                modifier = Modifier
                    .clickable { expanded = !expanded }
            ) {
                Text("Важность", fontSize = 16.sp, color = Color(0xFF000000))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = selectedItem, fontSize = 14.sp, color = color)
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp),
                    thickness = 1.dp,
                    color = Color(0x26000000)
                )
            }

        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp)
        ) {
            DropdownMenuItem(onClick = {
                onItemSelected(Importance.LOW)
                selectedItem = "Нет"
                expanded = false
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Нет")
                }
            }
            DropdownMenuItem(onClick = {
                onItemSelected(Importance.MEDIUM)
                selectedItem = "Низкий"
                expanded = false
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Низкий")
                }
            }
            DropdownMenuItem(onClick = {
                onItemSelected(Importance.HIGH)
                selectedItem = "!! Высокий"
                expanded = false
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("!! Высокий", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DeleteContainer(
    isDeleteEnabled: Boolean,
    viewModel: NotesViewModel,
    task: TodoItem?,
    navController: NavController
) {

    HorizontalDivider(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp),
        thickness = 1.dp,
        color = Color(0x26000000)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color = if (isDeleteEnabled) Color(0xFFFF3B30) else Color(0x26000000)

        Icon(Icons.Default.Delete, contentDescription = "Delete Icon", tint = color)

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Удалить",
            fontSize = 16.sp,
            color = color,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    if (isDeleteEnabled && task != null) {
                        viewModel.deleteNote(task)
                        navController.popBackStack()
                    }
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SecondScreenPreview() {

    ToDoAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = {

                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            // Действие при нажатии на кнопку "Сохранить"

                        }) {
                            Text(
                                "Сохранить",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                modifier = Modifier
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TodoInputFields(
                    TodoItem(
                        "1",
                        "Hello",
                        Importance.LOW,
                        null,
                        false,
                        Date(),
                        Date("")
                    )
                ) {

                }



//                DeleteContainer(true)
            }
        }
    }
}