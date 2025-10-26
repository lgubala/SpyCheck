package com.example.spycheck.ui.main.demos.fingerprinting.network.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.spycheck.NetworkFingerprint
@Composable
fun NetworkFingerprintSection(
    fingerprint: NetworkFingerprint?,
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean,
    progress: Int,
    statusMessage: String
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
                    text = "🌐",
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = "Network Fingerprint",
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
                NetworkEducationContent(
                    onAnalyze = onAnalyze,
                    isAnalyzing = isAnalyzing,
                    progress = progress,
                    statusMessage = statusMessage
                )
            } else {
                NetworkResultsContent(fingerprint = fingerprint)
            }
        }
    }
}

@Composable
private fun NetworkEducationContent(
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean,
    progress: Int,
    statusMessage: String
) {
    Column {
        Text(
            text = "📡 Network Configuration Patterns",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFFFBE0B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your network has unique characteristics that identify you:",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        val examples = listOf(
            "📏 MTU size: Carrier/ISP specific (1280-9000 bytes)",
            "🌐 DNS servers: Your ISP's unique configuration",
            "📶 Connection type: WiFi/Cellular/Ethernet",
            "🔧 Network settings: IPv6, VPN, proxy status"
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
                text = "💡 Real Example: T-Mobile uses MTU 1428, AT&T uses 1430. This ALONE can identify your carrier without asking for any permissions!",
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
                    text = "🌐 Analyze My Network",
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
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF4ECDC4),
                    trackColor = Color(0xFF3A3A3A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusMessage,
                    fontSize = 12.sp,
                    color = Color(0xFF4ECDC4),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun NetworkResultsContent(fingerprint: NetworkFingerprint) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fingerprint ID & Uniqueness
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF9B59B6).copy(alpha = 0.15f)
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
                    text = "Network ID: #${fingerprint.fingerprintId.take(12).uppercase()}",
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
            }
        }

        // Connection Profile
        NetworkDetailCard(
            emoji = "📶",
            title = "Connection Profile",
            signature = fingerprint.connectionProfile.connectionSignature,
            description = "${fingerprint.connectionProfile.connectionType} - ${if (fingerprint.connectionProfile.isConnected) "Connected" else "Disconnected"}",
            details = buildList {
                add("Type: ${fingerprint.connectionProfile.connectionType}")
                add("Status: ${if (fingerprint.connectionProfile.isConnected) "Connected" else "Disconnected"}")
                add("Metered: ${if (fingerprint.connectionProfile.isMetered) "Yes" else "No"}")
                fingerprint.connectionProfile.networkName?.let { add("Network: $it") }
                add("Strength: ${fingerprint.connectionProfile.connectionStrength}")
            },
            color = Color(0xFF4ECDC4)
        )

        // Network Capabilities
        NetworkDetailCard(
            emoji = "🔧",
            title = "Network Capabilities",
            signature = fingerprint.networkCapabilities.capabilitiesSignature,
            description = buildString {
                val features = mutableListOf<String>()
                if (fingerprint.networkCapabilities.supportsIPv4) features.add("IPv4")
                if (fingerprint.networkCapabilities.supportsIPv6) features.add("IPv6")
                if (fingerprint.networkCapabilities.hasVPN) features.add("VPN")
                if (fingerprint.networkCapabilities.hasProxy) features.add("Proxy")
                append(features.joinToString(", "))
            },
            details = buildList {
                add("IPv4: ${if (fingerprint.networkCapabilities.supportsIPv4) "Yes" else "No"}")
                add("IPv6: ${if (fingerprint.networkCapabilities.supportsIPv6) "Yes" else "No"}")
                add("VPN: ${if (fingerprint.networkCapabilities.hasVPN) "Active" else "None"}")
                add("Proxy: ${if (fingerprint.networkCapabilities.hasProxy) "Configured" else "None"}")
                if (fingerprint.networkCapabilities.maxDownloadSpeed > 0) {
                    add("Max Download: ${fingerprint.networkCapabilities.maxDownloadSpeed} Mbps")
                }
                if (fingerprint.networkCapabilities.maxUploadSpeed > 0) {
                    add("Max Upload: ${fingerprint.networkCapabilities.maxUploadSpeed} Mbps")
                }
            },
            color = Color(0xFF2ECC71)
        )

        // Network Configuration (THE MOST UNIQUE PART!)
        NetworkDetailCard(
            emoji = "⚙️",
            title = "Network Configuration",
            signature = fingerprint.networkConfig.configSignature,
            description = "MTU: ${fingerprint.networkConfig.mtu} bytes (carrier-specific!)",
            details = buildList {
                add("MTU Size: ${fingerprint.networkConfig.mtu} bytes")
                add("DNS Servers: ${fingerprint.networkConfig.dnsServers.size}")
                fingerprint.networkConfig.dnsServers.forEachIndexed { index, dns ->
                    add("  DNS ${index + 1}: $dns")
                }
                add("Network Interfaces: ${fingerprint.networkConfig.networkInterfaceCount}")
                add("Multiple Networks: ${if (fingerprint.networkConfig.hasMultipleNetworks) "Yes" else "No"}")
            },
            color = Color(0xFFFF6B6B)
        )

        // Uniqueness Factors
        if (fingerprint.uniquenessFactors.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🎯 What Makes You Unique:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    fingerprint.uniquenessFactors.forEach { factor ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "•", color = Color(0xFF4ECDC4), fontSize = 11.sp)
                            Text(
                                text = factor,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkDetailCard(
    emoji: String,
    title: String,
    signature: String,
    description: String,
    details: List<String>,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 20.sp)
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "ID: #${signature.uppercase()}",
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            details.forEach { detail ->
                Text(
                    text = detail,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}