package com.example.spycheck

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import android.webkit.WebView
import java.security.MessageDigest
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

data class DeviceFingerprint(
    val fingerprintId: String,
    val uniquenessScore: String, // "1 in X" format
    val installedApps: InstalledAppsProfile,
    val screenMetrics: ScreenMetrics,
    val systemFonts: FontProfile,
    val deviceInfo: DeviceInfo,
    val hardwareInfo: HardwareInfo,
    val uniquenessFactors: List<UniquenessFactor>
)

data class InstalledAppsProfile(
    val totalApps: Int,
    val categories: Map<String, List<String>>, // category -> app names
    val personalityProfile: String,
    val uniqueApps: List<String>, // Less common apps that increase uniqueness
    val appSignature: String // Hash of all apps
)

data class ScreenMetrics(
    val widthPixels: Int,
    val heightPixels: Int,
    val densityDpi: Int,
    val xdpi: Float,
    val ydpi: Float,
    val scaledDensity: Float,
    val diagonalInches: Double,
    val aspectRatio: String,
    val refreshRate: Float,
    val uniquenessContribution: String
)

data class FontProfile(
    val systemFonts: List<String>,
    val customFonts: List<String>,
    val totalFonts: Int,
    val fontSignature: String,
    val professionalIndicators: List<String> // "Design", "Development", "Language-specific"
)

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val apiLevel: Int,
    val buildFingerprint: String,
    val userAgent: String,
    val androidId: String, // Hashed for privacy
    val timezone: String,
    val language: String
)

data class HardwareInfo(
    val cpuCores: Int,
    val totalRam: String,
    val availableRam: String,
    val totalStorage: String,
    val availableStorage: String,
    val isRooted: Boolean,
    val hasNfc: Boolean,
    val hasBluetooth: Boolean,
    val hasGyroscope: Boolean
)

data class UniquenessFactor(
    val category: String,
    val value: String,
    val rarity: String // "Common", "Uncommon", "Rare", "Unique"
)

class DeviceFingerprintReader(private val context: Context) {

    companion object {
        // App categories for profiling
        private val DATING_APPS = mapOf(
            "com.tinder" to "Tinder",
            "com.bumble.app" to "Bumble",
            "com.hinge.app" to "Hinge",
            "com.coffeemeetsbagel.coffeemeetsbagel" to "Coffee Meets Bagel"
        )

        private val FINANCE_APPS = mapOf(
            "com.robinhood.android" to "Robinhood",
            "com.coinbase.android" to "Coinbase",
            "com.paypal.android.p2pmobile" to "PayPal",
            "com.venmo" to "Venmo",
            "com.cashapp" to "Cash App"
        )

        private val PRODUCTIVITY_APPS = mapOf(
            "com.notion.id" to "Notion",
            "com.todoist" to "Todoist",
            "com.microsoft.todos" to "Microsoft To Do",
            "com.evernote" to "Evernote",
            "com.trello" to "Trello"
        )

        private val GAMING_APPS = mapOf(
            "com.supercell.clashofclans" to "Clash of Clans",
            "com.roblox.client" to "Roblox",
            "com.mojang.minecraftpe" to "Minecraft",
            "com.pubg.mobile" to "PUBG",
            "com.activision.callofduty.shooter" to "Call of Duty"
        )

        private val DEVELOPER_APPS = mapOf(
            "com.github.android" to "GitHub",
            "com.termux" to "Termux",
            "com.microsoft.vscode" to "VS Code",
            "com.jetbrains.droidaide" to "AIDE",
            "com.sololearn" to "Sololearn"
        )

        private val PRIVACY_APPS = mapOf(
            "org.torproject.torbrowser" to "Tor Browser",
            "com.protonvpn.android" to "ProtonVPN",
            "net.mullvad.mullvadvpn" to "Mullvad VPN",
            "org.mozilla.firefox" to "Firefox",
            "com.duckduckgo.mobile.android" to "DuckDuckGo"
        )

        // Fonts that indicate profession/interests
        private val DESIGN_FONTS = setOf("Comic Sans MS", "Helvetica Neue", "Futura", "Gill Sans")
        private val DEVELOPER_FONTS = setOf("Fira Code", "Source Code Pro", "Monaco", "Consolas")
        private val LANGUAGE_FONTS = setOf("Noto Sans CJK", "Noto Sans Arabic", "Noto Sans Hebrew")
    }

