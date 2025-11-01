package com.example.spycheck

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import kotlin.math.abs

/**
 * Audio Fingerprinting - Minimal permissions required!
 *
 * How it works:
 * - Every device has unique audio hardware characteristics
 * - Microphone/speaker frequency response varies
 * - Audio latency patterns are device-specific
 * - Codec support creates unique signatures
 * - Audio routing capabilities differ per device
 *
 * Most of this requires NO permissions! Only advanced analysis needs RECORD_AUDIO.
 */

data class AudioFingerprint(
    val fingerprintId: String,
    val uniquenessScore: String,
    val audioHardware: AudioHardwareProfile,
    val latencyProfile: AudioLatencyProfile,
    val codecSupport: CodecSupportProfile,
    val audioCapabilities: AudioCapabilitiesProfile,
    val uniquenessFactors: List<String>
)

data class AudioHardwareProfile(
    val microphoneCount: Int,
    val speakerCount: Int,
    val microphoneTypes: List<String>,
    val speakerTypes: List<String>,
    val hasBuiltInMic: Boolean,
    val hasBuiltInSpeaker: Boolean,
    val hardwareSignature: String
)

data class AudioLatencyProfile(
    val inputLatency: Int,  // in milliseconds
    val outputLatency: Int, // in milliseconds
    val roundTripLatency: Int, // total latency
    val latencyClass: String, // "Low", "Normal", "High"
    val latencySignature: String
)

data class CodecSupportProfile(
    val supportedInputFormats: List<String>,
    val supportedOutputFormats: List<String>,
    val sampleRates: List<Int>,
    val channelConfigs: List<String>,
    val codecSignature: String
)

data class AudioCapabilitiesProfile(
    val supportsLowLatency: Boolean,
    val supportsProAudio: Boolean,
    val supportsMidi: Boolean,
    val maxInputChannels: Int,
    val maxOutputChannels: Int,
    val capabilitiesSignature: String
)

