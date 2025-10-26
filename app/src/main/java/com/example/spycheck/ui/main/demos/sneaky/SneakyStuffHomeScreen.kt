package com.example.spycheck.ui.main.demos.sneaky

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R

/**
 * Improved home screen for Sneaky Stuff section
 * Shows clear, non-technical explanations of what sneaky data collection is
 */
@Composable
fun SneakyStuffHomeScreen(
    onDemoClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header explanation
        item {
            HeaderCard()
        }

        // Demo cards
        items(getSneakyDemos()) { demo ->
            SneakyDemoCard(
                demo = demo,
                onClick = { onDemoClick(demo.id) }
            )
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B6B).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "🐛 " + stringResource(R.string.sneaky_stuff_home_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.sneaky_stuff_home_description),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color(0xFF2A2A2A),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 " + stringResource(R.string.sneaky_stuff_home_subtitle),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFBE0B),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun SneakyDemoCard(
    demo: SneakyDemo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Surface(
                color = demo.color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = demo.icon,
                        contentDescription = null,
                        tint = demo.color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = demo.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = demo.description,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private data class SneakyDemo(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

private fun getSneakyDemos(): List<SneakyDemo> {
    return listOf(
        SneakyDemo(
            id = "exif",
            title = "GPS from Photos",
            description = "Your location without asking",
            icon = Icons.Default.LocationOn,
            color = Color(0xFFFF6B6B)
        ),
        SneakyDemo(
            id = "wifi",
            title = "Location from Wi-Fi",
            description = "Pinpointing your location using nearby Wi-Fi networks",
            icon = Icons.Default.Wifi,
            color = Color(0xFF4ECDC4)
        ),
        SneakyDemo(
            id = "clipboard",
            title = "Clipboard Snooping",
            description = "Reading sensitive information you copy to the clipboard",
            icon = Icons.Default.ContentCopy,
            color = Color(0xFFFFBE0B)
        ),
        SneakyDemo(
            id = "notifications",
            title = "Notification Stealing",
            description = "Reading all your notifications from other apps",
            icon = Icons.Default.Notifications,
            color = Color(0xFFFF6B6B)
        ),
        SneakyDemo(
            id = "keystroke",
            title = "Keystroke Inference",
            description = "Guessing what you type using motion sensors",
            icon = Icons.Default.Keyboard,
            color = Color(0xFF9D4EDD)
        ),
        SneakyDemo(
            id = "sensors",
            title = "Sensor Tracking",
            description = "Using motion sensors to track your activities",
            icon = Icons.Default.Sensors,
            color = Color(0xFF06FFA5)
        ),
        SneakyDemo(
            id = "usage_stats",
            title = "Usage Stats Spying",
            description = "Seeing everything you do on your phone",
            icon = Icons.Default.Analytics,
            color = Color(0xFFFF9E00)
        )
    )
}
