package com.example.spycheck.ui.main.demos.fingerprinting.common


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * Common components shared across all fingerprinting sections
 */

@Composable
fun FingerprintSectionDivider() {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            color = Color.Gray.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FingerprintSectionHeader(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$emoji $title",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun DNAHeaderCard(dnaRotation: Float) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9B59B6).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        rotationY = dnaRotation
                    }
            ) {
                drawDNAHelix(this)
            }

            Text(
                text = "🧬",
                fontSize = 64.sp,
                color = Color(0xFF9B59B6).copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun EducationalIntroCard(onDismiss: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "🚨 The Invisible Tracking You Never Knew About",
                    color = Color(0xFFFFBE0B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Even without ANY permissions, apps and websites can create a unique 'fingerprint' of your device using:",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val points = listOf(
                "📱 Your installed apps combination",
                "📏 Exact screen dimensions & DPI",
                "🔤 System fonts installed",
                "⚡ Hardware specifications",
                "📊 Audio/sensor characteristics",
                "📶 Network behavior patterns"
            )

            points.forEach { point ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "• ",
                        color = Color(0xFF4ECDC4),
                        fontSize = 12.sp
                    )
                    Text(
                        text = point,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 This 'fingerprint' follows you across apps, websites, and even after factory resets!",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawDNAHelix(scope: DrawScope) {
    val steps = 20
    val amplitude = size.width * 0.3f
    val centerX = size.width / 2
    val stepHeight = size.height / steps

    for (i in 0 until steps) {
        val y = i * stepHeight
        val phase1 = (i * 0.5f)
        val phase2 = phase1 + PI.toFloat()

        val x1 = centerX + sin(phase1) * amplitude
        val x2 = centerX + sin(phase2) * amplitude

        scope.drawLine(
            color = Color(0xFF9B59B6).copy(alpha = 0.5f),
            start = androidx.compose.ui.geometry.Offset(x1, y),
            end = androidx.compose.ui.geometry.Offset(x2, y),
            strokeWidth = 2.dp.toPx()
        )

        scope.drawCircle(
            color = Color(0xFF4ECDC4),
            radius = 4.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(x1, y)
        )
        scope.drawCircle(
            color = Color(0xFFFF6B6B),
            radius = 4.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(x2, y)
        )
    }
}