package com.example.ui.screens

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
import com.example.ui.components.SaathAvatar
import com.example.ui.components.SaathPrimaryButton
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: (name: String, age: String, goals: String, saathiName: String, avatar: String, voice: String, personality: String, lang: String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    // User details state
    var preferredName by remember { mutableStateOf("Aditya Mishra") }
    var selectedAgeGroup by remember { mutableStateOf("18 – 24") }
    var selectedPriorities by remember { mutableStateOf(setOf("Study", "Routine", "Stress", "Mental Wellbeing")) }
    var additionalNotes by remember { mutableStateOf("") }

    // Saathi configuration state
    var selectedAvatarId by remember { mutableStateOf("aarav") }
    var saathiName by remember { mutableStateOf("Aarav") }
    var selectedVoice by remember { mutableStateOf("Male") }
    var selectedPersonality by remember { mutableStateOf("Friendly") }
    var selectedLanguage by remember { mutableStateOf("English") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation & Step Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (step > 1) step-- else onSkip() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
            }

            // Brand Logo in center
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "SAATH", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreen)
                Spacer(modifier = Modifier.width(3.dp))
                Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
            }

            Text(
                text = "Skip",
                fontSize = 13.sp,
                color = SecondaryText,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSkip() }.testTag("onboarding_skip_button")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$step / 4", fontSize = 11.sp, color = SecondaryText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { step / 4f },
                modifier = Modifier.width(120.dp).height(4.dp).clip(CircleShape),
                color = ForestGreen,
                trackColor = BorderColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (step == 1) {
            // STEP 1: Let's get to know you better
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Let's get to know\nyou better", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This helps Saath understand you and support you better.",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PaleGreen)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Hi! 👋 I'm Saath. Let's take this journey together 💚",
                            fontSize = 11.sp,
                            color = DeepGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                SaathiAvatar(avatarId = selectedAvatarId, size = 80.dp)
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    // Question 1: Name
                    Column {
                        Text(text = "What should I call you?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = preferredName,
                            onValueChange = { preferredName = it },
                            placeholder = { Text("Your name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SecondaryText) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("onboarding_name_input")
                        )
                    }

                    // Question 2: Age group
                    Column {
                        Text(text = "What's your age group?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Below 18", "18 – 24", "25 – 34", "35+").forEach { age ->
                                val selected = selectedAgeGroup == age
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) SoftMint else PureWhite)
                                        .border(1.5.dp, if (selected) ForestGreen else BorderColor, RoundedCornerShape(12.dp))
                                        .clickable { selectedAgeGroup = age }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (selected) ForestGreen else SecondaryText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = age,
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selected) ForestGreen else DarkNavy
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Question 3: What do you want Saath to help with?
                    Column {
                        Text(text = "What do you want Saath to help you with?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Text(text = "You can select multiple options", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(8.dp))

                        val options = listOf(
                            Pair("Study", Icons.Default.School),
                            Pair("Work", Icons.Default.Work),
                            Pair("Routine", Icons.Default.CalendarToday),
                            Pair("Fitness", Icons.Default.FitnessCenter),
                            Pair("Relationships", Icons.Default.Favorite),
                            Pair("Stress", Icons.Default.Spa),
                            Pair("Mental Wellbeing", Icons.Default.Psychology),
                            Pair("General Growth", Icons.Default.TrendingUp)
                        )

                        // 4x2 grid of selectable pills
                        options.chunked(4).forEach { rowList ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowList.forEach { (title, icon) ->
                                    val isSelected = selectedPriorities.contains(title)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) SoftMint else PureWhite)
                                            .border(1.5.dp, if (isSelected) ForestGreen else BorderColor, RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedPriorities = if (isSelected) selectedPriorities - title else selectedPriorities + title
                                            }
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = title,
                                                tint = if (isSelected) ForestGreen else SecondaryText,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = title,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) ForestGreen else DarkNavy
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Anything else? (Optional)
                    Column {
                        Text(text = "Anything else? (Optional)", fontSize = 12.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = additionalNotes,
                            onValueChange = { additionalNotes = it },
                            placeholder = { Text("e.g. I want to be more consistent, handle stress better...") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    SaathPrimaryButton(
                        text = "Next",
                        onClick = { step = 2 },
                        testTag = "onboarding_step1_next"
                    )
                }
            }
        } else if (step == 2) {
            // STEP 2: Goals & Habits
            Text(text = "Your Primary Goals 🎯", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
            Text(text = "Choose 3-5 focus areas to anchor your daily reflections.", fontSize = 12.sp, color = SecondaryText)
            Spacer(modifier = Modifier.height(16.dp))

            val goalsList = listOf(
                "Build 5 meaningful projects",
                "Stay fit and healthy (active 30 mins/day)",
                "Learn something new every day",
                "Better sleep & wind-down routine",
                "Manage academic or work stress peacefully",
                "Stay close to family and friends"
            )

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
                    goalsList.forEach { goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PaleGreen)
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = goal, fontSize = 13.sp, color = DarkNavy, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    SaathPrimaryButton(
                        text = "Next",
                        onClick = { step = 3 }
                    )
                }
            }
        } else if (step == 3) {
            // STEP 3: Trusted Circle preview
            Text(text = "Your Support Circle 🤝", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
            Text(text = "Connection without surveillance. SAATH keeps your journal private while making it easy to reach people who care.", fontSize = 12.sp, color = SecondaryText)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(
                        Triple("Mom", "Family", Icons.Default.Favorite),
                        Triple("Dad", "Family", Icons.Default.Security),
                        Triple("Best Friend", "Friend", Icons.Default.People)
                    ).forEach { (rel, label, icon) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(PaleGreen)
                                .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(SoftMint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = rel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                    Text(text = label, fontSize = 11.sp, color = SecondaryText)
                                }
                            }
                            Text(text = "Added", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    SaathPrimaryButton(
                        text = "Next: Meet Your Saathi",
                        onClick = { step = 4 }
                    )
                }
            }
        } else {
            // STEP 4: Meet Your Saathi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Meet your Saathi", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Spa, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "Choose a companion who feels right for you. You can always change this later.",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
                SaathiAvatar(avatarId = selectedAvatarId, size = 80.dp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Companion Choices: Aarav, Mira, Kian, Nova
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Pair("aarav", "Aarav"),
                    Pair("mira", "Mira"),
                    Pair("kian", "Kian"),
                    Pair("nova", "Nova")
                ).forEach { (id, name) ->
                    val isSelected = selectedAvatarId == id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SoftMint else PureWhite)
                            .border(2.dp, if (isSelected) ForestGreen else BorderColor, RoundedCornerShape(14.dp))
                            .clickable {
                                selectedAvatarId = id
                                saathiName = name
                            }
                            .padding(8.dp)
                    ) {
                        SaathiAvatar(avatarId = id, size = 48.dp, showGlow = isSelected)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ForestGreen else DarkNavy
                        )
                    }
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Name
                    Column {
                        Text(text = "Give your Saathi a name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = saathiName,
                            onValueChange = { saathiName = it },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SecondaryText) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("saathi_name_input")
                        )
                    }

                    // Voice
                    Column {
                        Text(text = "Choose a voice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Male", "Female", "Neutral").forEach { voice ->
                                val sel = selectedVoice == voice
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) SoftMint else PureWhite)
                                        .border(1.5.dp, if (sel) ForestGreen else BorderColor, RoundedCornerShape(10.dp))
                                        .clickable { selectedVoice = voice }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Mic, contentDescription = null, tint = if (sel) ForestGreen else SecondaryText, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = voice, fontSize = 11.sp, color = if (sel) ForestGreen else DarkNavy, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    // Personality style
                    Column {
                        Text(text = "Personality style", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Text(text = "This helps Saathi respond in your preferred way.", fontSize = 10.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Calm", "Friendly", "Motivating", "Funny").forEach { style ->
                                val sel = selectedPersonality == style
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) SoftMint else PureWhite)
                                        .border(1.5.dp, if (sel) ForestGreen else BorderColor, RoundedCornerShape(10.dp))
                                        .clickable { selectedPersonality = style }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = style, fontSize = 10.sp, color = if (sel) ForestGreen else DarkNavy, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    // Preferred language
                    Column {
                        Text(text = "Preferred language", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("English", "Hindi", "Hinglish").forEach { lang ->
                                val sel = selectedLanguage == lang
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (sel) SoftMint else PureWhite)
                                        .border(1.5.dp, if (sel) ForestGreen else BorderColor, RoundedCornerShape(10.dp))
                                        .clickable { selectedLanguage = lang }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = lang, fontSize = 11.sp, color = if (sel) ForestGreen else DarkNavy, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    // Excited to be your Saathi card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PaleGreen)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SaathiAvatar(avatarId = selectedAvatarId, size = 36.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Excited to be your Saathi, $preferredName!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                            Text(text = "Let's make this a healthier, happier you.", fontSize = 11.sp, color = ForestGreen)
                        }
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                    }

                    // Let's Begin Button
                    SaathPrimaryButton(
                        text = "Let's Begin",
                        onClick = {
                            onComplete(
                                preferredName,
                                selectedAgeGroup,
                                selectedPriorities.joinToString(","),
                                saathiName,
                                selectedAvatarId,
                                selectedVoice,
                                selectedPersonality,
                                selectedLanguage
                            )
                        },
                        testTag = "onboarding_complete_button"
                    )
                }
            }
        }
    }
}
