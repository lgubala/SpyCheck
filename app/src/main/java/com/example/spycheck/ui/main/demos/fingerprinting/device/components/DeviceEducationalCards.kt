package com.example.spycheck.ui.main.demos.fingerprinting.device.components

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
fun DeviceFingerprintExplanationCard() {
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
                text = "🤔 What is Device Fingerprinting?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📱 Your Device is Unique:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Just like a fingerprint identifies a person, your device has unique characteristics that identify YOU:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val factors = listOf(
                "📱 Installed apps: Your unique app combination",
                "🖥️ Screen specs: Exact resolution, DPI, refresh rate",
                "🔤 System fonts: Which fonts are installed",
                "⚙️ Hardware: CPU, RAM, storage configuration",
                "🌍 Language & locale: Your region settings",
                "🎨 Display settings: Theme, brightness patterns"
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
                        text = "💡 The Math:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "If you have 100 apps installed, and there are 5 million apps available, the number of UNIQUE COMBINATIONS is astronomical:\n\n" +
                                "5,000,000 choose 100 = more than the number of atoms in the universe!\n\n" +
                                "Add screen resolution, fonts, hardware... you're 1 in BILLIONS.",
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
                    text = "⚠️ NO PERMISSIONS NEEDED! All this info is freely accessible. Websites can read it through JavaScript!",
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
fun DeviceTrackingRealWorldCard() {
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
                    text = "🎭 Real-World Device Tracking",
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
                    DeviceRealWorldScenario(
                        number = "1",
                        title = "The Anonymous Activist",
                        story = "Sarah uses Tor Browser and VPN to post political content anonymously. But a government agency analyzes her device fingerprint (100+ apps, specific screen resolution 2400x1080, 147 system fonts). They cross-reference with ISP records. Only 3 people in her city have this EXACT combination. She's identified.",
                        impact = "Device fingerprints work THROUGH VPNs and Tor. Your hardware and apps betray your identity even when your IP is hidden."
                    )

                    DeviceRealWorldScenario(
                        number = "2",
                        title = "The Shopping Spy",
                        story = "Mike browses Amazon on his phone (logged in). Later, he visits Best Buy on his laptop (not logged in, different browser). Both sites see the SAME device fingerprint through browser APIs. Amazon tells Best Buy: 'This is the same person.' Best Buy adjusts prices based on his Amazon browsing history.",
                        impact = "Companies share device fingerprints to track you across devices and websites, building a complete shopping profile."
                    )

                    DeviceRealWorldScenario(
                        number = "3",
                        title = "The App Store Rejection",
                        story = "A developer gets banned from Google Play for policy violations. He creates a new account, new email, new payment method. But Google detects his device fingerprint (same phone, same app combination). New account banned instantly. He can't publish apps anymore from ANY account on this device.",
                        impact = "Device fingerprints create permanent bans. Even factory resets don't help if you reinstall the same apps."
                    )

                    DeviceRealWorldScenario(
                        number = "4",
                        title = "The Insurance Fraud Detector",
                        story = "Someone reports their phone stolen for insurance claim. But the insurance company's app had been fingerprinting the device. Days later, they detect the SAME fingerprint browsing shopping sites. Claim denied. Police called for fraud.",
                        impact = "Insurance companies use device fingerprints to detect fraud and deny legitimate claims if you keep using your 'stolen' device."
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚠️ CRITICAL: Device fingerprinting:\n" +
                                    "• Works through VPNs/proxies\n" +
                                    "• Works in incognito mode\n" +
                                    "• Persists after app uninstalls\n" +
                                    "• Tracks you across websites\n" +
                                    "• Requires ZERO permissions\n" +
                                    "• Cannot be blocked without breaking sites\n\n" +
                                    "Your device IS your identity.",
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
                    text = "👆 Tap to see 4 shocking examples",
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
private fun DeviceRealWorldScenario(
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
fun DeviceTestItYourselfCard() {
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
                text = "🧪 Test Device Fingerprinting",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF4ECDC4)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Prove your device is uniquely identifiable:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            DeviceExperimentCard(
                number = "1",
                title = "The Consistency Test",
                steps = listOf(
                    "Screenshot your Device Fingerprint ID",
                    "Close this app completely",
                    "Clear app data (Settings → Apps → TrackingWatch → Clear Data)",
                    "Reopen the app and analyze again",
                    "Compare the IDs"
                ),
                why = "IDENTICAL! Your device characteristics don't change."
            )

            Spacer(modifier = Modifier.height(12.dp))

            DeviceExperimentCard(
                number = "2",
                title = "The Browser Test",
                steps = listOf(
                    "Open Chrome and visit: amiunique.org",
                    "See your browser fingerprint score",
                    "Note: Screen resolution, fonts, plugins",
                    "Try in incognito mode",
                    "Compare results"
                ),
                why = "SAME fingerprint! Websites track you even in private mode."
            )

            Spacer(modifier = Modifier.height(12.dp))

            DeviceExperimentCard(
                number = "3",
                title = "The App Combination Test",
                steps = listOf(
                    "Note your 'Uniqueness Score' (e.g., 1 in 50 million)",
                    "Install 5 random apps",
                    "Analyze your device again",
                    "See how uniqueness increased"
                ),
                why = "More unique! Every app you install makes you MORE identifiable."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 Advanced: Use 2 phones of the SAME model. Both will have DIFFERENT fingerprints due to different apps, settings, and usage patterns!",
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
private fun DeviceExperimentCard(
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