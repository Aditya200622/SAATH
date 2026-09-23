package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.UserProfile
import com.example.data.model.WearableMetrics
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    tasks: List<Task>,
    wearableMetrics: WearableMetrics,
    latestMood: String = "Good",
    latestStress: Int = 4,
    saathiName: String = "Aarav",
    avatarId: String = "aarav",
    onNavigateToChat: () -> Unit,
    onNavigateToReset: () -> Unit,
    onNavigateToFocus: (String) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToCircle: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onToggleTaskComplete: (Task) -> Unit,
    onRunDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size.coerceAtLeast(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Top Header with Brand & Profile
        item {
            SaathHeader(
                userName = userProfile.fullName.split(" ").firstOrNull() ?: "Aditya",
                onAvatarClick = onNavigateToProfile,
                onNotificationClick = onNavigateToChat,
                onSearchClick = onNavigateToTasks
            )
        }

        // 2. Demo Mode Quick Bar (One-tap 90-second showcase)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SoftMint),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ForestGreen.copy(alpha = 0.3f))),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clickable { onRunDemo() }
                    .testTag("demo_mode_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Try 90-Sec Product Flow", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                            Text(text = "Overwhelm → Reset → Task Breakdown → Focus", fontSize = 10.sp, color = ForestGreen)
                        }
                    }
                    Text(text = "Start ›", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                }
            }
        }

        // 3. Companion Greeting Banner
        item {
            CompanionGreetingBanner(
                userName = userProfile.fullName.split(" ").firstOrNull() ?: "Aditya",
                greeting = "Good Morning",
                subGreeting = "A new day, a new opportunity to be a better you.",
                saathiQuote = "“Small steps every day lead to big changes.”",
                avatarId = avatarId,
                saathiName = saathiName,
                onAvatarClick = onNavigateToChat
            )
        }

        // 4. Wellbeing Metrics Grid: Mood, Stress Level, Sleep, Steps
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Mood",
                    value = latestMood,
                    icon = Icons.Default.SentimentSatisfied,
                    iconColor = MoodGood,
                    bgColor = PureWhite,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToReset
                )
                MetricCard(
                    title = "Stress Level",
                    value = "$latestStress / 10",
                    icon = Icons.Default.Favorite,
                    iconColor = ForestGreen,
                    bgColor = PureWhite,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToReset
                )
                MetricCard(
                    title = "Sleep",
                    value = "${wearableMetrics.sleepDurationMinutes / 60}h ${wearableMetrics.sleepDurationMinutes % 60}m",
                    icon = Icons.Default.Bedtime,
                    iconColor = AccentBlue,
                    bgColor = PureWhite,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToReset
                )
                MetricCard(
                    title = "Steps",
                    value = "${wearableMetrics.steps}",
                    icon = Icons.Default.DirectionsWalk,
                    iconColor = ForestGreen,
                    bgColor = PureWhite,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTasks
                )
            }
        }

        // 5. Today's Plan Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Today’s Plan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkNavy
                            )
                        }
                        Text(
                            text = "Tue, 23 Sep 2026",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Task List Items
                    tasks.take(5).forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PaleGreen)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                IconButton(
                                    onClick = { onToggleTaskComplete(task) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                        contentDescription = "Toggle Complete",
                                        tint = if (task.isCompleted) ForestGreen else SecondaryText
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = task.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (task.isCompleted) SecondaryText else DarkNavy
                                    )
                                    Text(
                                        text = "${task.scheduledTime.split("–").firstOrNull()?.trim() ?: ""} • ${task.notes}",
                                        fontSize = 11.sp,
                                        color = SecondaryText
                                    )
                                }
                            }

                            if (task.isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SoftMint)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "Completed", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onNavigateToFocus(task.title) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PureWhite, contentColor = ForestGreen),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ForestGreen.copy(alpha = 0.5f))),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(text = "Start", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // View All & Add Task
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View All Tasks ›",
                            fontSize = 12.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToTasks() }
                        )

                        Text(
                            text = "+ Add Task",
                            fontSize = 12.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToTasks() }
                        )
                    }
                }
            }
        }

        // 6. Quick Actions Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                Text(
                    text = "Quick Actions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
                Text(
                    text = "Whenever you need a hand.",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Talk to Saathi
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PureWhite)
                            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                            .clickable { onNavigateToChat() }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubble, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Talk to Saathi", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkNavy)
                        }
                    }

                    // 2 Min Reset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PureWhite)
                            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                            .clickable { onNavigateToReset() }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "2 Min Reset", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkNavy)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Add Task
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PureWhite)
                            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                            .clickable { onNavigateToTasks() }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Add Task", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkNavy)
                        }
                    }

                    // Reach Circle
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PureWhite)
                            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                            .clickable { onNavigateToCircle() }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Reach Circle", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = DarkNavy)
                        }
                    }
                }
            }
        }

        // 7. Saathi Reminder Banner
        item {
            QuoteBanner(
                quote = "Progress, not perfection.",
                subtext = "You're doing great! Keep showing up.",
                onClick = onNavigateToChat,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}
