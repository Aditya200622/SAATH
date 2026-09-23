package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrustedContact
import com.example.ui.components.SaathPrimaryButton
import com.example.ui.components.SaathSecondaryButton
import com.example.ui.theme.*

@Composable
fun CircleScreen(
    contacts: List<TrustedContact>,
    onAddContactClick: () -> Unit,
    onContactSupportClick: (TrustedContact) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("My Circle") }
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
                text = "Circle & Community 🤝",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkNavy
            )
            IconButton(onClick = onAddContactClick) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Contact", tint = ForestGreen)
            }
        }

        // Subtabs: "My Circle", "Community", "Resources", "Events"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("My Circle", "Community", "Resources", "Events").forEach { tab ->
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
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) PureWhite else DarkNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedTab == "My Circle") {
            // MY CIRCLE TAB
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Privacy Reassurance Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftMint),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Private & Consent-Driven", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                                Text(
                                    text = "Your journal and thoughts are strictly private. These trusted contacts are only reached when YOU choose to reach out.",
                                    fontSize = 11.sp,
                                    color = ForestGreen,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Trusted Contacts List
                items(contacts) { contact ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor)),
                        modifier = Modifier.fillMaxWidth()
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
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(contact.avatarColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = contact.name.firstOrNull()?.uppercase() ?: "C",
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = contact.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                        Text(text = "${contact.relation} • ${contact.phone}", fontSize = 12.sp, color = SecondaryText)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PaleGreen)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "Trusted", fontSize = 10.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Action buttons: Quick Call, Prepare Message / Request Support
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {}
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onContactSupportClick(contact) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1.4f).height(38.dp)
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Reach out", tint = ForestGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reach Out", fontSize = 12.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    SaathSecondaryButton(
                        text = "+ Add New Trusted Contact",
                        onClick = onAddContactClick,
                        leadingIcon = Icons.Default.Add
                    )
                }
            }
        } else {
            // COMMUNITY FEED / RESOURCES
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(text = "Trending Wellbeing Discussions", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                }

                // Community Post 1
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(AccentBlue), contentAlignment = Alignment.Center) {
                                    Text("R", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Rohan S. • 2h ago", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                    Text("Engineering Student", fontSize = 10.sp, color = SecondaryText)
                                }
                            }
                            Text(
                                text = "How I handled mid-semester burnout: 2-minute box breathing between lectures and turning off screens 45 minutes before sleep made a massive difference.",
                                fontSize = 13.sp,
                                color = DarkNavy,
                                lineHeight = 18.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("38 likes", fontSize = 11.sp, color = SecondaryText)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("9 replies", fontSize = 11.sp, color = SecondaryText)
                                }
                            }
                        }
                    }
                }

                // Community Post 2
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(ForestGreen), contentAlignment = Alignment.Center) {
                                    Text("P", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Pooja K. • 5h ago", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                                    Text("Design Lead", fontSize = 10.sp, color = SecondaryText)
                                }
                            }
                            Text(
                                text = "“Progress, not perfection.” My Saathi reminded me of this when I was stuck on an assignment. Breaking it down into 3 tiny steps was the antidote to procrastination.",
                                fontSize = 13.sp,
                                color = DarkNavy,
                                lineHeight = 18.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("62 likes", fontSize = 11.sp, color = SecondaryText)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("14 replies", fontSize = 11.sp, color = SecondaryText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddManageContactScreen(
    onSaveContact: (name: String, relation: String, phone: String, email: String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("Friend") }
    var phone by remember { mutableStateOf("+91 ") }
    var email by remember { mutableStateOf("") }
    var canSupport by remember { mutableStateOf(true) }
    var canMonthlySummary by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite)
            .statusBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                }
                Text(
                    text = "Add Trusted Contact",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = relation,
                onValueChange = { relation = it },
                label = { Text("Relationship (Mom, Dad, Friend, Sibling)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (Optional)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Permissions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Can receive 'I need someone' support alerts", fontSize = 12.sp, color = DarkNavy, modifier = Modifier.weight(1f))
                        Switch(
                            checked = canSupport,
                            onCheckedChange = { canSupport = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = SoftMint)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Can receive high-level monthly wellbeing overview", fontSize = 12.sp, color = DarkNavy, modifier = Modifier.weight(1f))
                        Switch(
                            checked = canMonthlySummary,
                            onCheckedChange = { canMonthlySummary = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = SoftMint)
                        )
                    }
                }
            }
        }

        SaathPrimaryButton(
            text = "Save Trusted Contact",
            onClick = {
                if (name.isNotBlank() && phone.isNotBlank()) {
                    onSaveContact(name, relation, phone, email)
                    onNavigateBack()
                }
            },
            enabled = name.isNotBlank()
        )
    }
}

@Composable
fun SupportRequestScreen(
    contact: TrustedContact,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDraft by remember {
        mutableStateOf("Hey ${contact.name}, having a bit of a heavy day and wanted to talk for a few minutes if you're free.")
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
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavy)
                }
                Text(
                    text = "Reach Out 💚",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftMint),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderColor))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Reaching out to ${contact.name} (${contact.relation})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
                        Text(text = "Asking for a small moment of someone's time is healthy and brave.", fontSize = 11.sp, color = ForestGreen)
                    }
                }
            }

            Text(text = "Prepare what to say (AI Draft Helper):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

            listOf(
                "Hey ${contact.name}, having a bit of a heavy day and wanted to talk for a few minutes if you're free.",
                "Hi ${contact.name}, just feeling a bit overwhelmed with college/work today. Could we catch up later?",
                "Hey! Just wanted to hear your voice and check in on you."
            ).forEach { draft ->
                val sel = selectedDraft == draft
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (sel) SoftMint else PureWhite)
                        .border(1.5.dp, if (sel) ForestGreen else BorderColor, RoundedCornerShape(12.dp))
                        .clickable { selectedDraft = draft }
                        .padding(12.dp)
                ) {
                    Text(text = draft, fontSize = 13.sp, color = DarkNavy, lineHeight = 18.sp)
                }
            }

            OutlinedTextField(
                value = selectedDraft,
                onValueChange = { selectedDraft = it },
                label = { Text("Edit your message") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SaathPrimaryButton(
                text = "Send SMS Message",
                onClick = {
                    try {
                        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.phone}")).apply {
                            putExtra("sms_body", selectedDraft)
                        }
                        context.startActivity(smsIntent)
                    } catch (e: Exception) {}
                },
                leadingIcon = Icons.Default.Chat
            )

            SaathSecondaryButton(
                text = "Call ${contact.name} Directly",
                onClick = {
                    try {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                        context.startActivity(dialIntent)
                    } catch (e: Exception) {}
                },
                leadingIcon = Icons.Default.Phone
            )
        }
    }
}
