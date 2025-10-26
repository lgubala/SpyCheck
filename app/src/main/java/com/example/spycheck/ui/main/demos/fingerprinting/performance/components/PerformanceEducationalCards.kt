package com.example.spycheck.ui.main.demos.fingerprinting.performance.components

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
fun PerformanceFingerprintExplanationCard() {
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
                text = "🤔 What is Performance Fingerprinting?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🎲 The Silicon Lottery:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Even two IDENTICAL phones from the same production line will perform differently:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val factors = listOf(
                "⚡ Manufacturing variance: ±5-15% speed difference",
                "🔥 Thermal behavior: How your device handles heat",
                "💾 Storage quality: Same spec, different speeds",
                "🧠 Memory binning: RAM chips sorted by quality",
                "🎯 CPU binning: 'Golden samples' vs regular chips"
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
                        text = "💡 What is CPU Binning?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF4ECDC4)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "When chips are manufactured, they test each one:\n\n" +
                                "• Top 5%: 'Golden samples' - fastest chips\n" +
                                "• Next 15%: High-binned - above average\n" +
                                "• Middle 60%: Standard - target performance\n" +
                                "• Bottom 20%: Low-binned - slower but stable\n\n" +
                                "Your phone gets a RANDOM chip from this lottery. Two 'identical' Galaxy S24s might have CPUs from different bins, making them UNIQUELY identifiable!",
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
                    text = "⚠️ NO PERMISSIONS NEEDED! Performance benchmarks are completely silent - websites can run them in JavaScript without asking!",
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
fun PerformanceTrackingRealWorldCard() {
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
                    text = "🎭 Real-World Performance Tracking",
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
                    PerformanceRealWorldScenario(
                        number = "1",
                        title = "The Browser Benchmark Trap",
                        story = "Jessica visits a shopping site using Tor Browser with ALL privacy protections. The site runs a silent JavaScript benchmark (takes 0.2 seconds, unnoticeable). Her CPU scores 847ms - matching a fingerprint from 2 weeks ago. They know: 'This is the same person who abandoned cart before.'",
                        impact = "Websites can run performance benchmarks in JavaScript without permission. Your CPU's unique speed becomes a tracking ID."
                    )

                    PerformanceRealWorldScenario(
                        number = "2",
                        title = "The Gaming Fraud Detection",
                        story = "A pro gamer is banned for cheating. He buys a new account, new PC (same model), new IP, new everything. But the game runs performance benchmarks during loading screens. His NEW PC scores 1,205ms on a test. His OLD PC scored 1,198ms. 0.6% difference. Banned again - same hardware detected.",
                        impact = "Performance signatures persist across accounts, IPs, and even 'new' identical hardware purchases."
                    )

                    PerformanceRealWorldScenario(
                        number = "3",
                        title = "The Price Discrimination Algorithm",
                        story = "Two users shop for hotels. User A (flagship phone, 920ms benchmark) sees $450/night. User B (budget phone, 1,850ms benchmark) sees $280/night for SAME room. Sites assume fast phones = wealthy users = higher willingness to pay.",
                        impact = "E-commerce uses performance fingerprints to detect 'premium' devices and charge more."
                    )

                    PerformanceRealWorldScenario(
                        number = "4",
                        title = "The Cross-Platform Tracking",
                        story = "Mike browses adult content on his gaming PC (CPU benchmark: 412ms). Later, he checks email on his work laptop (CPU: 1,100ms). Ad network runs benchmarks on BOTH. Different scores, but they see he ALSO has a phone that scores 850ms. All three devices linked through household network analysis + performance correlation.",
                        impact = "Performance fingerprints, combined with other signals, create a complete device profile across all your hardware."
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚠️ CRITICAL: Performance fingerprinting:\n" +
                                    "• Runs in < 1 second (unnoticeable)\n" +
                                    "• Works through VPNs/proxies\n" +
                                    "• Works in private browsing\n" +
                                    "• Requires ZERO permissions\n" +
                                    "• Can't be blocked without breaking sites\n" +
                                    "• Uniqueness increases over time (thermal aging)\n\n" +
                                    "Your hardware performance is a permanent ID.",
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
private fun PerformanceRealWorldScenario(
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
fun PerformanceTestItYourselfCard() {
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
                text = "🧪 Test Performance Consistency",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF4ECDC4)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Prove performance fingerprints are consistent:",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PerformanceExperimentCard(
                number = "1",
                title = "The Consistency Test",
                steps = listOf(
                    "Screenshot your benchmark results",
                    "Close the app completely",
                    "Wait 1 minute (let phone cool)",
                    "Reopen and benchmark again",
                    "Compare the scores"
                ),
                why = "Within ±5%! Your hardware performs consistently."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PerformanceExperimentCard(
                number = "2",
                title = "The Thermal Test",
                steps = listOf(
                    "Benchmark when phone is cool",
                    "Play a game for 5 minutes",
                    "Immediately benchmark again (hot)",
                    "Compare scores"
                ),
                why = "Slower when hot! This thermal pattern is also unique to your device."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PerformanceExperimentCard(
                number = "3",
                title = "The Browser Benchmark Test",
                steps = listOf(
                    "Open Chrome and visit: browserbench.org/Speedometer/",
                    "Run the benchmark (takes ~1 minute)",
                    "Note your score",
                    "Try in incognito mode",
                    "Compare scores"
                ),
                why = "SAME score! Websites can benchmark you silently."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 The Silicon Lottery Test: Compare your benchmark with a friend's IDENTICAL phone model. You'll likely see 5-15% difference due to manufacturing variance. This proves each device is unique!",
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
private fun PerformanceExperimentCard(
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