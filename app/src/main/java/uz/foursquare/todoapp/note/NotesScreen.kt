package uz.foursquare.todoapp.note

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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.foursquare.todoapp.note.view_model.NotesViewModel
import uz.foursquare.todoapp.types.TodoItem
import uz.foursquare.todoapp.ui.theme.ToDoAppTheme
import uz.foursquare.todoapp.utils.convertMillisToDate
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel, taskId: String?) {
    var currentTodoItem by remember { mutableStateOf<TodoItem?>(null) }

    if (taskId != null && taskId != "null" && taskId.isNotBlank()) {
        viewModel.getNoteById(taskId)
    }

    val task = viewModel.taskStateFlow.collectAsState().value

    Log.d("NotesScreen", "NotesScreen: $task")

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
                        Log.d("NotesScreen", "Saving note: $taskId")
                        if (!taskId.isNullOrEmpty()) {
                            currentTodoItem?.let { viewModel.addNote(it) }
                        } else {
                            currentTodoItem?.let { viewModel.updateNote(it) }
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

            viewModel.errorMessage.collectAsState().value?.let { errorMessage ->
                Snackbar(
                    action = {
                        Button(onClick = {
                            viewModel.getNoteById(taskId ?: "")
                        }) {
                            Text("Повторить")
                        }
                    }
                ) { Text("Something went wrong") }
            }
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

        var taskText by remember { mutableStateOf(task?.text ?: "") }
        var selectedImportance by remember { mutableStateOf(task?.importance ?: "low") }
        var isDueDateEnabled by remember { mutableStateOf(false) }
        var dueDate by remember { mutableStateOf(task?.deadline?.convertMillisToDate() ?: "") }
        var isDatePickerVisible by remember { mutableStateOf(false) }

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
            modifier = Modifier.fillMaxWidth()
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        val todoItem = TodoItem(
            id = task?.id ?: UUID.randomUUID().toString(),
            text = taskText,
            importance = selectedImportance,
            deadline = dueDate.toLongOrNull(),
            done = false,
            createdAt = task?.createdAt ?: Date().time,
            changedAt = Date().time,
            color = task?.color ?: "#FFFFFF",
            lastUpdatedBy = "user123",
            files = task?.files
        )
        onSave(todoItem)
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
    onItemSelected: (String) -> Unit
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
                onItemSelected("low")
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
                onItemSelected("basic")
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
                onItemSelected("important")
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
                    Log.d("NotesScreen", "Deleting note: ${isDeleteEnabled && task != null}")
                    if (isDeleteEnabled && task != null) {
                        viewModel.deleteNote(task.id)
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


//                DeleteContainer(true)
            }
        }
    }
}