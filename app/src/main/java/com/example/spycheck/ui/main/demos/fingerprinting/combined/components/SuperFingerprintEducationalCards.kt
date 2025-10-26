package com.example.spycheck.ui.main.demos.fingerprinting.combined.components

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
fun SuperFingerprintExplanationCard() {
    Card(
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
            Text(
                text = "🚨 The Complete Tracking Picture",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFFF6B6B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🧬 What is a Super Fingerprint?",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Companies DON'T use just one fingerprinting method. They combine ALL of them to create a SUPER FINGERPRINT that's virtually impossible to avoid:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val combinations = listOf(
                "📱 Device + 🎯 Sensor = Physical hardware ID",
                "🔋 Battery + 🚀 Performance = Usage patterns",
                "🌐 Network + 📱 Device = Location + Identity",
                "🎤 Audio + 🚀 Performance = Device verification"
            )

            combinations.forEach { combo ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "•", color = Color(0xFF4ECDC4), fontSize = 12.sp)
                    Text(
                        text = combo,
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
                        text = "💡 The Math of Combined Tracking:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "If each fingerprint is 1 in 1 million:\n\n" +
                                "• 2 combined = 1 in 1 TRILLION\n" +
                                "• 3 combined = 1 in 1 QUINTILLION\n" +
                                "• 6 combined = More unique than ATOMS in the universe!\n\n" +
                                "You are LITERALLY one in a quintillion.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "⚠️ This is what Google, Facebook, Amazon, and ad networks actually do. They see ALL of this data and combine it to track you everywhere.",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun SuperFingerprintRealWorldCard() {
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
                    text = "🎭 Real Super Fingerprint Tracking",
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
                    SuperRealWorldScenario(
                        number = "1",
                        title = "The Facebook Pixel Tracker",
                        story = "Rachel uses Firefox with uBlock Origin, VPN, and private browsing. She visits a clothing store website. Facebook Pixel captures: Device fingerprint (her apps), Sensor fingerprint (her accelerometer bias), Performance fingerprint (her CPU speed). Combined Super Fingerprint: #A7F2B5. Later, she logs into Instagram on her phone. Instagram sees SAME Super Fingerprint. Facebook now knows: Her private browsing session = Her Instagram account.",
                        impact = "Super Fingerprints link your 'anonymous' browsing to your real identity across different platforms and accounts."
                    )

                    SuperRealWorldScenario(
                        number = "2",
                        title = "The Insurance Fraud Detector",
                        story = "Someone files a stolen phone claim. But their insurance app had collected: Device + Sensor + Battery + Audio + Network + Performance = Super Fingerprint #D8K3M1. Days later, they browse shopping sites. Ad networks detect SAME Super Fingerprint still active. They sell this data to insurance fraud detection companies. Claim denied. Person arrested for fraud.",
                        impact = "Companies share Super Fingerprints across industries. Your 'stolen' device can be detected anywhere online."
                    )

                    SuperRealWorldScenario(
                        number = "3",
                        title = "The Political Targeting",
                        story = "Government wants to identify protesters. They capture Super Fingerprints from protest organizing websites (Device + Sensor + Network). Later, they buy ad targeting data from Google. Google has SAME Super Fingerprints linked to real Gmail accounts. Cross-reference reveals: 342 protesters identified by name, address, workplace. Privacy tools didn't help - hardware doesn't lie.",
                        impact = "Governments and corporations can de-anonymize anyone by combining Super Fingerprints with data broker information."
                    )

                    SuperRealWorldScenario(
                        number = "4",
                        title = "The Credit Score Destruction",
                        story = "Alex browses payday loan websites out of curiosity (incognito mode). Ad networks capture Super Fingerprint and mark him as 'financially distressed'. This data is sold to credit bureaus and banks. Months later, Alex applies for a mortgage. Denied - his Super Fingerprint is flagged as high-risk. He never signed up for anything, just browsed.",
                        impact = "Super Fingerprints create permanent records of your browsing behavior that affect real-world credit decisions."
                    )

                    SuperRealWorldScenario(
                        number = "5",
                        title = "The Cross-Device Price Gouging",
                        story = "Emma shops for flights on her laptop (work device). Price: $450. She checks again on her phone (personal device). Price: $620. Why? Her Super Fingerprint on phone reveals: Premium apps installed, 5G connection, flagship device, expensive audio hardware. Algorithm assumes wealth. She's paying $170 more for having a nice phone.",
                        impact = "Super Fingerprints enable price discrimination - companies charge you more based on your device fingerprint."
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚠️ ULTIMATE TRUTH:\n\n" +
                                    "Super Fingerprinting means:\n" +
                                    "• You CANNOT be anonymous online\n" +
                                    "• VPNs don't protect you (hardware doesn't change)\n" +
                                    "• Private browsing is a lie\n" +
                                    "• Factory resets don't help (if you reinstall same apps)\n" +
                                    "• Creating new accounts doesn't work\n" +
                                    "• Ad blockers can't stop hardware fingerprinting\n\n" +
                                    "Your device IS your permanent identity.",
                            fontSize = 11.sp,
                            color = Color(0xFFFF6B6B),
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
                    text = "👆 Tap to see 5 SHOCKING real examples",
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
private fun SuperRealWorldScenario(
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
fun SuperFingerprintProtectionCard() {
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
                text = "🛡️ Can You Protect Yourself?",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF4ECDC4)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "The harsh truth:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProtectionMethodCard(
                method = "❌ VPN",
                effectiveness = "0%",
                reason = "Doesn't change hardware characteristics",
                color = Color(0xFFFF6B6B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = "❌ Private Browsing",
                effectiveness = "0%",
                reason = "Hardware fingerprint still accessible",
                color = Color(0xFFFF6B6B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = "❌ Ad Blockers",
                effectiveness = "5%",
                reason = "Can't block hardware API access",
                color = Color(0xFFFF6B6B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = "⚠️ Factory Reset",
                effectiveness = "30%",
                reason = "Works only if you change ALL apps + settings",
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProtectionMethodCard(
                method = "✓ New Device",
                effectiveness = "90%",
                reason = "Different hardware = different fingerprint",
                color = Color(0xFF4ECDC4)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 Best Protection Strategy:\n\n" +
                            "1. Minimize app installs (fewer apps = less unique)\n" +
                            "2. Use generic devices (common models blend in)\n" +
                            "3. Disable JavaScript on sensitive sites\n" +
                            "4. Use different devices for different activities\n" +
                            "5. Accept you can't be truly anonymous\n\n" +
                            "Reality: Perfect protection is IMPOSSIBLE with modern devices.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ProtectionMethodCard(
    method: String,
    effectiveness: String,
    reason: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = reason,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 14.sp
                )
            }
            Text(
                text = effectiveness,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}