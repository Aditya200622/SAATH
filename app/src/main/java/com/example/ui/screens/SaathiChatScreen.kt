package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.ChatMessage
import com.example.data.model.SaathiConfig
import com.example.ui.components.AvatarMood
import com.example.ui.components.SaathiAvatar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SaathiChatScreen(
    saathiConfig: SaathiConfig,
    messages: List<ChatMessage>,
    isAiThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onStartReset: () -> Unit,
    onStartFocus: (String) -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToCircle: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .imePadding()
    ) {
        // 1. Companion Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                }
                Spacer(modifier = Modifier.width(4.dp))
                SaathiAvatar(
                    avatarId = saathiConfig.avatarId,
                    size = 40.dp,
                    mood = if (isAiThinking) AvatarMood.THINKING else AvatarMood.LISTENING
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = saathiConfig.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MoodGreat)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAiThinking) "Thinking..." else "Always here with you",
                            fontSize = 11.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = onNavigateToEmergency) {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = "Safety Support", tint = AccentRed)
                }
                IconButton(onClick = onStartReset) {
                    Icon(Icons.Default.Spa, contentDescription = "Reset Exercise", tint = ForestGreen)
                }
            }
        }

        HorizontalDivider(color = BorderLight)

        // 2. Chat Messages Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    if (!isUser) {
                        SaathiAvatar(
                            avatarId = saathiConfig.avatarId,
                            size = 32.dp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                        modifier = Modifier.widthIn(max = 280.dp),
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .background(if (isUser) ForestGreen else PureWhite)
                                .border(
                                    1.dp,
                                    if (isUser) ForestGreen else BorderColor,
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg.text,
                                fontSize = 14.sp,
                                color = if (isUser) PureWhite else DarkNavy,
                                lineHeight = 19.sp
                            )
                        }

                        // Suggestion Chips attached to Saathi's message
                        if (!isUser && msg.suggestionChipsJson.isNotBlank()) {
                            val chips = msg.suggestionChipsJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                chips.forEach { chip ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(PaleGreen)
                                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                            .clickable { onSendMessage(chip) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = chip,
                                            fontSize = 12.sp,
                                            color = ForestGreen,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Interactive Action Card attached to message
                        if (!isUser && msg.actionType.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SoftMint),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            when (msg.actionType) {
                                                "FOCUS" -> onStartFocus(msg.actionPayload.ifEmpty { "Focus Session" })
                                                "RESET" -> onStartReset()
                                                "BREAK_TASK" -> onNavigateToTasks()
                                                "EMERGENCY_SUPPORT" -> onNavigateToEmergency()
                                                else -> onStartFocus("Assignment")
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Text(
                                            text = when (msg.actionType) {
                                                "FOCUS" -> "Start Focus Timer"
                                                "RESET" -> "Start 2m Reset"
                                                "BREAK_TASK" -> "Break into Tasks"
                                                "EMERGENCY_SUPPORT" -> "View Helplines"
                                                else -> "Take Action"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = onStartReset,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("2m Reset", fontSize = 11.sp, color = ForestGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isAiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 40.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ForestGreen,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${saathiConfig.name} is typing...",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                }
            }
        }

        // 3. Quick Action Suggestion Chips above input bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val quickChips = listOf(
                Pair("2 Min Reset", Icons.Default.Spa),
                Pair("Add Task", Icons.Default.Add),
                Pair("How am I feeling?", Icons.Default.SentimentSatisfied),
                Pair("Give me a tip", Icons.Default.Lightbulb)
            )

            items(quickChips) { (label, icon) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PureWhite)
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                        .clickable {
                            if (label == "2 Min Reset") {
                                onStartReset()
                            } else {
                                onSendMessage(label)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = label, fontSize = 12.sp, color = DarkNavy, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // 4. Input Text Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhite)
                .border(1.dp, BorderLight)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onSendMessage("I'm feeling a bit anxious about my deadlines today.") },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = ForestGreen)
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Type a message...", fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreen,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = PaleGreen,
                    unfocusedContainerColor = PaleGreen
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("chat_input_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val text = inputText
                        inputText = ""
                        onSendMessage(text)
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ForestGreen)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
