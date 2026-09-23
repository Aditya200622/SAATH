package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.ui.components.SaathPrimaryButton
import com.example.ui.theme.*

@Composable
fun TasksScreen(
    tasks: List<Task>,
    onToggleTaskComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onAddTask: (title: String, category: String, time: String, notes: String) -> Unit,
    onStartFocus: (String) -> Unit,
    onNavigateToDetail: (Task) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Today") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog form states
    var newTitle by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Study") }
    var newTime by remember { mutableStateOf("03:00 PM – 04:30 PM") }
    var newNotes by remember { mutableStateOf("") }

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size.coerceAtLeast(1)
    val progress = completedCount.toFloat() / totalCount

    val filteredTasks = when (selectedTab) {
        "Completed" -> tasks.filter { it.isCompleted }
        "Upcoming" -> tasks.filter { !it.isCompleted && it.scheduledTime.contains("PM") }
        else -> tasks
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreen,
                contentColor = PureWhite,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 60.dp).testTag("fab_add_task")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = WarmWhite
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(WarmWhite)
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                }
                Text(
                    text = "My Day & Tasks 📝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Task", tint = ForestGreen)
                }
            }

            // Stats Banner Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Daily Progress", fontSize = 12.sp, color = SecondaryText)
                            Text(text = "$completedCount of $totalCount Tasks Done", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        }
                        Text(text = "${(progress * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreen)
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = ForestGreen,
                        trackColor = SoftMint
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "• 1 Task in Focus", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
                        Text(text = "• ${totalCount - completedCount} Remaining", fontSize = 11.sp, color = SecondaryText)
                    }
                }
            }

            // Tab bar: Today, Upcoming, Completed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Today", "Upcoming", "Completed").forEach { tab ->
                    val isSel = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) ForestGreen else PureWhite)
                            .border(1.dp, if (isSel) ForestGreen else BorderColor, RoundedCornerShape(12.dp))
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) PureWhite else DarkNavy
                        )
                    }
                }
            }

            // Tasks List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTasks) { task ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(task) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                IconButton(
                                    onClick = { onToggleTaskComplete(task) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = "Toggle Complete",
                                        tint = if (task.isCompleted) ForestGreen else SecondaryText
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = task.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (task.isCompleted) SecondaryText else DarkNavy
                                    )
                                    Text(
                                        text = "${task.scheduledTime} • ${task.category}",
                                        fontSize = 11.sp,
                                        color = SecondaryText
                                    )
                                    if (task.notes.isNotBlank()) {
                                        Text(
                                            text = task.notes,
                                            fontSize = 11.sp,
                                            color = ForestGreen,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!task.isCompleted) {
                                    Button(
                                        onClick = { onStartFocus(task.title) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SoftMint, contentColor = ForestGreen),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "Focus", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteTask(task) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = SecondaryText.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Task 🎯", fontWeight = FontWeight.Bold, color = DarkNavy) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Task Title") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_task_title_input")
                    )

                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Category (e.g. Study, Work, Project)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("Scheduled Time") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newNotes,
                        onValueChange = { newNotes = it },
                        label = { Text("Notes / Goal") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            onAddTask(newTitle, newCategory, newTime, newNotes)
                            newTitle = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier.testTag("confirm_add_task_button")
                ) {
                    Text("Add Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }
}

@Composable
fun TaskDetailScreen(
    task: Task,
    onToggleComplete: () -> Unit,
    onStartFocus: () -> Unit,
    onBreakDownAi: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var subtasks by remember {
        mutableStateOf(
            if (task.subtasksJson.isNotBlank()) {
                task.subtasksJson.split(",").map { Pair(it.trim(), false) }
            } else {
                listOf(
                    Pair("Read requirements & outline goal", true),
                    Pair("Collect resources and documentation", false),
                    Pair("Write primary draft (25 min focus)", false),
                    Pair("Review and polish final output", false)
                )
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                }
                Text(
                    text = "Task Breakdown 🔍",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
                IconButton(onClick = onToggleComplete) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = "Toggle",
                        tint = ForestGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SoftMint)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = task.category.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }

                    Text(text = task.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                    Text(text = "Scheduled: ${task.scheduledTime}", fontSize = 12.sp, color = SecondaryText)
                    if (task.notes.isNotBlank()) {
                        Text(text = "Notes: ${task.notes}", fontSize = 12.sp, color = DarkNavy)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Micro-steps checklist
            Text(text = "Actionable Micro-Steps:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
            Spacer(modifier = Modifier.height(8.dp))

            subtasks.forEachIndexed { idx, (title, done) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PaleGreen)
                        .clickable {
                            subtasks = subtasks.mapIndexed { i, pair ->
                                if (i == idx) Pair(pair.first, !pair.second) else pair
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (done) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = null,
                        tint = if (done) ForestGreen else SecondaryText
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        color = if (done) SecondaryText else DarkNavy,
                        fontWeight = if (done) FontWeight.Normal else FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Bottom Actions
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SaathPrimaryButton(
                text = "Start 25m Focus Timer",
                onClick = onStartFocus,
                leadingIcon = Icons.Default.PlayArrow,
                testTag = "start_task_focus_button"
            )

            SaathSecondaryButton(
                text = "Break Down Further With Saathi",
                onClick = onBreakDownAi,
                leadingIcon = Icons.Default.AutoAwesome
            )
        }
    }
}
