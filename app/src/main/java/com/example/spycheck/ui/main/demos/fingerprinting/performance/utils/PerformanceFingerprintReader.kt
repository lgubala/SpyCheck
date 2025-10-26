package com.example.spycheck

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.SystemClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureNanoTime

/**
 * Performance Fingerprinting - NO permissions required!
 *
 * How it works:
 * - Every device has unique performance characteristics
 * - CPU speed varies even in same model (manufacturing tolerance)
 * - Memory access patterns differ by device
 * - GPU performance varies (thermal throttling, binning)
 * - Storage I/O is device-specific
 *
 * These benchmarks create a unique performance signature!
 */

data class PerformanceFingerprint(
    val fingerprintId: String,
    val uniquenessScore: String,
    val cpuProfile: CPUPerformanceProfile,
    val memoryProfile: MemoryPerformanceProfile,
    val computeProfile: ComputePerformanceProfile,
    val storageProfile: StoragePerformanceProfile,
    val uniquenessFactors: List<String>
)

data class CPUPerformanceProfile(
    val cores: Int,
    val cpuArchitecture: String,
    val integerBenchmark: Long,      // nanoseconds for integer operations
    val floatingPointBenchmark: Long, // nanoseconds for FP operations
    val performanceClass: String,     // "High-end", "Mid-range", "Budget"
    val cpuSignature: String
)

data class MemoryPerformanceProfile(
    val totalRAM: Long,              // in MB
    val availableRAM: Long,          // in MB
    val memoryReadSpeed: Long,       // operations per millisecond
    val memoryWriteSpeed: Long,      // operations per millisecond
    val memoryClass: String,         // "High", "Medium", "Low"
    val memorySignature: String
)

data class ComputePerformanceProfile(
    val mathBenchmark: Long,         // nanoseconds for complex math
    val stringBenchmark: Long,       // nanoseconds for string operations
    val arrayBenchmark: Long,        // nanoseconds for array operations
    val overallScore: Int,           // 0-1000 performance score
    val computeSignature: String
)

data class StoragePerformanceProfile(
    val storageType: String,         // "UFS 3.1", "UFS 2.1", "eMMC 5.1", etc
    val totalStorage: Long,          // in MB
    val availableStorage: Long,      // in MB
    val ioPerformance: String,       // "Excellent", "Good", "Fair", "Poor"
    val storageSignature: String
)

class PerformanceFingerprintReader(private val context: Context) {

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private val _analysisProgress = MutableStateFlow(0)
    val analysisProgress: StateFlow<Int> = _analysisProgress.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready to benchmark")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    suspend fun analyzePerformance(): PerformanceFingerprint {
        _statusMessage.value = "🔥 Benchmarking CPU..."
        _analysisProgress.value = 15

        val cpuProfile = benchmarkCPU()

        delay(500)
        _statusMessage.value = "🧠 Testing memory speed..."
        _analysisProgress.value = 35

        val memoryProfile = benchmarkMemory()

        delay(500)
        _statusMessage.value = "⚡ Running compute tests..."
        _analysisProgress.value = 55

        val computeProfile = benchmarkCompute()

        delay(500)
        _statusMessage.value = "💾 Measuring storage I/O..."
        _analysisProgress.value = 75

        val storageProfile = analyzeStorage()

        delay(300)
        _statusMessage.value = "✅ Performance fingerprint generated!"
        _analysisProgress.value = 100

        // Generate combined fingerprint ID
        val fingerprintData = buildString {
            append(cpuProfile.cpuSignature)
            append(memoryProfile.memorySignature)
            append(computeProfile.computeSignature)
            append(storageProfile.storageSignature)
        }

        val fingerprintId = hashString(fingerprintData)
        val uniquenessScore = calculatePerformanceUniqueness(cpuProfile, memoryProfile, computeProfile)
        val uniquenessFactors = identifyUniquenessFactors(cpuProfile, memoryProfile, computeProfile, storageProfile)

