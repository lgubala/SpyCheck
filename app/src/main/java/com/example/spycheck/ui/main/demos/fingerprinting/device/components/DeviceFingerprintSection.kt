package com.example.spycheck.ui.main.demos.fingerprinting.device.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow
import com.example.spycheck.DeviceFingerprint
import com.example.spycheck.DeviceInfo
import com.example.spycheck.FontProfile
import com.example.spycheck.HardwareInfo
import com.example.spycheck.InstalledAppsProfile
import com.example.spycheck.ScreenMetrics
import com.example.spycheck.UniquenessFactor

/**
 * Device Fingerprint Section - Refactored to match other fingerprint sections
 */

@Composable
fun DeviceFingerprintSection(
    fingerprint: DeviceFingerprint?,
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📱",
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = "Device Fingerprint",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "NO permission required",
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (fingerprint == null) {
                DeviceEducationContent(
                    onAnalyze = onAnalyze,
                    isAnalyzing = isAnalyzing
                )
            } else {
                DeviceResultsContent(fingerprint = fingerprint)
            }
        }
    }
}

@Composable
private fun DeviceEducationContent(
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean
) {
    Column {
        Text(
            text = "🧬 Your Digital DNA",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFFFBE0B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your device has unique characteristics that create a fingerprint:",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        val examples = listOf(
            "📱 Installed apps combination (1 in millions)",
            "🖥️ Screen resolution & DPI (device-specific)",
            "🔤 System fonts installed (language/region)",
            "⚙️ Hardware specs (CPU, RAM, storage)"
        )

        examples.forEach { example ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "•",
                    color = Color(0xFF4ECDC4),
                    fontSize = 12.sp
                )
                Text(
                    text = example,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
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
                text = "💡 Real Example: If you have 100 apps from 5 million available, the unique combinations exceed atoms in the universe!",
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isAnalyzing) {
            Button(
                onClick = onAnalyze,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4ECDC4)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "📱 Analyze My Device",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4ECDC4),
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🔬 Extracting your Digital DNA...",
                    fontSize = 12.sp,
                    color = Color(0xFF4ECDC4),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DeviceResultsContent(fingerprint: DeviceFingerprint) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fingerprint ID & Uniqueness (Super Fingerprint Card style)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF9B59B6).copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF9B59B6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Device ID: #${fingerprint.fingerprintId.take(12).uppercase()}",
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = Color(0xFF9B59B6)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Uniqueness: ${fingerprint.uniquenessScore}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (fingerprint.installedApps.personalityProfile.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Profile: ${fingerprint.installedApps.personalityProfile}",
                        fontSize = 11.sp,
                        color = Color(0xFFFFBE0B)
                    )
                }
            }
        }

        // Component stats summary
        DeviceComponentsCard(fingerprint)
    }
}

// Keep all other components from original file (DeviceComponentsCard, DeviceDetailTabs, etc.)
@Composable
fun DeviceComponentsCard(fingerprint: DeviceFingerprint) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📱 Device Fingerprint Components",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    emoji = "📱",
                    value = "${fingerprint.installedApps.totalApps}",
                    label = "Apps"
                )
                StatItem(
                    emoji = "🖥",
                    value = fingerprint.screenMetrics.aspectRatio,
                    label = "Ratio"
                )
                StatItem(
                    emoji = "🔤",
                    value = "${fingerprint.systemFonts.totalFonts}",
                    label = "Fonts"
                )
                StatItem(
                    emoji = "⚙️",
                    value = "${fingerprint.hardwareInfo.cpuCores}",
                    label = "Cores"
                )
            }
        }
    }
}

