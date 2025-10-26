package com.example.spycheck.ui.main.demos.sneaky.clipboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.sneaky.clipboard.ClipboardDemoViewModel
import com.example.spycheck.ui.theme.DangerRed
import com.example.spycheck.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.*

/**
 * Demo content showing clipboard snooping
 */
@Composable
fun ClipboardDemoContent(viewModel: ClipboardDemoViewModel) {
    val isMonitoring by viewModel.isMonitoring.collectAsState()
    val clipboardHistory by viewModel.clipboardHistory.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Control Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isMonitoring) {
                Button(
                    onClick = { viewModel.startMonitoring() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerRed
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.start_monitoring))
                }
            } else {
                Button(
                    onClick = { viewModel.stopMonitoring() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.stop_monitoring))
                }

                if (clipboardHistory.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { viewModel.clearHistory() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.clear_history))
                    }
                }
            }
        }

        // Monitoring Status
        if (isMonitoring) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DangerRed.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔴", fontSize = 24.sp)
                    Column {
                        Text(
                            text = stringResource(R.string.monitoring_active),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DangerRed
                        )
                        Text(
                            text = "Every time you copy something, it will appear here",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Clipboard History
        if (clipboardHistory.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📋", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.clipboard_empty),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Items count
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFBE0B).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFFBE0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.items_captured, clipboardHistory.size),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFFBE0B)
                    )
                }
            }

            // List of captured items
            clipboardHistory.forEach { entry ->
                ClipboardItemCard(entry, viewModel)
            }
        }

        // Warning
        if (isMonitoring || clipboardHistory.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DangerRed.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "⚠️ This is how apps steal your data!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DangerRed
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Any app can monitor your clipboard in the background without asking permission. Passwords, credit cards, OTP codes - everything you copy can be stolen silently.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipboardItemCard(
    entry: com.example.spycheck.ui.main.demos.sneaky.clipboard.utils.ClipboardEntry,
    viewModel: ClipboardDemoViewModel
) {
    val isSensitive = viewModel.isSensitive(entry)
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSensitive) DangerRed.copy(alpha = 0.15f) 
                           else Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Icon + Type + Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.getTypeIcon(entry),
                        fontSize = 20.sp
                    )
                    Text(
                        text = viewModel.getTypeLabel(entry),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isSensitive) DangerRed else Color.White
                    )
                }
                
                Text(
                    text = formatTimestamp(entry.timestamp),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Surface(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = entry.content,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSensitive) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = DangerRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "⚠️ SENSITIVE DATA - This could be stolen by malicious apps!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DangerRed,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
