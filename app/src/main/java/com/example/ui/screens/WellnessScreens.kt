package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SaathAvatar
import com.example.ui.components.SaathPrimaryButton
import com.example.ui.components.SaathSecondaryButton
import com.example.ui.theme.*

@Composable
fun ResetExerciseScreen(
    currentExerciseTitle: String,
    exercisePhase: String,
    exerciseSecondsLeft: Int,
    exerciseCycle: Int,
    isExerciseRunning: Boolean,
    onStartExercise: (String) -> Unit,
    onStopExercise: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExercise by remember { mutableStateOf("4-7-8 Breathing") }
    var afterFeeling by remember { mutableStateOf<String?>(null) }

    val circleScale by animateFloatAsState(
        targetValue = when (exercisePhase) {
            "Inhale" -> 1.35f
            "Hold" -> 1.35f
            "Exhale" -> 0.9f
            else -> 1.0f
        },
        animationSpec = tween(
            durationMillis = when (exercisePhase) {
                "Inhale" -> 4000
                "Hold" -> 7000
                "Exhale" -> 8000
                else -> 1000
            },
            easing = LinearEasing
        ),
        label = "breathScale"
    )

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
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
            }
            Text(
                text = "2-Minute Calming Reset 🌿",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Exercise Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val exercises = listOf("4-7-8 Breathing", "5-4-3-2-1 Grounding", "Box Breathing", "Quick Relax")
            items(exercises) { ex ->
                val isSelected = selectedExercise == ex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) ForestGreen else PureWhite)
                        .border(1.dp, if (isSelected) ForestGreen else BorderColor, RoundedCornerShape(16.dp))
                        .clickable {
                            selectedExercise = ex
                            if (isExerciseRunning) onStopExercise()
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = ex,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PureWhite else DarkNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Animated Breathing Ring
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(circleScale),
            contentAlignment = Alignment.Center
        ) {
            // Background glow circle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ForestGreen.copy(alpha = 0.25f), PaleGreen.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
            )

            // Inner circle
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .border(3.dp, ForestGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isExerciseRunning) exercisePhase else "Ready",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isExerciseRunning) "${exerciseSecondsLeft}s" else "Tap Start",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                    if (isExerciseRunning) {
                        Text(
                            text = "Cycle $exerciseCycle of 4",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Controls: Start / Pause
        if (!isExerciseRunning) {
            SaathPrimaryButton(
                text = "Begin $selectedExercise",
                onClick = { onStartExercise(selectedExercise) },
                leadingIcon = Icons.Default.PlayArrow,
                testTag = "start_exercise_button"
            )
        } else {
            SaathSecondaryButton(
                text = "Pause Exercise",
                onClick = onStopExercise,
                leadingIcon = Icons.Default.Pause,
                testTag = "pause_exercise_button"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Post-exercise reflection
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "How do you feel right now?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Calmer 😌", "Better 🙂", "Same 😐").forEach { feel ->
                        val sel = afterFeeling == feel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (sel) SoftMint else PaleGreen)
                                .border(1.dp, if (sel) ForestGreen else BorderLight, RoundedCornerShape(12.dp))
                                .clickable { afterFeeling = feel }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = feel,
                                fontSize = 12.sp,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                                color = if (sel) ForestGreen else DarkNavy
                            )
                        }
                    }
                }

                if (afterFeeling != null) {
                    Text(
                        text = "Saathi: Proud of you for taking this 2-minute pause. Let's return to your day with clarity.",
                        fontSize = 11.sp,
                        color = ForestGreen,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FocusSessionScreen(
    taskTitle: String,
    secondsLeft: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSound by remember { mutableStateOf("Forest Rain") }
    var soundPlaying by remember { mutableStateOf(false) }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val progress = (totalSeconds - secondsLeft).toFloat() / totalSeconds.coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
            }
            Text(
                text = "Focus Mode 🎯",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
            }
        }

        // Active Task Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoftMint),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(PureWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CenterFocusStrong, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Current Focus Task", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
                    Text(text = taskTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                }
            }
        }

        // Circular Timer Canvas
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                // Track
                drawCircle(
                    color = BorderColor,
                    style = Stroke(strokeWidth)
                )
                // Progress
                drawArc(
                    color = ForestGreen,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkNavy
                )
                Text(
                    text = if (isRunning) "Deep Focus" else "Paused",
                    fontSize = 13.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Ambient Sound Selector
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ambient Sound: $selectedSound", fontSize = 12.sp, color = DarkNavy, fontWeight = FontWeight.Medium)
                }
                Switch(
                    checked = soundPlaying,
                    onCheckedChange = { soundPlaying = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = SoftMint)
                )
            }
        }

        // Play / Pause / Reset Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = SecondaryText)
            }

            Spacer(modifier = Modifier.width(24.dp))

            IconButton(
                onClick = { if (isRunning) onPause() else onPlay() },
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(ForestGreen)
                    .testTag("toggle_focus_timer")
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Play",
                    tint = PureWhite,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Complete Early", tint = ForestGreen)
            }
        }
    }
}

@Composable
fun JournalScreen(
    onSaveJournal: (mood: String, helpedTags: List<String>, text: String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMood by remember { mutableStateOf("Better") }
    var selectedTags by remember { mutableStateOf(setOf("Breathing", "Completing a task")) }
    var journalText by remember { mutableStateOf("") }
    var savedSuccess by remember { mutableStateOf(false) }

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
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
            }
            Text(
                text = "Daily Reflection 📖",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Date & Companion Message
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoftMint),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                SaathiAvatar(size = 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Evening Check-in • Tue, 23 Sep", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Writing down what happened helps your brain process the day and sleep deeper.",
                        fontSize = 12.sp,
                        color = DarkNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Question 1: How was your day?
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(text = "How was your overall day?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Better 🙂", "Okay 😐", "Same 😶", "Hard 😫").forEach { mood ->
                        val sel = selectedMood == mood.split(" ").first()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (sel) SoftMint else PaleGreen)
                                .border(1.5.dp, if (sel) ForestGreen else BorderLight, RoundedCornerShape(12.dp))
                                .clickable { selectedMood = mood.split(" ").first() }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mood,
                                fontSize = 11.sp,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium,
                                color = if (sel) ForestGreen else DarkNavy
                            )
                        }
                    }
                }

                // Question 2: What helped today?
                Text(text = "What helped you feel better today?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                val tags = listOf(
                    "Walking", "Breathing", "Talking to someone",
                    "Completing a task", "Taking a break", "Listening to music"
                )

                tags.chunked(2).forEach { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunk.forEach { tag ->
                            val sel = selectedTags.contains(tag)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (sel) SoftMint else PureWhite)
                                    .border(1.dp, if (sel) ForestGreen else BorderColor, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedTags = if (sel) selectedTags - tag else selectedTags + tag
                                    }
                                    .padding(vertical = 8.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    color = if (sel) ForestGreen else DarkNavy,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Reflection Editor
                Text(text = "Write your thoughts freely (Private & Encrypted)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                OutlinedTextField(
                    value = journalText,
                    onValueChange = { journalText = it },
                    placeholder = { Text("What made you smile today? What was challenging? You are safe here...") },
                    minLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("journal_text_input")
                )

                SaathPrimaryButton(
                    text = "Save Reflection",
                    onClick = {
                        onSaveJournal(selectedMood, selectedTags.toList(), journalText)
                        savedSuccess = true
                    },
                    testTag = "save_journal_button"
                )

                if (savedSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftMint)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "✨ Reflection saved! Saathi: Proud of you for showing up today.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }
            }
        }
    }
}
