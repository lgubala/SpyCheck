package com.example.spycheck.ui.main.demos.fingerprinting.audio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.AudioFingerprint


@Composable
fun AudioFingerprintSection(
    fingerprint: AudioFingerprint?,
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
                    text = "🎤",
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = "Audio Fingerprint",
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
                AudioEducationContent(
                    onAnalyze = onAnalyze,
                    isAnalyzing = isAnalyzing,
                    progress = progress,
                    statusMessage = statusMessage
                )
            } else {
                AudioResultsContent(fingerprint = fingerprint)
            }
        }
    }
}

@Composable
private fun AudioEducationContent(
    onAnalyze: () -> Unit,
    isAnalyzing: Boolean,
    progress: Int,
    statusMessage: String
) {
    Column {
        Text(
            text = "🎵 Audio Hardware Variations",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFFFBE0B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Every device's audio system has unique characteristics:",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        val examples = listOf(
            "🎤 Microphone count and types (varies by model)",
            "⏱️ Audio latency patterns (hardware-specific)",
            "🔊 Speaker configuration (mono/stereo/quad)",
            "🎵 Supported audio codecs and sample rates"
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
                text = "💡 Real Example: A Galaxy S24 Ultra has 3 mics with 12ms latency. A budget phone has 1 mic with 85ms latency. Instant identification!",
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
                    text = "🎤 Analyze My Audio",
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
private fun AudioResultsContent(fingerprint: AudioFingerprint) {
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
                    text = "Audio ID: #${fingerprint.fingerprintId.take(12).uppercase()}",
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

        // Audio Hardware
        AudioDetailCard(
            emoji = "🎤",
            title = "Audio Hardware",
            signature = fingerprint.audioHardware.hardwareSignature,
            description = "Microphones: ${fingerprint.audioHardware.microphoneCount}, Speakers: ${fingerprint.audioHardware.speakerCount}",
            details = buildList {
                add("Built-in Mic: ${if (fingerprint.audioHardware.hasBuiltInMic) "Yes" else "No"}")
                add("Built-in Speaker: ${if (fingerprint.audioHardware.hasBuiltInSpeaker) "Yes" else "No"}")
                if (fingerprint.audioHardware.microphoneTypes.isNotEmpty()) {
                    add("Mic Types: ${fingerprint.audioHardware.microphoneTypes.joinToString(", ")}")
                }
                if (fingerprint.audioHardware.speakerTypes.isNotEmpty()) {
                    add("Speaker Types: ${fingerprint.audioHardware.speakerTypes.take(3).joinToString(", ")}")
                }
            },
            color = Color(0xFF4ECDC4)
        )

        // Audio Latency
        AudioDetailCard(
            emoji = "⏱️",
            title = "Audio Latency",
            signature = fingerprint.latencyProfile.latencySignature,
            description = fingerprint.latencyProfile.latencyClass,
            details = listOf(
                "Input Latency: ${fingerprint.latencyProfile.inputLatency}ms",
                "Output Latency: ${fingerprint.latencyProfile.outputLatency}ms",
                "Round-trip: ${fingerprint.latencyProfile.roundTripLatency}ms"
            ),
            color = Color(0xFFFFBE0B)
        )

        // Codec Support
        AudioDetailCard(
            emoji = "🎵",
            title = "Codec Support",
            signature = fingerprint.codecSupport.codecSignature,
            description = "${fingerprint.codecSupport.sampleRates.size} sample rates supported",
            details = buildList {
                add("Formats: ${fingerprint.codecSupport.supportedInputFormats.size} input, ${fingerprint.codecSupport.supportedOutputFormats.size} output")
                add("Sample Rates: ${fingerprint.codecSupport.sampleRates.joinToString(", ")} Hz")
                add("Channels: ${fingerprint.codecSupport.channelConfigs.joinToString(", ")}")
            },
            color = Color(0xFF2ECC71)
        )

        // Audio Capabilities
        AudioDetailCard(
            emoji = "🔊",
            title = "Audio Capabilities",
            signature = fingerprint.audioCapabilities.capabilitiesSignature,
            description = buildString {
                if (fingerprint.audioCapabilities.supportsProAudio) append("Pro Audio, ")
                if (fingerprint.audioCapabilities.supportsLowLatency) append("Low Latency, ")
                if (fingerprint.audioCapabilities.supportsMidi) append("MIDI, ")
                if (isEmpty()) append("Standard Audio")
                else removeSuffix(", ")
            },
            details = listOf(
                "Low Latency: ${if (fingerprint.audioCapabilities.supportsLowLatency) "Yes" else "No"}",
                "Pro Audio: ${if (fingerprint.audioCapabilities.supportsProAudio) "Yes" else "No"}",
                "MIDI: ${if (fingerprint.audioCapabilities.supportsMidi) "Yes" else "No"}",
                "Max Input Channels: ${fingerprint.audioCapabilities.maxInputChannels}",
                "Max Output Channels: ${fingerprint.audioCapabilities.maxOutputChannels}"
            ),
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
private fun AudioDetailCard(
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