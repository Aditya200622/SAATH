package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SaathiConfig
import com.example.data.model.UserProfile
import com.example.data.model.WearableMetrics
import com.example.ui.components.SaathiAvatar
import com.example.ui.components.SaathPrimaryButton
import com.example.ui.components.SaathSecondaryButton
import com.example.ui.theme.*

@Composable
fun SafetyScreen(
    onNavigateBack: () -> Unit,
    onStartGrounding: () -> Unit,
    onReachCircle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                text = "Safety & Crisis Support 🛡️",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Empathy Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftMint),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "You are not alone. Help is here.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                        }
                        Text(
                            text = "If you or someone you know is feeling overwhelmed, hopeless, or thinking about self-harm, please connect with a trained professional right now. Support is free, confidential, and 24/7.",
                            fontSize = 12.sp,
                            color = ForestGreen,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Quick Actions: Grounding & Trusted Circle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onStartGrounding,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "2-Min Grounding", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReachCircle,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Call Trusted Circle", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Text(text = "Verified 24/7 National Helplines", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
            }

            // Helpline 1: Tele-MANAS
            val helplines = listOf(
                Triple("Tele-MANAS (Govt of India)", "Free 24x7 National Mental Health Service • 20+ Languages", "14416"),
                Triple("KIRAN Mental Health", "Toll-Free 24x7 Ministry of Social Justice Helpline", "1800-599-0019"),
                Triple("Vandrevala Foundation", "Free 24x7 Professional Crisis Intervention & Counseling", "9999666555"),
                Triple("AASRA", "24/7 Confidential Suicide Prevention & Distress Helpline", "9820466726"),
                Triple("National Emergency Number", "Emergency Police / Medical Services (Pan-India)", "112")
            )

            items(helplines) { (name, desc, number) ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                            Text(text = desc, fontSize = 11.sp, color = SecondaryText, lineHeight = 14.sp)
                            Text(text = "Dial: $number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen, modifier = Modifier.padding(top = 4.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                try {
                                    val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                                    context.startActivity(dial)
                                } catch (e: Exception) {}
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = PureWhite, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    userProfile: UserProfile,
    saathiConfig: SaathiConfig,
    wearableMetrics: WearableMetrics,
    onSyncWearable: () -> Unit,
    onToggleWearable: () -> Unit,
    onResetDemoData: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var morningNudge by remember { mutableStateOf(true) }
    var eveningReflection by remember { mutableStateOf(true) }
    var quietHours by remember { mutableStateOf(true) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
    ) {
        // Top Navigation
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
                text = "Settings & Profile ⚙️",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.fullName.firstOrNull()?.uppercase() ?: "A",
                                color = PureWhite,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = userProfile.fullName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                            Text(text = userProfile.email, fontSize = 12.sp, color = SecondaryText)
                            Text(text = userProfile.location, fontSize = 11.sp, color = ForestGreen, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }

            // Companion Settings Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Your AI Saathi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SaathiAvatar(avatarId = saathiConfig.avatarId, size = 44.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "${saathiConfig.name} • ${saathiConfig.personality}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                Text(text = "Voice: ${saathiConfig.voice} | Language: ${saathiConfig.language}", fontSize = 11.sp, color = SecondaryText)
                            }
                        }
                    }
                }
            }

            // Wearable Device Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Connected Health Device", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SoftMint), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Watch, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = wearableMetrics.deviceName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                    Text(
                                        text = if (wearableMetrics.isConnected) "Connected • Synced just now" else "Disconnected",
                                        fontSize = 11.sp,
                                        color = if (wearableMetrics.isConnected) ForestGreen else SecondaryText
                                    )
                                }
                            }
                            Switch(
                                checked = wearableMetrics.isConnected,
                                onCheckedChange = { onToggleWearable() },
                                colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = SoftMint)
                            )
                        }

                        Button(
                            onClick = onSyncWearable,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftMint, contentColor = ForestGreen),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Latest Biometrics (HR: ${wearableMetrics.heartRate} bpm, Steps: ${wearableMetrics.steps})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Notification Nudges & Quiet Hours
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "Nudges & Quiet Hours", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Morning Planning Nudge", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarkNavy)
                                Text(text = "Prompt to check in and sequence your day (08:30 AM)", fontSize = 11.sp, color = SecondaryText)
                            }
                            Switch(checked = morningNudge, onCheckedChange = { morningNudge = it }, colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = SoftMint))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Evening Reflection Nudge", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarkNavy)
                                Text(text = "Wind-down check-in & gratitude prompt (09:00 PM)", fontSize = 11.sp, color = SecondaryText)
                            }
                            Switch(checked = eveningReflection, onCheckedChange = { eveningReflection = it }, colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = SoftMint))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Quiet Hours (Do Not Disturb)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarkNavy)
                                Text(text = "No non-urgent notifications between 10:30 PM – 07:30 AM", fontSize = 11.sp, color = SecondaryText)
                            }
                            Switch(checked = quietHours, onCheckedChange = { quietHours = it }, colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = SoftMint))
                        }
                    }
                }
            }

            // Data & Demo Management
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Demo & Data Controls", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = ForestGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset All Data to Demo Prototype", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Initial Demo?") },
            text = { Text("This will restore Aditya Mishra's initial schedule, check-in, contacts, and chat logs.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text("Reset Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }
}