    fun generateDeviceFingerprint(): DeviceFingerprint {
        val installedApps = profileInstalledApps()
        val screenMetrics = captureScreenMetrics()
        val fonts = analyzeFonts()
        val deviceInfo = collectDeviceInfo()
        val hardwareInfo = collectHardwareInfo()

        // Generate unique fingerprint ID
        val fingerprintData = buildString {
            append(installedApps.appSignature)
            append(screenMetrics.widthPixels)
            append(screenMetrics.heightPixels)
            append(screenMetrics.densityDpi)
            append(fonts.fontSignature)
            append(deviceInfo.buildFingerprint)
            append(hardwareInfo.cpuCores)
            append(hardwareInfo.totalRam)
        }

        val fingerprintId = hashString(fingerprintData)
        val uniquenessScore = calculateUniqueness(installedApps, screenMetrics, fonts, deviceInfo, hardwareInfo)
        val uniquenessFactors = identifyUniquenessFactors(installedApps, screenMetrics, fonts, deviceInfo, hardwareInfo)

        return DeviceFingerprint(
            fingerprintId = fingerprintId,
            uniquenessScore = uniquenessScore,
            installedApps = installedApps,
            screenMetrics = screenMetrics,
            systemFonts = fonts,
            deviceInfo = deviceInfo,
            hardwareInfo = hardwareInfo,
            uniquenessFactors = uniquenessFactors
        )
    }

    private fun profileInstalledApps(): InstalledAppsProfile {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val categories = mutableMapOf<String, MutableList<String>>()
        val uniqueApps = mutableListOf<String>()
        val allAppPackages = mutableListOf<String>()

        // Categorize apps
        installedApps.forEach { app ->
            val packageName = app.packageName
            allAppPackages.add(packageName)

            // Skip system apps for profiling
            if (app.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = pm.getApplicationLabel(app).toString()

                when {
                    DATING_APPS.containsKey(packageName) -> {
                        categories.getOrPut("💔 Dating") { mutableListOf() }.add(DATING_APPS[packageName]!!)
                        uniqueApps.add("Dating: ${DATING_APPS[packageName]}")
                    }
                    FINANCE_APPS.containsKey(packageName) -> {
                        categories.getOrPut("💰 Finance") { mutableListOf() }.add(FINANCE_APPS[packageName]!!)
                        uniqueApps.add("Finance: ${FINANCE_APPS[packageName]}")
                    }
                    GAMING_APPS.containsKey(packageName) -> {
                        categories.getOrPut("🎮 Gaming") { mutableListOf() }.add(GAMING_APPS[packageName]!!)
                    }
                    DEVELOPER_APPS.containsKey(packageName) -> {
                        categories.getOrPut("👩‍💻 Developer") { mutableListOf() }.add(DEVELOPER_APPS[packageName]!!)
                        uniqueApps.add("Dev: ${DEVELOPER_APPS[packageName]}")
                    }
                    PRIVACY_APPS.containsKey(packageName) -> {
                        categories.getOrPut("🔒 Privacy") { mutableListOf() }.add(PRIVACY_APPS[packageName]!!)
                        uniqueApps.add("Privacy: ${PRIVACY_APPS[packageName]}")
                    }
                    PRODUCTIVITY_APPS.containsKey(packageName) -> {
                        categories.getOrPut("📊 Productivity") { mutableListOf() }.add(PRODUCTIVITY_APPS[packageName]!!)
                    }
                    packageName.contains("facebook") || packageName.contains("instagram") ||
                            packageName.contains("twitter") || packageName.contains("tiktok") -> {
                        categories.getOrPut("📱 Social Media") { mutableListOf() }.add(appName)
                    }
                    packageName.contains("bank") || packageName.contains("pay") -> {
                        categories.getOrPut("🏦 Banking") { mutableListOf() }.add(appName)
                    }
                }
            }
        }

        // Generate personality profile
        val profile = generatePersonalityProfile(categories)

        // Create signature hash from all apps
        val appSignature = hashString(allAppPackages.sorted().joinToString(","))

        return InstalledAppsProfile(
            totalApps = installedApps.size,
            categories = categories,
            personalityProfile = profile,
            uniqueApps = uniqueApps.take(10), // Top 10 unique apps
            appSignature = appSignature.take(12) // Shortened hash
        )
    }

