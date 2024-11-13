package uz.foursquare.todoapp.todolist_screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.foursquare.todoapp.R
import uz.foursquare.todoapp.todolist_screen.view_model.ToDoListViewModel
import uz.foursquare.todoapp.types.Importance
import uz.foursquare.todoapp.types.TodoItem
import uz.foursquare.todoapp.ui.theme.ToDoAppTheme
import uz.foursquare.todoapp.utils.convertMillisToDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListScreen(viewModel: ToDoListViewModel, navController: NavController, context: Context) {
    ToDoAppTheme {

        val backgroundColor = if (!isSystemInDarkTheme()) Color(0xFFF7F6F2) else Color(0xFF161618)
        val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

        val scrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

        val isCollapsed =
            remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }
        val completedTasks = viewModel.tasksStateFlow.collectAsState().value.count { it.done }
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor,
                        scrolledContainerColor = backgroundColor,
                        titleContentColor = textColor,
                    ),
                    title = {
                        AppBar(!isCollapsed.value, completedTasks)
                    },
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.shadow(if (!isCollapsed.value) 0.dp else 6.dp)
                )
            },
            floatingActionButton = {
                FAB(navController)
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                MainContent(viewModel = viewModel, navController = navController)

                viewModel.errorMessage.collectAsState().value?.let { errorMessage ->
                    Snackbar(
                        action = {
                            Button(onClick = {
                                viewModel.tasksStateFlow
                            }) {
                                Text("Повторить")
                            }
                        }
                    ) { Text("Something went wrong") }
                }


            }
        }
    }
}

@Composable
fun AppBar(state: Boolean, competedTasks: Int) {
    val backgroundColor = if (!isSystemInDarkTheme()) Color(0xFFF7F6F2) else Color(0xFF161618)
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Row(
        modifier = Modifier
            .padding(end = 24.dp, start = 60.dp)

    ) {

        Column(
            modifier = Modifier
                .weight(1f)
        ) {

            Text(
                "Мои дела",
                fontSize = 32.sp,
                fontWeight = FontWeight(500),
                color = textColor
            )

            if (state) {
                Text(
                    "Выполнено — $competedTasks",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF8E8E90)
                )
            }
        }

    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    viewModel: ToDoListViewModel,
    navController: NavController
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        val tasks = viewModel.tasksStateFlow.collectAsState()

        TaskList(tasks.value, viewModel, navController)
    }
}

@Composable
fun TaskList(tasks: List<TodoItem>, viewModel: ToDoListViewModel, navController: NavController) {
    val backgroundColor = if (isSystemInDarkTheme()) Color(0xFF252528) else Color.White
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    if (tasks.isEmpty()) {
        Text(
            text = "Нет задач",
            color = textColor,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(backgroundColor)
    ) {
        LazyColumn {
            items(tasks) { task ->
                TaskItem(task = task,
                    changeItem = {
                        navController.navigate("notesScreen/$it")
                    },
                    onCompleteChange = { isChecked ->
                        val changedTask = task.copy(done = isChecked)
                        viewModel.toggleTaskCompletion(changedTask)
                    })
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TodoItem, changeItem: (String) -> Unit, onCompleteChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()
    ) {
        val textColor = when (task.importance) {
            "low" -> if (task.done) Color.Gray else Color.Black
            "medium" -> if (task.done) Color.Gray else Color.Black
            "important" -> if (task.done) Color.Gray else Color(0xFFFF3B30)
            else -> Color.Black
        }

        val checkboxColor = when (task.importance) {
            "low" -> if (task.done) Color.Gray else Color(0xFF007AFF)
            "medium" -> if (task.done) Color.Gray else Color(0xFF007AFF)
            "important" -> if (task.done) Color.Gray else Color(0xFFFF3B30)
            else -> Color(0xFF007AFF)
        }

        val textDecoration =
            if (task.done) TextDecoration.LineThrough else TextDecoration.None

        Checkbox(
            checked = task.done,
            onCheckedChange = { isChecked -> onCompleteChange(isChecked) },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF007AFF), uncheckedColor = checkboxColor
            ),
            modifier = Modifier.padding(start = 8.dp)
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Row {
                when (task.importance) {
                    "important" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.high_priority_icon),
                            contentDescription = "Priority High",
                            tint = textColor,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }

                    "medium" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.medium_priority_icon),
                            contentDescription = "Priority Medium",
                            tint = textColor,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                        )
                    }

                    else -> {}
                }

                Text(
                    text = task.text, textAlign = TextAlign.Center, style = TextStyle(
                        textDecoration = textDecoration, color = textColor, fontSize = 16.sp
                    )
                )
            }

            if (task.deadline != null) {
                Text(
                    text = task.deadline.convertMillisToDate(),
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
            }
        }

        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Info icon",
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    changeItem(task.id)
                }
        )
    }
}

@Composable
fun FAB(
    navController: NavController, onFabClick: () -> Unit = {
        navController.navigate("notesScreen/null")
    }
) {
    FloatingActionButton(
        onClick = onFabClick, containerColor = Color(0xFF2979FF), shape = RoundedCornerShape(50)
    ) {
        Icon(
            imageVector = Icons.Filled.Add, contentDescription = "Add Task", tint = Color.White
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize(),
            floatingActionButton = { }) { innerPadding ->
            innerPadding
//            MainContent(modifier = Modifier.padding(innerPadding))
        }
    }
}