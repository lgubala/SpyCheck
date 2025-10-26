package com.example.spycheck.ui.main.demos.fingerprinting.network.components

import androidx.compose.animation.AnimatedVisibility
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

@Composable
fun NetworkFingerprintExplanationCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4ECDC4).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🤔 What is Network Fingerprinting?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🌐 Every Network Has a Unique Signature:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your ISP and carrier configure networks differently, creating identifiable patterns:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val factors = listOf(
                "📏 MTU (Max Transmission Unit): Carrier-specific packet size",
                "🌐 DNS servers: Your ISP's unique DNS configuration",
                "📡 Network type: 5G/LTE reveals device & carrier",
                "🔧 IPv6 support: Not all carriers enable it",
                "⚡ Connection speeds: Bandwidth limits vary by plan"
            )

            factors.forEach { factor ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "•", color = Color(0xFF4ECDC4), fontSize = 12.sp)
                    Text(
                        text = factor,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "💡 The MTU Trick:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "MTU (Maximum Transmission Unit) is THE most unique network identifier:\n\n" +
                                "• T-Mobile: 1428 bytes\n" +
                                "• Verizon: 1428 bytes\n" +
                                "• AT&T: 1430 bytes\n" +
                                "• Comcast: 1500 bytes\n" +
                                "• Google Fiber: 1500 bytes\n\n" +
                                "This ONE number can reveal your carrier without asking!",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
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
                    text = "⚠️ NO PERMISSIONS NEEDED! Network configuration is freely accessible. Websites can read this through JavaScript!",
                    fontSize = 11.sp,
                    color = Color(0xFFFFBE0B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFBE0B).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "📱 Note: Carrier network type (5G/LTE) requires READ_PHONE_STATE permission. All other network info requires NO permissions!",
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun NetworkTrackingRealWorldCard() {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎭 Real-World Network Tracking",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFFF6B6B)
                )
                Text(
                    text = if (expanded) "▼" else "▶",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 20.sp
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NetworkRealWorldScenario(
                        number = "1",
                        title = "The Coffee Shop Stalker",
                        story = "Lisa always uses VPN and private browsing at Starbucks. But websites detect her MTU (1500 bytes) and DNS servers (8.8.8.8). They recognize: 'This is the same person who browses here daily, regardless of her fake IP address.'",
                        impact = "Network fingerprints work THROUGH VPNs because they reveal your actual connection characteristics, not just your IP."
                    )

                    NetworkRealWorldScenario(
                        number = "2",
                        title = "The Carrier Price Discrimination",
                        story = "Two friends buy concert tickets. One on T-Mobile 5G (MTU 1428) gets charged $150. Other on WiFi (MTU 1500) gets charged $120 for same seats. Sites charge MORE for mobile users with premium networks.",
                        impact = "E-commerce sites use network fingerprints to detect 'wealthy' users (premium carriers, 5G) and show higher prices."
                    )

                    NetworkRealWorldScenario(
                        number = "3",
                        title = "The Corporate Leak Investigator",
                        story = "A whistleblower leaks documents using Tor from a coffee shop. Investigators analyze the MTU (1430 bytes = AT&T Corporate) in metadata. They narrow suspects to employees with AT&T corporate plans. Only 50 people. Easy to find.",
                        impact = "Corporate networks have VERY specific MTU values. This can identify company employees even through anonymization."
                    )

                    NetworkRealWorldScenario(
                        number = "4",
                        title = "The Cross-Device Ad Network",
                        story = "Mark browses adult content on phone (T-Mobile MTU 1428). Later opens laptop on home WiFi (Comcast MTU 1500). Ad network sees SAME DNS servers (ISP default). They link his devices: 'Both from same household.'",
                        impact = "Even different devices share network characteristics when on same ISP, allowing cross-device tracking."
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚠️ CRITICAL: Network fingerprinting:\n" +
                                    "• Works through VPNs\n" +
                                    "• Works through proxies\n" +
                                    "• Works in incognito mode\n" +
                                    "• Requires NO permissions\n" +
                                    "• Accessible via JavaScript\n" +
                                    "• Links devices on same network\n\n" +
                                    "Your network ALWAYS betrays you.",
                            fontSize = 11.sp,
                            color = Color(0xFFFFBE0B),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            if (!expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "👆 Tap to see 4 real examples",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun NetworkRealWorldScenario(
    number: String,
    title: String,
    story: String,
    impact: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFFF6B6B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = story,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "💡", fontSize = 12.sp)
                    Text(
                        text = impact,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NetworkTestItYourselfCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🧪 Test Network Fingerprinting",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF4ECDC4)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Prove network fingerprints reveal your identity:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            NetworkExperimentCard(
                number = "1",
                title = "The WiFi Switch Test",
                steps = listOf(
                    "Analyze network on cellular data",
                    "Note your MTU and DNS servers",
                    "Switch to WiFi",
                    "Analyze again",
                    "Compare the fingerprints"
                ),
                why = "COMPLETELY DIFFERENT! Each network has unique settings."
            )

            Spacer(modifier = Modifier.height(12.dp))

            NetworkExperimentCard(
                number = "2",
                title = "The VPN Test",
                steps = listOf(
                    "Analyze without VPN",
                    "Note your MTU value",
                    "Enable VPN (any provider)",
                    "Analyze again",
                    "Compare MTU values"
                ),
                why = "MTU stays SAME! VPN changes IP but not hardware config."
            )

            Spacer(modifier = Modifier.height(12.dp))

            NetworkExperimentCard(
                number = "3",
                title = "The Browser Test",
                steps = listOf(
                    "Open Chrome and visit: browserleaks.com/ip",
                    "Check 'WebRTC' section",
                    "See your local IP and network info",
                    "Try in incognito mode",
                    "Compare results"
                ),
                why = "IDENTICAL! Websites can see your network config."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 MTU Detective: Compare your MTU with friends on different carriers. You'll see patterns:\n" +
                            "• T-Mobile: 1428\n" +
                            "• Verizon: 1428\n" +
                            "• AT&T: 1430\n" +
                            "Each carrier is identifiable!",
                    fontSize = 11.sp,
                    color = Color(0xFFFFBE0B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun NetworkExperimentCard(
    number: String,
    title: String,
    steps: List<String>,
    why: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
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
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF4ECDC4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        fontSize = 11.sp,
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFBE0B).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔌", fontSize = 12.sp)
                    Text(
                        text = "Why: $why",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}