package com.example.spycheck.ui.main.demos.sneaky.notifications.components

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.ui.main.demos.sneaky.notifications.NotificationDemoViewModel
import com.example.spycheck.ui.main.demos.sneaky.notifications.utils.CapturedNotification
import com.example.spycheck.ui.main.demos.sneaky.notifications.utils.TrackingNotificationListener

enum class NotificationCategory {
    PERSONAL, SYSTEM
}

@Composable
fun NotificationDemoContent(viewModel: NotificationDemoViewModel) {
    val context = LocalContext.current
    val notifications by TrackingNotificationListener.notifications.collectAsState()
    var selectedCategory by remember { mutableStateOf(NotificationCategory.PERSONAL) }

    // Auto-refresh permission state when returning from settings
    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                viewModel.checkPermission(context)
                handler.postDelayed(this, 1000) // Check every second
            }
        }
        handler.post(runnable)

        onDispose {
            handler.removeCallbacks(runnable)
        }
    }

    // Filter notifications
    val filteredNotifications = remember(notifications, selectedCategory) {
        when (selectedCategory) {
            NotificationCategory.PERSONAL -> notifications.filter { it.isSensitive }
            NotificationCategory.SYSTEM -> notifications.filter { !it.isSensitive }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active warning banner
        ActiveMonitoringBanner(
            count = notifications.size,
            onRevokePermission = {
                Toast.makeText(
                    context,
                    "👉 Find 'SpyCheck' and toggle OFF",
                    Toast.LENGTH_LONG
                ).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent)
                }, 1000)
            }
        )

        // Category tabs
        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            personalCount = notifications.count { it.isSensitive },
            systemCount = notifications.count { !it.isSensitive }
        )

        // Clear button
        OutlinedButton(
            onClick = {
                TrackingNotificationListener.clearNotifications()
                Toast.makeText(context, "Notifications cleared", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF4ECDC4)
            )
        ) {
            Text("🗑️ Clear All Notifications")
        }

        // Notifications list
        if (filteredNotifications.isEmpty()) {
            EmptyState(selectedCategory)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredNotifications) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}

@Composable
fun ActiveMonitoringBanner(
    count: Int,
    onRevokePermission: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f * alpha)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔴", fontSize = 20.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACTIVE MONITORING",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$count notifications captured",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRevokePermission,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A3A3A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🔒 Revoke Permission",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: NotificationCategory,
    onCategorySelected: (NotificationCategory) -> Unit,
    personalCount: Int,
    systemCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryTab(
            title = "Personal",
            count = personalCount,
            isSelected = selectedCategory == NotificationCategory.PERSONAL,
            onClick = { onCategorySelected(NotificationCategory.PERSONAL) },
            modifier = Modifier.weight(1f)
        )

        CategoryTab(
            title = "System",
            count = systemCount,
            isSelected = selectedCategory == NotificationCategory.SYSTEM,
            onClick = { onCategorySelected(NotificationCategory.SYSTEM) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryTab(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF4ECDC4)
            else
                Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color.White
            )
            Text(
                text = "$count",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.Black else Color(0xFF4ECDC4)
            )
        }
    }
}

@Composable
fun EmptyState(category: NotificationCategory) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (category == NotificationCategory.PERSONAL) "💬" else "⚙️",
                fontSize = 48.sp
            )
            Text(
                text = "No ${category.name.lowercase()} notifications yet",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = if (category == NotificationCategory.PERSONAL)
                    "Try sending yourself a message"
                else
                    "System notifications will appear here",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun NotificationItem(notification: CapturedNotification) {
    var isRevealed by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isSensitive)
                Color(0xFFFF6B6B).copy(alpha = 0.15f)
            else
                Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isRevealed = true }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // App name and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.appName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (notification.isSensitive) Color(0xFFFF6B6B) else Color.White
                )

                if (notification.isSensitive) {
                    Surface(
                        color = Color(0xFFFF6B6B).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "SENSITIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            if (!notification.title.isNullOrBlank()) {
                Text(
                    text = if (isRevealed) {
                        notification.title
                    } else {
                        "•".repeat((notification.title.length / 2).coerceAtLeast(15))
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = if (!isRevealed) Modifier.blur(5.dp) else Modifier
                )
            }

            // Text content
            if (!notification.text.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRevealed)
                        notification.text
                    else
                        "•".repeat((notification.text.length / 3).coerceAtLeast(20)),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp,
                    modifier = if (!isRevealed) Modifier.blur(5.dp) else Modifier
                )
            }

            if (!isRevealed) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFF4ECDC4).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "👆 Tap to reveal content",
                        fontSize = 11.sp,
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            if (notification.isSensitive && isRevealed) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Apps can steal this data for fraud/phishing",
                    fontSize = 10.sp,
                    color = Color(0xFFFF6B6B),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
