package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class AvatarMood {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ENCOURAGING,
    CONCERNED
}

@Composable
fun SaathiAvatar(
    avatarId: String = "aarav",
    size: Dp = 64.dp,
    mood: AvatarMood = AvatarMood.IDLE,
    showGlow: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = when (mood) {
            AvatarMood.THINKING, AvatarMood.LISTENING -> 1.08f
            AvatarMood.SPEAKING -> 1.05f
            AvatarMood.ENCOURAGING -> 1.06f
            AvatarMood.CONCERNED -> 1.02f
            AvatarMood.IDLE -> 1.03f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (mood) {
                    AvatarMood.SPEAKING -> 600
                    AvatarMood.THINKING -> 800
                    AvatarMood.CONCERNED -> 1600
                    else -> 1200
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatarPulse"
    )

    val (bgGradient, initialLetter, accentColor) = when (avatarId.lowercase()) {
        "mira" -> Triple(
            listOf(Color(0xFFFFD1DC), Color(0xFFFDE2E4)),
            "M",
            Color(0xFFE56B6F)
        )
        "kian" -> Triple(
            listOf(Color(0xFFD0E1FD), Color(0xFFE2EAFC)),
            "K",
            Color(0xFF4F7CFF)
        )
        "nova" -> Triple(
            listOf(Color(0xFFFDE2B8), Color(0xFFFFF1DC)),
            "N",
            Color(0xFFE6A23C)
        )
        else -> Triple( // Aarav (default, warm boy in green hoodie)
            listOf(Color(0xFFCBEAD7), Color(0xFFE8F6ED)),
            "A",
            ForestGreen
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(if (showGlow) pulseScale else 1f),
        contentAlignment = Alignment.Center
    ) {
        // Soft Glow Aura
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(size + 8.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ForestGreen.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
        }

        // Avatar Face Container
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Brush.verticalGradient(bgGradient))
                .border(2.dp, PureWhite, CircleShape)
                .border(3.dp, accentColor.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.toPx() / 2, size.toPx() / 2)
                // Hair and styling accents
                drawCircle(
                    color = accentColor.copy(alpha = 0.15f),
                    radius = size.toPx() * 0.48f,
                    center = centerOffset
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Friendly stylized face badge
                Text(
                    text = initialLetter,
                    fontSize = (size.value * 0.42f).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }

            // Small leaf / heart emblem on bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.32f)
                    .clip(CircleShape)
                    .background(PureWhite)
                    .border(1.dp, ForestGreen.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Saathi badge",
                    tint = ForestGreen,
                    modifier = Modifier.size(size * 0.2f)
                )
            }
        }
    }
}
