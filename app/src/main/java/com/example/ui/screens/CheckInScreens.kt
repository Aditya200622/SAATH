package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.components.SaathAvatar
import com.example.ui.components.SaathPrimaryButton
import com.example.ui.components.SaathSecondaryButton
import com.example.ui.theme.*

@Composable
fun MorningCheckInScreen(
    userName: String = "Aditya",
    avatarId: String = "aarav",
    onSubmitCheckIn: (mood: String, stress: Int, priorities: List<String>, notes: String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf("Good") }
    var stressLevel by remember { mutableFloatStateOf(4f) }
    var selectedPriorities by remember {
        mutableStateOf(setOf("College", "Study", "Assignment", "Exercise", "Project"))
    }
    var notesText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSkip) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "SAATH", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreen)
                Spacer(modifier = Modifier.width(3.dp))
                Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
            }
            Text(
                text = "Skip",
                fontSize = 13.sp,
                color = SecondaryText,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSkip() }.testTag("checkin_skip_button")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Header Greeting & Companion
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Good Morning,\n$userName! ☀️",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "How are you feeling today? A small check-in helps me plan a better day for you.",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    lineHeight = 16.sp
                )
            }
            SaathiAvatar(avatarId = avatarId, size = 80.dp)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Question 1: How are you feeling today?
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "1. How are you feeling today?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )

                // 5 Mood Cards
                val moods = listOf(
                    Triple("Great", "😄", MoodGreat),
                    Triple("Good", "🙂", MoodGood),
                    Triple("Okay", "😐", MoodOkay),
                    Triple("Low", "🙁", MoodLow),
                    Triple("Stressed", "😫", MoodStressed)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.forEach { (mood, emoji, color) ->
                        val isSelected = selectedMood == mood
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) SoftMint else PaleGreen)
                                .border(1.5.dp, if (isSelected) ForestGreen else BorderLight, RoundedCornerShape(14.dp))
                                .clickable { selectedMood = mood }
                                .padding(vertical = 10.dp)
                                .testTag("mood_$mood")
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mood,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ForestGreen else DarkNavy
                            )
                        }
                    }
                }

                // Question 2: What's your stress level right now?
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. What’s your stress level right now?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy
                        )
                        Text(
                            text = "${stressLevel.toInt()} / 10",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (stressLevel >= 7) AccentRed else ForestGreen
                        )
                    }
                    Slider(
                        value = stressLevel,
                        onValueChange = { stressLevel = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = ForestGreen,
                            activeTrackColor = ForestGreen,
                            inactiveTrackColor = BorderColor
                        ),
                        modifier = Modifier.testTag("stress_slider")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "No stress", fontSize = 11.sp, color = SecondaryText)
                        Text(text = "Extreme stress", fontSize = 11.sp, color = SecondaryText)
                    }
                }

                // Question 3: What are your key priorities today?
                Column {
                    Text(
                        text = "3. What are your key priorities today?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                    Text(
                        text = "Select as many as you like",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val prioritiesList = listOf(
                        Pair("College", Icons.Default.School),
                        Pair("Study", Icons.Default.MenuBook),
                        Pair("Assignment", Icons.Default.Assignment),
                        Pair("Work", Icons.Default.Work),
                        Pair("Exercise", Icons.Default.FitnessCenter),
                        Pair("Family", Icons.Default.Groups),
                        Pair("Personal", Icons.Default.FavoriteBorder),
                        Pair("Project", Icons.Default.Computer),
                        Pair("Social", Icons.Default.People),
                        Pair("Self-care", Icons.Default.Spa),
                        Pair("Other", Icons.Default.MoreHoriz)
                    )

                    prioritiesList.chunked(3).forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            chunk.forEach { (name, icon) ->
                                val sel = selectedPriorities.contains(name)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) SoftMint else PureWhite)
                                        .border(1.dp, if (sel) ForestGreen else BorderColor, RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedPriorities = if (sel) selectedPriorities - name else selectedPriorities + name
                                        }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(icon, contentDescription = null, tint = if (sel) ForestGreen else SecondaryText, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = name, fontSize = 11.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, color = if (sel) ForestGreen else DarkNavy)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                // Question 4: Anything on your mind? (Optional)
                Column {
                    Text(
                        text = "4. Anything on your mind? (Optional)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        placeholder = { Text("You can share anything...") },
                        trailingIcon = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice input", tint = ForestGreen)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkin_notes_input")
                    )
                }

                // Submit Button
                SaathPrimaryButton(
                    text = "Next",
                    onClick = {
                        onSubmitCheckIn(
                            selectedMood,
                            stressLevel.toInt(),
                            selectedPriorities.toList(),
                            notesText
                        )
                    },
                    testTag = "checkin_submit_button"
                )
            }
        }
    }
}

@Composable
fun DailyPlanScreen(
    generatedTasks: List<Task>,
    onAcceptPlan: (List<Task>) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var taskList by remember { mutableStateOf(generatedTasks) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateToHome) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
            }
            Text(
                text = "Your AI Daily Plan 📅",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )
            IconButton(onClick = onNavigateToHome) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Wellbeing Context Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SoftMint)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SaathiAvatar(size = 44.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Tailored to your current energy & priorities",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                    Text(
                        text = "Tasks are sequenced with resting buffers so you stay calm and focused.",
                        fontSize = 11.sp,
                        color = ForestGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timeline Tasks List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            taskList.forEachIndexed { index, task ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PaleGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (task.category.lowercase()) {
                                        "college" -> Icons.Default.School
                                        "study" -> Icons.Default.MenuBook
                                        "assignment" -> Icons.Default.Assignment
                                        "workout" -> Icons.Default.FitnessCenter
                                        else -> Icons.Default.Computer
                                    },
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkNavy
                                )
                                Text(
                                    text = "${task.scheduledTime} • ${task.notes}",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        // Remove / Move
                        IconButton(
                            onClick = { taskList = taskList.filterIndexed { i, _ -> i != index } }
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = SecondaryText, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        SaathPrimaryButton(
            text = "Accept & Start My Day",
            onClick = {
                onAcceptPlan(taskList)
                onNavigateToHome()
            },
            testTag = "accept_daily_plan_button"
        )

        Spacer(modifier = Modifier.height(10.dp))

        SaathSecondaryButton(
            text = "Add Custom Task",
            onClick = {
                taskList = taskList + Task(
                    title = "Afternoon Focus",
                    category = "Personal",
                    scheduledTime = "04:30 PM – 05:30 PM",
                    durationMinutes = 60,
                    notes = "Self-directed work"
                )
            },
            leadingIcon = Icons.Default.Add
        )
    }
}