    private fun generatePersonalityProfile(categories: Map<String, List<String>>): String {
        val profiles = mutableListOf<String>()

        categories.forEach { (category, apps) ->
            when (category) {
                "💔 Dating" -> {
                    if (apps.size > 2) profiles.add("Serial dater (${apps.size} dating apps!)")
                    else if (apps.size > 0) profiles.add("Actively dating")
                }
                "💰 Finance" -> {
                    if (apps.any { it.contains("Robinhood") || it.contains("Coinbase") })
                        profiles.add("Crypto/Stock trader")
                    if (apps.size > 3) profiles.add("Financially focused")
                }
                "🎮 Gaming" -> {
                    if (apps.size > 5) profiles.add("Hardcore gamer")
                    else if (apps.size > 2) profiles.add("Casual gamer")
                }
                "👩‍💻 Developer" -> profiles.add("Software developer")
                "🔒 Privacy" -> profiles.add("Privacy-conscious")
                "📱 Social Media" -> {
                    if (apps.size > 4) profiles.add("Social media addict")
                    else if (apps.size > 2) profiles.add("Socially active")
                }
            }
        }

        return when {
            profiles.isEmpty() -> "Minimalist user"
            profiles.size == 1 -> profiles[0]
            else -> profiles.take(2).joinToString(" + ")
        }
    }