        return PerformanceFingerprint(
            fingerprintId = fingerprintId,
            uniquenessScore = uniquenessScore,
            cpuProfile = cpuProfile,
            memoryProfile = memoryProfile,
            computeProfile = computeProfile,
            storageProfile = storageProfile,
            uniquenessFactors = uniquenessFactors
        )
    }

    private suspend fun benchmarkCPU(): CPUPerformanceProfile = withContext(Dispatchers.Default) {
        val cores = Runtime.getRuntime().availableProcessors()
        val cpuArch = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"

        // Integer benchmark: Simple arithmetic operations
        val integerTime = measureNanoTime {
            var result = 0L
            repeat(100_000) {
                result += it * 17L
                result -= it / 3L
            }
        }

        // Floating point benchmark: Complex calculations
        val floatingPointTime = measureNanoTime {
            var result = 0.0
            repeat(50_000) {
                result += sqrt(it.toDouble())
                result *= 1.0001
            }
        }

        // Classify performance
        val performanceClass = when {
            integerTime < 5_000_000 && floatingPointTime < 10_000_000 -> "Flagship (Top 5%)"
            integerTime < 10_000_000 && floatingPointTime < 20_000_000 -> "High-end (Top 20%)"
            integerTime < 20_000_000 && floatingPointTime < 40_000_000 -> "Mid-range (Top 50%)"
            else -> "Budget (Below average)"
        }

        val cpuSignature = hashString(
            "$cores:$integerTime:$floatingPointTime:$cpuArch"
        ).take(8)

        CPUPerformanceProfile(
            cores = cores,
            cpuArchitecture = cpuArch,
            integerBenchmark = integerTime,
            floatingPointBenchmark = floatingPointTime,
            performanceClass = performanceClass,
            cpuSignature = cpuSignature
        )
    }

    private suspend fun benchmarkMemory(): MemoryPerformanceProfile = withContext(Dispatchers.Default) {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        val totalRAM = memInfo.totalMem / (1024 * 1024) // Convert to MB
        val availableRAM = memInfo.availMem / (1024 * 1024)

        // Memory read benchmark
        val readSpeed = measureNanoTime {
            val array = IntArray(10_000) { it }
            var sum = 0
            repeat(1_000) {
                sum += array.sum()
            }
        }
        val readOpsPerMs = 10_000_000 / (readSpeed / 1_000_000) // Operations per millisecond

        // Memory write benchmark
        val writeSpeed = measureNanoTime {
            val array = IntArray(10_000)
            repeat(1_000) {
                for (i in array.indices) {
                    array[i] = i
                }
            }
        }
        val writeOpsPerMs = 10_000_000 / (writeSpeed / 1_000_000)

        val memoryClass = when {
            totalRAM > 12_000 -> "Flagship (12GB+)"
            totalRAM > 8_000 -> "High-end (8-12GB)"
            totalRAM > 4_000 -> "Mid-range (4-8GB)"
            else -> "Budget (<4GB)"
        }

        val memorySignature = hashString(
            "$totalRAM:$readOpsPerMs:$writeOpsPerMs"
        ).take(8)

        MemoryPerformanceProfile(
            totalRAM = totalRAM,
            availableRAM = availableRAM,
            memoryReadSpeed = readOpsPerMs,
            memoryWriteSpeed = writeOpsPerMs,
            memoryClass = memoryClass,
            memorySignature = memorySignature
        )
    }

    private suspend fun benchmarkCompute(): ComputePerformanceProfile = withContext(Dispatchers.Default) {
        // Complex math benchmark (like JavaScript performance tests)
        val mathTime = measureNanoTime {
            var result = 1.0
            repeat(10_000) {
                result = sqrt(result * it + 1.0)
                result = result.pow(1.1)
            }
        }

        // String manipulation benchmark
        val stringTime = measureNanoTime {
            var str = ""
            repeat(1_000) {
                str += "a"
                str = str.reversed()
            }
        }

        // Array operations benchmark
        val arrayTime = measureNanoTime {
            val list = mutableListOf<Int>()
            repeat(10_000) {
                list.add(it)
                list.sorted()
            }
        }

        // Calculate overall score (lower time = higher score)
        val totalTime = mathTime + stringTime + arrayTime
        val overallScore = (1_000_000_000_000 / totalTime).toInt().coerceIn(0, 1000)

        val computeSignature = hashString(
            "$mathTime:$stringTime:$arrayTime"
        ).take(8)

        ComputePerformanceProfile(
            mathBenchmark = mathTime,
            stringBenchmark = stringTime,
            arrayBenchmark = arrayTime,
            overallScore = overallScore,
            computeSignature = computeSignature
        )
    }

    private fun analyzeStorage(): StoragePerformanceProfile {
        val statFs = android.os.StatFs(context.filesDir.absolutePath)

        val totalStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            statFs.totalBytes / (1024 * 1024) // Convert to MB
        } else {
            @Suppress("DEPRECATION")
            (statFs.blockCount.toLong() * statFs.blockSize.toLong()) / (1024 * 1024)
        }

        val availableStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            statFs.availableBytes / (1024 * 1024)
        } else {
            @Suppress("DEPRECATION")
            (statFs.availableBlocks.toLong() * statFs.blockSize.toLong()) / (1024 * 1024)
        }

        // Estimate storage type based on total capacity and device info
        val storageType = when {
            totalStorage > 500_000 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> "UFS 3.1" // 512GB+, Android 11+
            totalStorage > 200_000 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> "UFS 3.0" // 256GB+, Android 10+
            totalStorage > 100_000 -> "UFS 2.1" // 128GB+
            totalStorage > 32_000 -> "eMMC 5.1" // 64GB+
            else -> "eMMC 5.0 or older"
        }

        val ioPerformance = when (storageType) {
            "UFS 3.1" -> "Excellent (flagship)"
            "UFS 3.0" -> "Excellent (high-end)"
            "UFS 2.1" -> "Good (mid-range)"
            "eMMC 5.1" -> "Fair (budget)"
            else -> "Poor (old device)"
        }

        val storageSignature = hashString(
            "$totalStorage:$storageType"
        ).take(8)

        return StoragePerformanceProfile(
            storageType = storageType,
            totalStorage = totalStorage,
            availableStorage = availableStorage,
            ioPerformance = ioPerformance,
            storageSignature = storageSignature
        )
    }

    private fun calculatePerformanceUniqueness(
        cpu: CPUPerformanceProfile,
        memory: MemoryPerformanceProfile,
        compute: ComputePerformanceProfile
    ): String {
        var uniqueness = 1.0

        // CPU benchmark uniqueness (each device performs slightly differently)
        // Even same model has ±10% variance due to silicon lottery
        uniqueness *= (cpu.integerBenchmark / 1_000_000.0 * 50.0)

        // Memory speed variance
        uniqueness *= (memory.memoryReadSpeed / 100.0)

        // Overall compute score
        uniqueness *= (compute.overallScore * 2.0)

        // Core count contribution
        uniqueness *= when (cpu.cores) {
            1, 2 -> 10.0   // Very old
            4 -> 50.0      // Common
            6 -> 100.0     // Mid-range
            8 -> 200.0     // High-end
            else -> 300.0  // Flagship
        }

        val finalScore = uniqueness.toLong()

        return when {
            finalScore > 1_000_000 -> "1 in ${finalScore / 1_000_000} million"
            finalScore > 1_000 -> "1 in ${finalScore / 1_000}K"
            else -> "1 in $finalScore"
        }
    }

    private fun identifyUniquenessFactors(
        cpu: CPUPerformanceProfile,
        memory: MemoryPerformanceProfile,
        compute: ComputePerformanceProfile,
        storage: StoragePerformanceProfile
    ): List<String> {
        val factors = mutableListOf<String>()

        if (cpu.cores >= 8) {
            factors.add("8+ CPU cores - flagship device")
        }

        if (cpu.integerBenchmark < 10_000_000) {
            factors.add("Fast CPU performance (${cpu.integerBenchmark / 1_000_000}ms) - top 10% of devices")
        }

        if (memory.totalRAM > 8_000) {
            factors.add("High RAM (${memory.totalRAM / 1024}GB) - premium device")
        }

        if (storage.storageType.contains("UFS 3")) {
            factors.add("${storage.storageType} storage - flagship speed")
        }

        if (compute.overallScore > 500) {
            factors.add("High compute score (${compute.overallScore}/1000) - excellent performance")
        }

        factors.add("Benchmark variance: Each device performs slightly differently due to 'silicon lottery'")

        if (factors.size == 1) {
            factors.add("Standard performance configuration (still uniquely identifiable by benchmark timings)")
        }

        return factors
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}