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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.AudioFingerprint
import com.example.spycheck.R


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
            containerColor = colorResource(R.color.card_background)
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
                    text = stringResource(R.string.fp_audio_section_icon),
                    fontSize = 32.sp
                )
                Column {
                    Text(
                        text = stringResource(R.string.fp_audio_section_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.white)
                    )
                    Text(
                        text = stringResource(R.string.fp_audio_section_no_permission),
                        fontSize = 12.sp,
                        color = colorResource(R.color.info_blue)
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
    val context = LocalContext.current
    val examples = context.resources.getStringArray(R.array.fp_audio_section_examples)

    Column {
        Text(
            text = stringResource(R.string.fp_audio_section_variations_title),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = colorResource(R.color.warning_yellow)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.fp_audio_section_variations_desc),
            fontSize = 12.sp,
            color = colorResource(R.color.white).copy(alpha = 0.8f),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        examples.forEach { example ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.bullet_point),
                    color = colorResource(R.color.info_blue),
                    fontSize = 12.sp
                )
                Text(
                    text = example,
                    fontSize = 11.sp,
                    color = colorResource(R.color.white).copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorResource(R.color.danger_red).copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.fp_audio_section_real_example),
                fontSize = 11.sp,
                color = colorResource(R.color.white),
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
                    containerColor = colorResource(R.color.info_blue)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.fp_audio_section_analyze_button),
                    color = colorResource(R.color.black),
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
                    color = colorResource(R.color.info_blue),
                    trackColor = colorResource(R.color.audio_progress_track)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusMessage,
                    fontSize = 12.sp,
                    color = colorResource(R.color.info_blue),
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
                containerColor = colorResource(R.color.audio_result_bg)
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
                    text = stringResource(R.string.fp_audio_section_audio_id, fingerprint.fingerprintId.take(12).uppercase()),
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = colorResource(R.color.audio_result_color)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.fp_audio_section_uniqueness, fingerprint.uniquenessScore),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.white)
                )
            }
        }

        // Audio Hardware
        AudioDetailCard(
            emoji = stringResource(R.string.fp_audio_section_icon_hardware),
            title = stringResource(R.string.fp_audio_section_hardware_title),
            signature = fingerprint.audioHardware.hardwareSignature,
            description = stringResource(
                R.string.fp_audio_section_hardware_desc,
                fingerprint.audioHardware.microphoneCount,
                fingerprint.audioHardware.speakerCount
            ),
            details = buildList {
                add(stringResource(R.string.fp_audio_section_builtin_mic,
                    stringResource(if (fingerprint.audioHardware.hasBuiltInMic) R.string.yes else R.string.no)))
                add(stringResource(R.string.fp_audio_section_builtin_speaker,
                    stringResource(if (fingerprint.audioHardware.hasBuiltInSpeaker) R.string.yes else R.string.no)))
                if (fingerprint.audioHardware.microphoneTypes.isNotEmpty()) {
                    add(stringResource(R.string.fp_audio_section_mic_types,
                        fingerprint.audioHardware.microphoneTypes.joinToString(", ")))
                }
                if (fingerprint.audioHardware.speakerTypes.isNotEmpty()) {
                    add(stringResource(R.string.fp_audio_section_speaker_types,
                        fingerprint.audioHardware.speakerTypes.take(3).joinToString(", ")))
                }
            },
            color = colorResource(R.color.info_blue)
        )

        // Audio Latency
        AudioDetailCard(
            emoji = stringResource(R.string.fp_audio_section_icon_latency),
            title = stringResource(R.string.fp_audio_section_latency_title),
            signature = fingerprint.latencyProfile.latencySignature,
            description = fingerprint.latencyProfile.latencyClass,
            details = listOf(
                stringResource(R.string.fp_audio_section_input_latency, fingerprint.latencyProfile.inputLatency),
                stringResource(R.string.fp_audio_section_output_latency, fingerprint.latencyProfile.outputLatency),
                stringResource(R.string.fp_audio_section_roundtrip_latency, fingerprint.latencyProfile.roundTripLatency)
            ),
            color = colorResource(R.color.warning_yellow)
        )

        // Codec Support
        AudioDetailCard(
            emoji = stringResource(R.string.fp_audio_section_icon_codec),
            title = stringResource(R.string.fp_audio_section_codec_title),
            signature = fingerprint.codecSupport.codecSignature,
            description = stringResource(R.string.fp_audio_section_codec_desc, fingerprint.codecSupport.sampleRates.size),
            details = buildList {
                add(stringResource(
                    R.string.fp_audio_section_codec_formats,
                    fingerprint.codecSupport.supportedInputFormats.size,
                    fingerprint.codecSupport.supportedOutputFormats.size
                ))
                add(stringResource(R.string.fp_audio_section_sample_rates,
                    fingerprint.codecSupport.sampleRates.joinToString(", ")))
                add(stringResource(R.string.fp_audio_section_channels,
                    fingerprint.codecSupport.channelConfigs.joinToString(", ")))
            },
            color = colorResource(R.color.audio_codec_color)
        )

        // Audio Capabilities
        AudioDetailCard(
            emoji = stringResource(R.string.fp_audio_section_icon_capabilities),
            title = stringResource(R.string.fp_audio_section_capabilities_title),
            signature = fingerprint.audioCapabilities.capabilitiesSignature,
            description = buildString {
                if (fingerprint.audioCapabilities.supportsProAudio) append(stringResource(R.string.fp_audio_section_pro_audio) + ", ")
                if (fingerprint.audioCapabilities.supportsLowLatency) append(stringResource(R.string.fp_audio_section_low_latency) + ", ")
                if (fingerprint.audioCapabilities.supportsMidi) append(stringResource(R.string.fp_audio_section_midi) + ", ")
                if (isEmpty()) append(stringResource(R.string.fp_audio_section_standard_audio))
                else removeSuffix(", ")
            },
            details = listOf(
                stringResource(R.string.fp_audio_section_low_latency_label,
                    stringResource(if (fingerprint.audioCapabilities.supportsLowLatency) R.string.yes else R.string.no)),
                stringResource(R.string.fp_audio_section_pro_audio_label,
                    stringResource(if (fingerprint.audioCapabilities.supportsProAudio) R.string.yes else R.string.no)),
                stringResource(R.string.fp_audio_section_midi_label,
                    stringResource(if (fingerprint.audioCapabilities.supportsMidi) R.string.yes else R.string.no)),
                stringResource(R.string.fp_audio_section_max_input, fingerprint.audioCapabilities.maxInputChannels),
                stringResource(R.string.fp_audio_section_max_output, fingerprint.audioCapabilities.maxOutputChannels)
            ),
            color = colorResource(R.color.danger_red)
        )

        // Uniqueness Factors
        if (fingerprint.uniquenessFactors.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.card_background)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.fp_audio_section_unique_factors),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorResource(R.color.white)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    fingerprint.uniquenessFactors.forEach { factor ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.bullet_point),
                                color = colorResource(R.color.info_blue),
                                fontSize = 11.sp
                            )
                            Text(
                                text = factor,
                                fontSize = 11.sp,
                                color = colorResource(R.color.white).copy(alpha = 0.8f)
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
                        color = colorResource(R.color.white)
                    )
                    Text(
                        text = stringResource(R.string.fp_audio_section_id_label, signature.uppercase()),
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
                color = colorResource(R.color.white).copy(alpha = 0.7f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            details.forEach { detail ->
                Text(
                    text = detail,
                    fontSize = 10.sp,
                    color = colorResource(R.color.white).copy(alpha = 0.6f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}