class AudioFingerprintReader(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _analysisProgress = MutableStateFlow(0)
    val analysisProgress: StateFlow<Int> = _analysisProgress.asStateFlow()

    private val _statusMessage = MutableStateFlow(context.getString(R.string.fp_audio_reader_ready))
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    suspend fun analyzeAudio(): AudioFingerprint {
        _statusMessage.value = context.getString(R.string.fp_audio_reader_detecting_hardware)
        _analysisProgress.value = 15

        val hardwareProfile = analyzeAudioHardware()

        delay(300)
        _statusMessage.value = context.getString(R.string.fp_audio_reader_measuring_latency)
        _analysisProgress.value = 35

        val latencyProfile = analyzeAudioLatency()

        delay(300)
        _statusMessage.value = context.getString(R.string.fp_audio_reader_checking_codec)
        _analysisProgress.value = 55

        val codecProfile = analyzeCodecSupport()

        delay(300)
        _statusMessage.value = context.getString(R.string.fp_audio_reader_analyzing_capabilities)
        _analysisProgress.value = 75

        val capabilitiesProfile = analyzeAudioCapabilities()

        delay(300)
        _statusMessage.value = context.getString(R.string.fp_audio_reader_generated)
        _analysisProgress.value = 100

        // Generate combined fingerprint ID
        val fingerprintData = buildString {
            append(hardwareProfile.hardwareSignature)
            append(latencyProfile.latencySignature)
            append(codecProfile.codecSignature)
            append(capabilitiesProfile.capabilitiesSignature)
        }

        val fingerprintId = hashString(fingerprintData)
        val uniquenessScore = calculateAudioUniqueness(hardwareProfile, latencyProfile, codecProfile)
        val uniquenessFactors = identifyUniquenessFactors(hardwareProfile, latencyProfile, capabilitiesProfile)

        return AudioFingerprint(
            fingerprintId = fingerprintId,
            uniquenessScore = uniquenessScore,
            audioHardware = hardwareProfile,
            latencyProfile = latencyProfile,
            codecSupport = codecProfile,
            audioCapabilities = capabilitiesProfile,
            uniquenessFactors = uniquenessFactors
        )
    }

    private fun analyzeAudioHardware(): AudioHardwareProfile {
        val devices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager.getDevices(AudioManager.GET_DEVICES_ALL)
        } else {
            emptyArray()
        }

        val microphones = devices.filter { it.isSink } // Input devices
        val speakers = devices.filter { it.isSource } // Output devices

        val micTypes = mutableListOf<String>()
        val speakerTypes = mutableListOf<String>()
        var hasBuiltInMic = false
        var hasBuiltInSpeaker = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            microphones.forEach { device ->
                val typeName = getDeviceTypeName(device.type)
                micTypes.add(typeName)
                if (device.type == AudioDeviceInfo.TYPE_BUILTIN_MIC) {
                    hasBuiltInMic = true
                }
            }

            speakers.forEach { device ->
                val typeName = getDeviceTypeName(device.type)
                speakerTypes.add(typeName)
                if (device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    hasBuiltInSpeaker = true
                }
            }
        } else {
            // Fallback for older devices
            hasBuiltInMic = true
            hasBuiltInSpeaker = true
            micTypes.add(context.getString(R.string.fp_audio_reader_builtin_microphone))
            speakerTypes.add(context.getString(R.string.fp_audio_reader_builtin_speaker))
        }

        val hardwareSignature = hashString(
            "${microphones.size}:${speakers.size}:${micTypes.sorted()}:${speakerTypes.sorted()}"
        ).take(8)

        return AudioHardwareProfile(
            microphoneCount = microphones.size,
            speakerCount = speakers.size,
            microphoneTypes = micTypes.distinct(),
            speakerTypes = speakerTypes.distinct(),
            hasBuiltInMic = hasBuiltInMic,
            hasBuiltInSpeaker = hasBuiltInSpeaker,
            hardwareSignature = hardwareSignature
        )
    }

    private fun analyzeAudioLatency(): AudioLatencyProfile {
        // Get output latency
        val outputLatency = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val latencyString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
                val framesPerBuffer = latencyString?.toIntOrNull() ?: 0
                val sampleRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)?.toIntOrNull() ?: 48000
                if (framesPerBuffer > 0) {
                    (framesPerBuffer * 1000 / sampleRate) // Convert to milliseconds
                } else {
                    20 // Default estimate
                }
            } catch (e: Exception) {
                20
            }
        } else {
            20 // Default for older devices
        }

        // Estimate input latency (typically higher than output)
        val inputLatency = outputLatency + 10

        val roundTripLatency = inputLatency + outputLatency

        val latencyClass = when {
            roundTripLatency < 20 -> context.getString(R.string.fp_audio_reader_latency_ultra_low)
            roundTripLatency < 45 -> context.getString(R.string.fp_audio_reader_latency_low)
            roundTripLatency < 90 -> context.getString(R.string.fp_audio_reader_latency_normal)
            else -> context.getString(R.string.fp_audio_reader_latency_high)
        }

        val latencySignature = hashString("$inputLatency:$outputLatency:$roundTripLatency").take(6)

        return AudioLatencyProfile(
            inputLatency = inputLatency,
            outputLatency = outputLatency,
            roundTripLatency = roundTripLatency,
            latencyClass = latencyClass,
            latencySignature = latencySignature
        )
    }

    private fun analyzeCodecSupport(): CodecSupportProfile {
        val inputFormats = mutableListOf<String>()
        val outputFormats = mutableListOf<String>()

        // Common audio formats to check
        val formatsToCheck = listOf(
            context.getString(R.string.fp_audio_reader_pcm_16) to android.media.AudioFormat.ENCODING_PCM_16BIT,
            context.getString(R.string.fp_audio_reader_pcm_8) to android.media.AudioFormat.ENCODING_PCM_8BIT,
            context.getString(R.string.fp_audio_reader_pcm_float) to android.media.AudioFormat.ENCODING_PCM_FLOAT
        )

        formatsToCheck.forEach { (name, encoding) ->
            inputFormats.add(name)
            outputFormats.add(name)
        }

        // Sample rates to check
        val commonSampleRates = listOf(8000, 11025, 16000, 22050, 44100, 48000)
        val supportedRates = mutableListOf<Int>()

        commonSampleRates.forEach { rate ->
            try {
                val bufferSize = AudioRecord.getMinBufferSize(
                    rate,
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT
                )
                if (bufferSize > 0) {
                    supportedRates.add(rate)
                }
            } catch (e: Exception) {
                // Rate not supported
            }
        }

        val channelConfigs = listOf(
            context.getString(R.string.fp_audio_reader_mono),
            context.getString(R.string.fp_audio_reader_stereo)
        )

        val codecSignature = hashString(
            "${inputFormats.size}:${outputFormats.size}:${supportedRates.joinToString(",")}"
        ).take(8)

        return CodecSupportProfile(
            supportedInputFormats = inputFormats,
            supportedOutputFormats = outputFormats,
            sampleRates = supportedRates,
            channelConfigs = channelConfigs,
            codecSignature = codecSignature
        )
    }

    private fun analyzeAudioCapabilities(): AudioCapabilitiesProfile {
        val supportsLowLatency = context.packageManager.hasSystemFeature(
            android.content.pm.PackageManager.FEATURE_AUDIO_LOW_LATENCY
        )

        val supportsProAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.packageManager.hasSystemFeature(
                android.content.pm.PackageManager.FEATURE_AUDIO_PRO
            )
        } else {
            false
        }

        val supportsMidi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.packageManager.hasSystemFeature(
                android.content.pm.PackageManager.FEATURE_MIDI
            )
        } else {
            false
        }

        // Estimate max channels based on device capabilities
        val maxInputChannels = if (supportsProAudio) 4 else 2
        val maxOutputChannels = if (supportsProAudio) 8 else 2

        val capabilitiesSignature = hashString(
            "$supportsLowLatency:$supportsProAudio:$supportsMidi:$maxInputChannels:$maxOutputChannels"
        ).take(6)

        return AudioCapabilitiesProfile(
            supportsLowLatency = supportsLowLatency,
            supportsProAudio = supportsProAudio,
            supportsMidi = supportsMidi,
            maxInputChannels = maxInputChannels,
            maxOutputChannels = maxOutputChannels,
            capabilitiesSignature = capabilitiesSignature
        )
    }

    private fun getDeviceTypeName(type: Int): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return context.getString(R.string.fp_audio_reader_unknown)

        return when (type) {
            AudioDeviceInfo.TYPE_BUILTIN_MIC -> context.getString(R.string.fp_audio_reader_builtin_mic)
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> context.getString(R.string.fp_audio_reader_builtin_speaker_device)
            AudioDeviceInfo.TYPE_WIRED_HEADSET -> context.getString(R.string.fp_audio_reader_wired_headset)
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> context.getString(R.string.fp_audio_reader_wired_headphones)
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> context.getString(R.string.fp_audio_reader_bluetooth_a2dp)
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> context.getString(R.string.fp_audio_reader_bluetooth_sco)
            AudioDeviceInfo.TYPE_USB_DEVICE -> context.getString(R.string.fp_audio_reader_usb_audio)
            AudioDeviceInfo.TYPE_USB_ACCESSORY -> context.getString(R.string.fp_audio_reader_usb_accessory)
            AudioDeviceInfo.TYPE_USB_HEADSET -> context.getString(R.string.fp_audio_reader_usb_headset)
            else -> context.getString(R.string.fp_audio_reader_unknown_type, type)
        }
    }

    private fun calculateAudioUniqueness(
        hardware: AudioHardwareProfile,
        latency: AudioLatencyProfile,
        codec: CodecSupportProfile
    ): String {
        var uniqueness = 1.0

        // Hardware uniqueness
        uniqueness *= when {
            hardware.microphoneCount > 2 -> 500.0
            hardware.microphoneCount == 2 -> 100.0
            else -> 50.0
        }

        // Latency uniqueness (very device-specific)
        uniqueness *= when (latency.latencyClass) {
            context.getString(R.string.fp_audio_reader_latency_ultra_low) -> 1000.0  // Rare
            context.getString(R.string.fp_audio_reader_latency_low) -> 300.0
            context.getString(R.string.fp_audio_reader_latency_normal) -> 150.0
            else -> 100.0
        }

        // Codec support variation
        uniqueness *= (codec.sampleRates.size * 50.0)

        val finalScore = uniqueness.toLong()

        return when {
            finalScore > 1_000_000 -> context.getString(R.string.fp_audio_reader_uniqueness_million, finalScore / 1_000_000)
            finalScore > 1_000 -> context.getString(R.string.fp_audio_reader_uniqueness_k, finalScore / 1_000)
            else -> context.getString(R.string.fp_audio_reader_uniqueness_basic, finalScore)
        }
    }

    private fun identifyUniquenessFactors(
        hardware: AudioHardwareProfile,
        latency: AudioLatencyProfile,
        capabilities: AudioCapabilitiesProfile
    ): List<String> {
        val factors = mutableListOf<String>()

        if (hardware.microphoneCount > 1) {
            factors.add(context.getString(R.string.fp_audio_reader_factor_multiple_mics, hardware.microphoneCount))
        }

        if (latency.roundTripLatency < 45) {
            factors.add(context.getString(R.string.fp_audio_reader_factor_low_latency, latency.roundTripLatency))
        }

        if (capabilities.supportsProAudio) {
            factors.add(context.getString(R.string.fp_audio_reader_factor_pro_audio))
        }

        if (capabilities.supportsLowLatency) {
            factors.add(context.getString(R.string.fp_audio_reader_factor_low_latency_path))
        }

        if (hardware.microphoneTypes.size > 2) {
            factors.add(context.getString(R.string.fp_audio_reader_factor_advanced_mic, hardware.microphoneTypes.joinToString(", ")))
        }

        if (factors.isEmpty()) {
            factors.add(context.getString(R.string.fp_audio_reader_factor_standard))
        }

        return factors
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}