package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricCard
import com.example.ui.theme.*

@Composable
fun InsightsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("This Week") }
    var selectedSubtab by remember { mutableStateOf("Overview") }

    Column(
        modifier = modifier
            .fillMaxSize()
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
                text = "Insights & Analytics 📊",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )

            // Timeframe badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftMint)
                    .clickable {
                        selectedTimeframe = if (selectedTimeframe == "This Week") "This Month" else "This Week"
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = selectedTimeframe, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            }
        }

        // Subtabs: Overview, Mood, Productivity, Wellbeing, Journal
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subtabs = listOf("Overview", "Mood", "Productivity", "Wellbeing", "Journal")
            items(subtabs) { tab ->
                val isSel = selectedSubtab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) ForestGreen else PureWhite)
                        .border(1.dp, if (isSel) ForestGreen else BorderColor, RoundedCornerShape(12.dp))
                        .clickable { selectedSubtab = tab }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 4 Metrics Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Avg Mood",
                        value = "Good 🙂",
                        icon = Icons.Default.SentimentSatisfied,
                        iconColor = MoodGood,
                        bgColor = PureWhite,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Tasks Done",
                        value = "18 / 24",
                        icon = Icons.Default.CheckCircle,
                        iconColor = ForestGreen,
                        bgColor = PureWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Focus Time",
                        value = "4.5 hrs",
                        icon = Icons.Default.Timer,
                        iconColor = AccentBlue,
                        bgColor = PureWhite,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Streak",
                        value = "5 Days 🔥",
                        icon = Icons.Default.LocalFireDepartment,
                        iconColor = AccentAmber,
                        bgColor = PureWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Mood Trend Chart Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
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
                            Text(text = "Weekly Mood Trend", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                            Text(text = "Stable & Improving 📈", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                        }

                        // Custom Canvas Line Graph
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .padding(vertical = 10.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Draw horizontal grid lines
                                for (i in 0..3) {
                                    val y = h * (i / 3f)
                                    drawLine(
                                        color = BorderLight,
                                        start = Offset(0f, y),
                                        end = Offset(w, y),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }

                                // 7 data points (Mon to Sun) normalized
                                val points = listOf(0.6f, 0.4f, 0.7f, 0.8f, 0.65f, 0.75f, 0.85f)
                                val stepX = w / (points.size - 1)

                                val path = Path()
                                points.forEachIndexed { idx, value ->
                                    val x = idx * stepX
                                    val y = h - (value * h * 0.8f) - (h * 0.1f)
                                    if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                    // Point circle
                                    drawCircle(color = ForestGreen, radius = 4.dp.toPx(), center = Offset(x, y))
                                    drawCircle(color = PureWhite, radius = 2.dp.toPx(), center = Offset(x, y))
                                }

                                drawPath(
                                    path = path,
                                    color = ForestGreen,
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                                Text(text = day, fontSize = 10.sp, color = SecondaryText)
                            }
                        }
                    }
                }
            }

            // Task Completion & Focus Distribution
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "Task Completion & Focus", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Donut Chart
                            Box(
                                modifier = Modifier.size(90.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val stroke = 10.dp.toPx()
                                    drawCircle(color = SoftMint, style = Stroke(stroke))
                                    drawArc(
                                        color = ForestGreen,
                                        startAngle = -90f,
                                        sweepAngle = 360f * 0.75f,
                                        useCenter = false,
                                        style = Stroke(stroke, cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "75%", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = DarkNavy)
                                }
                            }

                            Spacer(modifier = Modifier.width(18.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "18 Completed / 6 Remaining", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                Text(text = "Highest focus time: College & Study blocks", fontSize = 11.sp, color = SecondaryText)
                                Text(text = "Most productive hours: 10:00 AM – 01:00 PM", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Wellbeing Dimensions
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "Wellbeing Dimensions Score", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                        listOf(
                            Triple("Mental Calm & Rest", 0.82f, ForestGreen),
                            Triple("Routine Consistency", 0.78f, AccentBlue),
                            Triple("Physical Activity (Steps)", 0.65f, AccentAmber),
                            Triple("Social & Trusted Circle", 0.70f, AccentPurple)
                        ).forEach { (dim, score, color) ->
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = dim, fontSize = 12.sp, color = DarkNavy, fontWeight = FontWeight.Medium)
                                    Text(text = "${(score * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { score },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                    color = color,
                                    trackColor = BorderLight
                                )
                            }
                        }
                    }
                }
            }

            // Achievements Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Recent Milestones 🏆", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SoftMint)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(text = "🔥 5-Day Streak", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                                Text(text = "Consistent daily morning check-ins", fontSize = 10.sp, color = ForestGreen)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PaleGreen)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(text = "🧘 Mindful Breather", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                                Text(text = "Completed 4 calming resets", fontSize = 10.sp, color = ForestGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}