@Composable
private fun StatItem(emoji: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(
            value,
            color = Color(0xFF4ECDC4),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            label,
            color = Color.Gray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun DeviceDetailTabs(
    fingerprint: DeviceFingerprint,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1A1A1A),
            contentColor = Color.White,
            edgePadding = 0.dp
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                text = { Text("📱 Apps", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                text = { Text("🖥 Screen", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                text = { Text("🔤 Fonts", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                text = { Text("⚙️ Hardware", fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> DeviceAppsDetails(fingerprint.installedApps)
            1 -> DeviceScreenDetails(fingerprint.screenMetrics)
            2 -> DeviceFontDetails(fingerprint.systemFonts)
            3 -> DeviceHardwareDetails(fingerprint.hardwareInfo, fingerprint.deviceInfo)
        }
    }
}

@Composable
private fun DeviceAppsDetails(apps: InstalledAppsProfile) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Installed Apps Analysis",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            apps.categories.forEach { (category, appList) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = category,
                        color = Color(0xFF4ECDC4),
                        fontSize = 12.sp,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = appList.joinToString(", "),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (apps.uniqueApps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🎯 Unique Apps (Increase tracking accuracy):",
                    color = Color(0xFFFFBE0B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                apps.uniqueApps.forEach { app ->
                    Text(
                        text = "• $app",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
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
                    text = "⚠️ App Signature: ${apps.appSignature}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun DeviceScreenDetails(screen: ScreenMetrics) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Screen Metrics Analysis",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val metrics = listOf(
                "Resolution" to "${screen.widthPixels} × ${screen.heightPixels}",
                "Density" to "${screen.densityDpi} DPI",
                "Physical DPI" to "X: ${screen.xdpi.toInt()}, Y: ${screen.ydpi.toInt()}",
                "Screen Size" to "${String.format("%.1f", screen.diagonalInches)}\"",
                "Aspect Ratio" to screen.aspectRatio,
                "Refresh Rate" to "${screen.refreshRate.toInt()}Hz",
                "Uniqueness" to screen.uniquenessContribution
            )

            metrics.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = value,
                        color = if (label == "Uniqueness" && value.contains("Rare"))
                            Color(0xFFFF6B6B) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (label == "Uniqueness") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceFontDetails(fonts: FontProfile) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Font Profile Analysis",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Total Fonts: ${fonts.totalFonts}",
                color = Color(0xFF4ECDC4),
                fontSize = 12.sp
            )

            if (fonts.professionalIndicators.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🎯 Professional Indicators:",
                    color = Color(0xFFFFBE0B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                fonts.professionalIndicators.forEach { indicator ->
                    Text(
                        text = "• $indicator",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Font Signature: ${fonts.fontSignature}",
                color = Color.Gray,
                fontSize = 10.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun DeviceHardwareDetails(hardware: HardwareInfo, device: DeviceInfo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Hardware & System Info",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val specs = listOf(
                "Device" to "${device.manufacturer} ${device.model}",
                "Android" to "${device.androidVersion} (API ${device.apiLevel})",
                "CPU Cores" to hardware.cpuCores.toString(),
                "RAM" to "${hardware.totalRam} (${hardware.availableRam} free)",
                "Storage" to "${hardware.totalStorage} (${hardware.availableStorage} free)",
                "Root Status" to if (hardware.isRooted) "⚠️ ROOTED" else "Not rooted",
                "NFC" to if (hardware.hasNfc) "✓" else "✗",
                "Bluetooth" to if (hardware.hasBluetooth) "✓" else "✗",
                "Gyroscope" to if (hardware.hasGyroscope) "✓" else "✗"
            )

            specs.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = value,
                        color = if (value.contains("ROOTED")) Color(0xFFFF6B6B) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (value.contains("ROOTED")) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceUniquenessFactorsCard(factors: List<UniquenessFactor>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🎯 Uniqueness Factors",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            factors.forEach { factor ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = factor.category,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                        Text(
                            text = factor.value,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    val (color, emoji) = when (factor.rarity) {
                        "Unique" -> Color(0xFFFF6B6B) to "🔴"
                        "Rare" -> Color(0xFFFFBE0B) to "🟠"
                        "Uncommon" -> Color(0xFF4ECDC4) to "🟡"
                        else -> Color.Gray to "⚪"
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(emoji, fontSize = 8.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = factor.rarity,
                            color = color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}