    private fun captureScreenMetrics(): ScreenMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)

        // Real metrics for uniqueness
        val realMetrics = DisplayMetrics()
        display.getRealMetrics(realMetrics)

        // Calculate diagonal screen size
        val widthInches = realMetrics.widthPixels / realMetrics.xdpi
        val heightInches = realMetrics.heightPixels / realMetrics.ydpi
        val diagonalInches = sqrt(widthInches.pow(2) + heightInches.pow(2)).toDouble()


        // Calculate aspect ratio
        val gcd = gcd(realMetrics.widthPixels, realMetrics.heightPixels)
        val aspectRatio = "${realMetrics.widthPixels / gcd}:${realMetrics.heightPixels / gcd}"

        // Get refresh rate
        val refreshRate = display.refreshRate

        // Determine uniqueness contribution
        val uniqueness = when {
            realMetrics.widthPixels * realMetrics.heightPixels > 2000 * 3000 -> "Rare high-res"
            aspectRatio !in listOf("16:9", "18:9", "19:9", "20:9") -> "Unique aspect ratio"
            refreshRate > 90 -> "High refresh (${refreshRate.toInt()}Hz)"
            diagonalInches < 5.5 || diagonalInches > 7.0 -> "Unusual size"
            else -> "Common display"
        }

        return ScreenMetrics(
            widthPixels = realMetrics.widthPixels,
            heightPixels = realMetrics.heightPixels,
            densityDpi = realMetrics.densityDpi,
            xdpi = realMetrics.xdpi,
            ydpi = realMetrics.ydpi,
            scaledDensity = metrics.scaledDensity,
            diagonalInches = diagonalInches,
            aspectRatio = aspectRatio,
            refreshRate = refreshRate,
            uniquenessContribution = uniqueness
        )
    }

    private fun analyzeFonts(): FontProfile {
        val systemFonts = mutableListOf<String>()
        val customFonts = mutableListOf<String>()
        val professionalIndicators = mutableListOf<String>()

        // Get system fonts
        val fontFiles = File("/system/fonts").listFiles()
        fontFiles?.forEach { file ->
            val fontName = file.name.removeSuffix(".ttf").removeSuffix(".otf")
            systemFonts.add(fontName)

            // Check for professional indicators
            when {
                DESIGN_FONTS.any { fontName.contains(it, ignoreCase = true) } ->
                    professionalIndicators.add("Design professional")
                DEVELOPER_FONTS.any { fontName.contains(it, ignoreCase = true) } ->
                    professionalIndicators.add("Developer")
                LANGUAGE_FONTS.any { fontName.contains(it, ignoreCase = true) } ->
                    professionalIndicators.add("Multilingual user")
            }
        }

        // Check for custom fonts in app directories
        val customFontDirs = listOf(
            File(context.filesDir, "fonts"),
            File(context.getExternalFilesDir(null), "fonts")
        )

        customFontDirs.forEach { dir ->
            dir.listFiles()?.forEach { file ->
                customFonts.add(file.name)
            }
        }

        val fontSignature = hashString(systemFonts.sorted().joinToString(","))

        return FontProfile(
            systemFonts = systemFonts.take(20), // Top 20 for display
            customFonts = customFonts,
            totalFonts = systemFonts.size + customFonts.size,
            fontSignature = fontSignature.take(8),
            professionalIndicators = professionalIndicators
        )
    }

    private fun collectDeviceInfo(): DeviceInfo {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val userAgent = try {
            WebView(context).settings.userAgentString
        } catch (e: Exception) {
            "WebView unavailable"
        }

        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            buildFingerprint = Build.FINGERPRINT,
            userAgent = userAgent,
            androidId = hashString(androidId).take(8), // Hash for privacy
            timezone = java.util.TimeZone.getDefault().id,
            language = java.util.Locale.getDefault().toString()
        )
    }

    private fun collectHardwareInfo(): HardwareInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        val totalRam = memInfo.totalMem / (1024 * 1024 * 1024) // Convert to GB
        val availableRam = memInfo.availMem / (1024 * 1024 * 1024)

        // Storage info
        val statFs = android.os.StatFs(android.os.Environment.getDataDirectory().path)
        val totalStorage = (statFs.blockCountLong * statFs.blockSizeLong) / (1024 * 1024 * 1024)
        val availableStorage = (statFs.availableBlocksLong * statFs.blockSizeLong) / (1024 * 1024 * 1024)

        // CPU info
        val cpuCores = Runtime.getRuntime().availableProcessors()

        // Check for root
        val isRooted = checkForRoot()

        // Hardware features
        val pm = context.packageManager
        val hasNfc = pm.hasSystemFeature(PackageManager.FEATURE_NFC)
        val hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        val hasGyroscope = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)

        return HardwareInfo(
            cpuCores = cpuCores,
            totalRam = "${totalRam}GB",
            availableRam = "${availableRam}GB",
            totalStorage = "${totalStorage}GB",
            availableStorage = "${availableStorage}GB",
            isRooted = isRooted,
            hasNfc = hasNfc,
            hasBluetooth = hasBluetooth,
            hasGyroscope = hasGyroscope
        )
    }

    private fun checkForRoot(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su"
        )

        return paths.any { File(it).exists() }
    }

    private fun calculateUniqueness(
        apps: InstalledAppsProfile,
        screen: ScreenMetrics,
        fonts: FontProfile,
        device: DeviceInfo,
        hardware: HardwareInfo
    ): String {
        // Simplified uniqueness calculation based on various factors
        var uniquenessScore = 1.0

        // Apps contribute most to uniqueness
        uniquenessScore *= (apps.totalApps * 0.8)
        uniquenessScore *= if (apps.uniqueApps.size > 5) 10.0 else 2.0

        // Screen metrics
        val commonResolutions = listOf(1080 * 1920, 1080 * 2340, 1440 * 2960)
        if (screen.widthPixels * screen.heightPixels !in commonResolutions) {
            uniquenessScore *= 5.0
        }

        // Fonts
        uniquenessScore *= (fonts.totalFonts * 0.3)

        // Hardware
        if (hardware.isRooted) uniquenessScore *= 50.0
        if (!hardware.hasNfc) uniquenessScore *= 2.0

        // Convert to readable format
        val finalScore = uniquenessScore.toInt()
        return when {
            finalScore > 1000000 -> "1 in ${finalScore / 1000000} million"
            finalScore > 1000 -> "1 in ${finalScore / 1000}K"
            else -> "1 in $finalScore"
        }
    }

    private fun identifyUniquenessFactors(
        apps: InstalledAppsProfile,
        screen: ScreenMetrics,
        fonts: FontProfile,
        device: DeviceInfo,
        hardware: HardwareInfo
    ): List<UniquenessFactor> {
        val factors = mutableListOf<UniquenessFactor>()

        // App-based factors
        if (apps.uniqueApps.isNotEmpty()) {
            factors.add(UniquenessFactor(
                category = "Apps",
                value = "${apps.uniqueApps.size} unique apps detected",
                rarity = if (apps.uniqueApps.size > 5) "Rare" else "Uncommon"
            ))
        }

        // Screen factors
        factors.add(UniquenessFactor(
            category = "Display",
            value = "${screen.widthPixels}×${screen.heightPixels} @ ${screen.densityDpi}dpi",
            rarity = if (screen.uniquenessContribution.contains("Rare")) "Rare" else "Common"
        ))

        // Font factors
        if (fonts.professionalIndicators.isNotEmpty()) {
            factors.add(UniquenessFactor(
                category = "Fonts",
                value = fonts.professionalIndicators.first(),
                rarity = "Uncommon"
            ))
        }

        // Hardware factors
        if (hardware.isRooted) {
            factors.add(UniquenessFactor(
                category = "System",
                value = "Rooted device detected",
                rarity = "Unique"
            ))
        }

        // Device factors
        factors.add(UniquenessFactor(
            category = "Device",
            value = "${device.manufacturer} ${device.model}",
            rarity = "Common"
        ))

        return factors
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }
}