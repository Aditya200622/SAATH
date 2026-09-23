package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.*

@Composable
fun SaathHeader(
    userName: String = "Aditya",
    onAvatarClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canNavigateBack) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .border(1.dp, BorderColor, CircleShape)
                    .testTag("header_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkNavy
                )
            }
        }

        // Center / Brand Logo
        Column(
            horizontalAlignment = if (canNavigateBack) Alignment.CenterHorizontally else Alignment.Start,
            modifier = if (canNavigateBack) Modifier.weight(1f) else Modifier
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SAATH",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreen,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Saath leaf",
                    tint = ForestGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = "Always with you",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                letterSpacing = 0.5.sp
            )
        }

        // Right side icons: Search, Notification Bell, User Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(36.dp).testTag("header_search_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = DarkNavy,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Notification Bell with Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onNotificationClick() }
                    .testTag("header_notification_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = DarkNavy,
                    modifier = Modifier.size(24.dp)
                )
                // Red badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 2.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(AccentRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3",
                        color = PureWhite,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // User Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ForestGreen)
                    .border(1.5.dp, PureWhite, CircleShape)
                    .clickable { onAvatarClick() }
                    .testTag("header_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "A",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

enum class NavTab(val label: String) {
    HOME("Home"),
    SAATHI("Saathi"),
    TASKS("Tasks"),
    CIRCLE("Circle"),
    INSIGHTS("Insights")
}

@Composable
fun SaathBottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = PureWhite,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                val icon = when (tab) {
                    NavTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                    NavTab.SAATHI -> if (isSelected) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline
                    NavTab.TASKS -> if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline
                    NavTab.CIRCLE -> if (isSelected) Icons.Filled.Groups else Icons.Outlined.Groups
                    NavTab.INSIGHTS -> if (isSelected) Icons.Filled.BarChart else Icons.Outlined.BarChart
                }

                if (isSelected) {
                    // Selected tab pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ForestGreen)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .clickable { onTabSelected(tab) }
                            .testTag("nav_${tab.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = tab.label,
                            color = PureWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("nav_${tab.name.lowercase()}")
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = SecondaryText,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
