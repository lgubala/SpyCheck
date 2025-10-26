package com.example.spycheck.ui.main.demos.sneaky.usage_stats.utils

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.abs

data class UsageInsights(
    val dailyRoutine: DailyRoutine,
    val sleepAnalysis: SleepAnalysis,
    val appAddictions: List<AppAddiction>,
    val workLifeBalance: WorkLifeBalance,
    val mentalHealthIndicators: MentalHealthIndicators,
    val rawStats: Map<String, Long> // package name to usage time in ms
)

data class DailyRoutine(
    val wakeUpTime: String,
    val sleepTime: String,
    val mostActiveHour: String,
    val phoneCheckCount: Int,
    val firstAppOfDay: String,
    val lastAppBeforeSleep: String,
    val routineDescription: String
)

data class SleepAnalysis(
    val lastNightSleepDuration: Float, // in hours
    val sleepQualityScore: Int, // 0-100
    val midnightUsage: Long, // minutes used between midnight-6am
    val timesWokeUpToCheckPhone: Int,
    val verdict: String
)

data class AppAddiction(
    val appName: String,
    val packageName: String,
    val dailyUsage: Long, // in minutes
    val sessionCount: Int,
    val longestSession: Long, // in minutes
    val addictionLevel: String, // "Severe", "Moderate", "Healthy"
    val concerningPattern: String
)

data class WorkLifeBalance(
    val workAppUsage: Long, // minutes
    val entertainmentUsage: Long, // minutes
    val productivityScore: Int, // 0-100
    val workHoursViolations: List<String>, // e.g., "Instagram used 47 times during work hours"
    val balanceVerdict: String
)

data class MentalHealthIndicators(
    val doomScrollingSessions: Int,
    val lateNightSocialMedia: Long, // minutes after 11pm
    val anxietyIndicators: List<String>,
    val moodAffectingApps: List<String>,
    val overallRiskLevel: String // "High", "Medium", "Low"
)

class UsageStatsReader(private val context: Context) {

    companion object {
        // Real app categories based on common packages
        private val SOCIAL_MEDIA_APPS = setOf(
            "com.instagram.android", "com.facebook.katana", "com.twitter.android",
            "com.tiktok", "com.snapchat.android", "com.reddit.frontpage",
            "com.discord", "com.pinterest", "com.tumblr", "com.linkedin.android",
            "com.zhiliaoapp.musically", "com.facebook.orca", "com.whatsapp",
            "com.telegram.messenger", "com.viber.voip", "com.skype.raider"
        )

        private val WORK_APPS = setOf(
            "com.microsoft.teams", "com.slack", "com.microsoft.office.outlook",
            "com.google.android.gm", "com.microsoft.office.word", "com.microsoft.office.excel",
            "com.zoom.videomeetings", "com.google.android.apps.meetings", "com.notion.id",
            "com.asana.app", "com.monday.monday", "com.trello", "com.todoist",
            "com.anydo", "com.microsoft.todos", "com.google.android.apps.docs",
            "com.google.android.apps.docs.editors.sheets", "com.google.android.apps.docs.editors.docs"
        )

        private val ENTERTAINMENT_APPS = setOf(
            "com.netflix.mediaclient", "com.google.android.youtube", "com.spotify.music",
            "com.hbo.hbonow", "com.disney.disneyplus", "com.amazon.avod.thirdpartyclient",
            "tv.twitch.android.app", "com.valvesoftware.android.steam.community",
            "com.google.android.youtube.tv", "com.peacocktv.peacockandroid",
            "com.apple.atv", "com.paramount.plus", "com.soundcloud.android"
        )

        private val DATING_APPS = setOf(
            "com.tinder", "com.bumble.app", "com.hinge.app", "com.okcupid.okcupid",
            "com.coffeemeetsbagel.coffeemeetsbagel", "com.match.android.matchmobile",
            "com.badoo.mobile", "com.ftw_and_co.happn", "com.pof.android",
            "com.zoosk.zoosk", "com.eharmony", "com.lovoo.android"
        )

        private val GAMING_APPS = setOf(
            "com.supercell.clashofclans", "com.king.candycrushsaga", "com.roblox.client",
            "com.pubg.mobile", "com.activision.callofduty.shooter", "com.ea.games.apexlegends_mobile",
            "com.mojang.minecraftpe", "com.supercell.brawlstars", "com.kiloo.subwaysurf",
            "com.imangi.templerun2", "com.halfbrick.fruitninjafree", "com.rovio.angrybirds"
        )

        private val SHOPPING_APPS = setOf(
            "com.amazon.mShop.android.shopping", "com.alibaba.aliexpresshd", "com.ebay.mobile",
            "com.wish.wishapp", "com.shopee", "com.etsy.android", "com.target.ui",
            "com.walmart.android", "com.bestbuy.android", "com.macys.android",
            "com.nike.omega", "com.adidas.app", "com.hm"
        )

        private val ADDICTIVE_APPS = SOCIAL_MEDIA_APPS + GAMING_APPS + setOf(
            "com.google.android.youtube", "tv.twitch.android.app"
        )
    }

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageAccessSettings() {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    suspend fun analyzeUsageData(): UsageInsights? {
        if (!hasUsageStatsPermission()) return null

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 60 * 60 * 1000) // Last 24 hours

        // Get usage stats for last 24 hours
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        // Get detailed events for pattern analysis
        val events = usageStatsManager.queryEvents(startTime, endTime)

        // Build comprehensive insights
        val eventsList = mutableListOf<UsageEvents.Event>()
        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)
            eventsList.add(event)
        }

        val rawStats = usageStats.associateBy(
            { it.packageName },
            { it.totalTimeInForeground }
        )

        return UsageInsights(
            dailyRoutine = analyzeDailyRoutine(eventsList, rawStats),
            sleepAnalysis = analyzeSleepPatterns(eventsList, rawStats),
            appAddictions = findAppAddictions(usageStats, eventsList),
            workLifeBalance = analyzeWorkLifeBalance(eventsList, rawStats),
            mentalHealthIndicators = analyzeMentalHealth(eventsList, rawStats),
            rawStats = rawStats
        )
    }

    private fun analyzeDailyRoutine(events: List<UsageEvents.Event>, stats: Map<String, Long>): DailyRoutine {
        // Find first and last phone usage
        val sortedEvents = events.sortedBy { it.timeStamp }
        val firstUnlock = sortedEvents.firstOrNull {
            it.eventType == UsageEvents.Event.KEYGUARD_HIDDEN ||
                    it.eventType == UsageEvents.Event.SCREEN_INTERACTIVE
        }
        val lastLock = sortedEvents.lastOrNull {
            it.eventType == UsageEvents.Event.KEYGUARD_SHOWN ||
                    it.eventType == UsageEvents.Event.SCREEN_NON_INTERACTIVE
        }

        val wakeTime = firstUnlock?.let {
            LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(it.timeStamp),
                ZoneId.systemDefault()
            ).toLocalTime().toString()
        } ?: "Unknown"

        val sleepTime = lastLock?.let {
            LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(it.timeStamp),
                ZoneId.systemDefault()
            ).toLocalTime().toString()
        } ?: "Unknown"

        // Count phone checks (unlock events)
        val phoneCheckCount = events.count {
            it.eventType == UsageEvents.Event.KEYGUARD_HIDDEN ||
                    it.eventType == UsageEvents.Event.SCREEN_INTERACTIVE
        }

        // Find most active hour
        val hourlyUsage = mutableMapOf<Int, Int>()
        events.forEach { event ->
            val cal = Calendar.getInstance().apply { timeInMillis = event.timeStamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hourlyUsage[hour] = (hourlyUsage[hour] ?: 0) + 1
        }
        val mostActiveHour = hourlyUsage.maxByOrNull { it.value }?.key?.let { hour ->
            String.format("%02d:00 - %02d:00", hour, (hour + 1) % 24)
        } ?: "Unknown"

        // First and last apps
        val firstApp = sortedEvents.firstOrNull {
            it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
        }?.packageName?.let { getAppName(it) } ?: "Unknown"

        val lastApp = sortedEvents.lastOrNull {
            it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
        }?.packageName?.let { getAppName(it) } ?: "Unknown"

        // Generate description
        val description = when {
            phoneCheckCount > 200 -> "🚨 SEVERE ADDICTION: You checked your phone $phoneCheckCount times today! That's every ${(16 * 60) / phoneCheckCount} minutes while awake."
            phoneCheckCount > 100 -> "⚠️ Heavy usage: $phoneCheckCount phone checks suggests compulsive behavior"
            phoneCheckCount > 50 -> "📱 Moderate usage: $phoneCheckCount checks per day"
            else -> "✅ Light usage: Only $phoneCheckCount checks today"
        }

        return DailyRoutine(
            wakeUpTime = wakeTime,
            sleepTime = sleepTime,
            mostActiveHour = mostActiveHour,
            phoneCheckCount = phoneCheckCount,
            firstAppOfDay = firstApp,
            lastAppBeforeSleep = lastApp,
            routineDescription = description
        )
    }

    private fun analyzeSleepPatterns(
        events: List<UsageEvents.Event>,
        stats: Map<String, Long>
    ): SleepAnalysis {
        // Calculate midnight usage (12 AM - 6 AM)
        val midnightEvents = events.filter { event ->
            val cal = Calendar.getInstance().apply { timeInMillis = event.timeStamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour in 0..5
        }

        val midnightUsageMinutes = midnightEvents.count { it.eventType == UsageEvents.Event.ACTIVITY_RESUMED } * 5L

        // Count wake-ups to check phone
        val nightWakeups = midnightEvents.count {
            it.eventType == UsageEvents.Event.KEYGUARD_HIDDEN ||
                    it.eventType == UsageEvents.Event.SCREEN_INTERACTIVE
        }

        // Estimate sleep duration (very rough - time between last evening and first morning activity)
        val eveningEvents = events.filter { event ->
            val cal = Calendar.getInstance().apply { timeInMillis = event.timeStamp }
            cal.get(Calendar.HOUR_OF_DAY) in 22..23
        }
        val morningEvents = events.filter { event ->
            val cal = Calendar.getInstance().apply { timeInMillis = event.timeStamp }
            cal.get(Calendar.HOUR_OF_DAY) in 6..9
        }

        val lastEvening = eveningEvents.maxByOrNull { it.timeStamp }?.timeStamp ?: 0
        val firstMorning = morningEvents.minByOrNull { it.timeStamp }?.timeStamp ?: 0

        val sleepDuration = if (lastEvening > 0 && firstMorning > lastEvening) {
            ((firstMorning - lastEvening) / (1000 * 60 * 60)).toFloat()
        } else {
            7.5f // Default assumption
        }

        // Quality score
        val qualityScore = when {
            nightWakeups == 0 && midnightUsageMinutes == 0L -> 100
            nightWakeups <= 1 && midnightUsageMinutes < 15 -> 85
            nightWakeups <= 3 && midnightUsageMinutes < 30 -> 60
            nightWakeups <= 5 -> 40
            else -> 20
        }

        // Verdict
        val verdict = when {
            nightWakeups > 5 -> "🚨 CRITICAL: You woke up $nightWakeups times to check your phone! Phone addiction is destroying your sleep."
            nightWakeups > 3 -> "😰 BAD: $nightWakeups midnight wake-ups indicates severe sleep disruption"
            midnightUsageMinutes > 60 -> "⚠️ You spent ${midnightUsageMinutes}min on your phone between midnight-6am. Insomnia?"
            nightWakeups > 0 -> "💤 You checked your phone $nightWakeups times during the night"
            else -> "✅ Good sleep hygiene: No phone usage during sleep hours"
        }

        return SleepAnalysis(
            lastNightSleepDuration = sleepDuration,
            sleepQualityScore = qualityScore,
            midnightUsage = midnightUsageMinutes,
            timesWokeUpToCheckPhone = nightWakeups,
            verdict = verdict
        )
    }

    private fun findAppAddictions(
        usageStats: List<UsageStats>,
        events: List<UsageEvents.Event>
    ): List<AppAddiction> {
        val addictions = mutableListOf<AppAddiction>()

        // Focus on known addictive apps with significant usage
        usageStats.filter {
            it.packageName in ADDICTIVE_APPS && it.totalTimeInForeground > 30 * 60 * 1000 // > 30 min
        }.sortedByDescending { it.totalTimeInForeground }
            .take(5)
            .forEach { stat ->
                val packageName = stat.packageName
                val dailyUsage = stat.totalTimeInForeground / (60 * 1000) // Convert to minutes

                // Count sessions
                val appEvents = events.filter { it.packageName == packageName }
                val sessionCount = appEvents.count { it.eventType == UsageEvents.Event.ACTIVITY_RESUMED }

                // Find longest session
                var maxSession = 0L
                var sessionStart = 0L
                appEvents.sortedBy { it.timeStamp }.forEach { event ->
                    when (event.eventType) {
                        UsageEvents.Event.ACTIVITY_RESUMED -> sessionStart = event.timeStamp
                        UsageEvents.Event.ACTIVITY_PAUSED -> {
                            if (sessionStart > 0) {
                                val duration = (event.timeStamp - sessionStart) / (60 * 1000)
                                if (duration > maxSession) maxSession = duration
                                sessionStart = 0
                            }
                        }
                    }
                }

                // Addiction level
                val level = when {
                    dailyUsage > 240 -> "Severe" // > 4 hours
                    dailyUsage > 120 -> "Moderate" // > 2 hours
                    else -> "Healthy"
                }

                // Pattern detection
                val pattern = when {
                    maxSession > 120 -> "🚨 Single session lasted ${maxSession}min! Binge usage detected."
                    sessionCount > 50 -> "😰 Opened $sessionCount times today - compulsive checking behavior"
                    dailyUsage > 180 -> "⚠️ ${dailyUsage / 60}+ hours on this app today"
                    else -> "Frequent user"
                }

                addictions.add(
                    AppAddiction(
                        appName = getAppName(packageName),
                        packageName = packageName,
                        dailyUsage = dailyUsage,
                        sessionCount = sessionCount,
                        longestSession = maxSession,
                        addictionLevel = level,
                        concerningPattern = pattern
                    )
                )
            }

        return addictions
    }

    private fun analyzeWorkLifeBalance(
        events: List<UsageEvents.Event>,
        stats: Map<String, Long>
    ): WorkLifeBalance {
        // Calculate work vs entertainment time
        val workAppUsage = stats.filter { it.key in WORK_APPS }
            .values.sum() / (60 * 1000) // Convert to minutes

        val entertainmentUsage = stats.filter { it.key in ENTERTAINMENT_APPS }
            .values.sum() / (60 * 1000)

        // Find work hour violations (9 AM - 5 PM usage of distracting apps)
        val violations = mutableListOf<String>()
        val workHourDistractingApps = mutableMapOf<String, Int>()

        events.filter { event ->
            val cal = Calendar.getInstance().apply { timeInMillis = event.timeStamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            // Monday-Friday, 9 AM - 5 PM
            dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY && hour in 9..16
        }.filter {
            it.packageName in (SOCIAL_MEDIA_APPS + GAMING_APPS + ENTERTAINMENT_APPS) &&
                    it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
        }.forEach { event ->
            workHourDistractingApps[event.packageName] =
                (workHourDistractingApps[event.packageName] ?: 0) + 1
        }

        // Generate violation messages
        workHourDistractingApps.forEach { (pkg, count) ->
            val appName = getAppName(pkg)
            when {
                count > 20 -> violations.add("🚨 $appName opened $count times during work hours!")
                count > 10 -> violations.add("⚠️ $appName distracted you $count times at work")
                count > 5 -> violations.add("$appName checked $count times during work")
            }
        }

        // Calculate productivity score
        val totalUsage = stats.values.sum() / (60 * 1000)
        val productiveUsage = workAppUsage
        val wastedTime = entertainmentUsage

        val productivityScore = when {
            totalUsage == 0L -> 0
            workAppUsage > entertainmentUsage * 2 -> 90
            workAppUsage > entertainmentUsage -> 70
            workAppUsage > entertainmentUsage / 2 -> 50
            else -> 30
        }

        // Generate verdict
        val verdict = when {
            violations.size > 5 -> "🔴 TERRIBLE: You're constantly distracted at work! ${violations.size} distracting behaviors detected."
            entertainmentUsage > workAppUsage * 3 -> "🎮 You spent 3x more time on entertainment than work. Priority check needed!"
            workAppUsage > 480 -> "💀 WORKAHOLIC ALERT: ${workAppUsage / 60} hours on work apps! Burnout incoming."
            productivityScore > 70 -> "✅ Good balance: Productive but not obsessive"
            else -> "⚠️ Entertainment is dominating your screen time"
        }

        return WorkLifeBalance(
            workAppUsage = workAppUsage,
            entertainmentUsage = entertainmentUsage,
            productivityScore = productivityScore,
            workHoursViolations = violations.take(5), // Top 5 violations
            balanceVerdict = verdict
        )
    }

    private fun analyzeMentalHealth(
        events: List<UsageEvents.Event>,
        stats: Map<String, Long>
    ): MentalHealthIndicators {
        val anxietyIndicators = mutableListOf<String>()
        val moodAffectingApps = mutableListOf<String>()

        // Late night social media usage (after 11 PM)
        val lateNightSocial = events.filter { event ->
            val cal = Calendar.getInstance().apply { timeInMillis = event.timeStamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hour >= 23 || hour <= 2
        }.filter {
            it.packageName in SOCIAL_MEDIA_APPS && it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
        }

        val lateNightMinutes = lateNightSocial.size * 5 // Approximate 5 min per session

        // Doom scrolling detection (long social media sessions)
        var doomScrollingSessions = 0
        var lastSocialStart = 0L

        events.sortedBy { it.timeStamp }.forEach { event ->
            if (event.packageName in SOCIAL_MEDIA_APPS) {
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> lastSocialStart = event.timeStamp
                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        if (lastSocialStart > 0) {
                            val duration = (event.timeStamp - lastSocialStart) / (60 * 1000)
                            if (duration > 30) doomScrollingSessions++
                            lastSocialStart = 0
                        }
                    }
                }
            }
        }

        // Check for anxiety patterns
        val rapidAppSwitching = findRapidAppSwitching(events)
        if (rapidAppSwitching > 50) {
            anxietyIndicators.add("🔄 Rapid app switching ($rapidAppSwitching times) suggests restlessness")
        }

        // Dating app usage patterns
        val datingAppSessions = events.count {
            it.packageName in DATING_APPS && it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
        }
        if (datingAppSessions > 15) {
            anxietyIndicators.add("💔 Excessive dating app checking ($datingAppSessions times) indicates relationship anxiety")
        }

        // News app checking (doom consuming)
        val newsApps = setOf("com.google.android.googlequicksearchbox", "com.reddit.frontpage", "com.twitter.android")
        val newsChecks = events.count {
            it.packageName in newsApps && it.eventType == UsageEvents.Event.ACTIVITY_RESUMED
        }
        if (newsChecks > 30) {
            anxietyIndicators.add("📰 Compulsive news checking ($newsChecks times) - doomscrolling detected")
        }

        // 3 AM usage (insomnia indicator)
        val threeAmUsage = events.any { event ->
            val cal = Calendar.getInstance().apply { timeInMillis = event.timeStamp }
            cal.get(Calendar.HOUR_OF_DAY) in 3..5
        }
        if (threeAmUsage) {
            anxietyIndicators.add("😰 Phone usage between 3-5 AM indicates severe insomnia")
        }

        // Identify mood-affecting apps
        stats.filter { it.key in SOCIAL_MEDIA_APPS && it.value > 60 * 60 * 1000 }
            .forEach { (pkg, _) ->
                moodAffectingApps.add("${getAppName(pkg)} - comparison trap")
            }

        if (datingAppSessions > 10) {
            moodAffectingApps.add("Dating apps - validation seeking behavior")
        }

        // Determine risk level
        val riskLevel = when {
            anxietyIndicators.size >= 4 || doomScrollingSessions > 5 -> "🔴 HIGH RISK"
            anxietyIndicators.size >= 2 || lateNightMinutes > 60 -> "🟡 MEDIUM RISK"
            else -> "🟢 LOW RISK"
        }

        return MentalHealthIndicators(
            doomScrollingSessions = doomScrollingSessions,
            lateNightSocialMedia = lateNightMinutes.toLong(),
            anxietyIndicators = anxietyIndicators,
            moodAffectingApps = moodAffectingApps,
            overallRiskLevel = riskLevel
        )
    }

    private fun findRapidAppSwitching(events: List<UsageEvents.Event>): Int {
        var switches = 0
        var lastApp = ""
        var lastTime = 0L

        events.sortedBy { it.timeStamp }
            .filter { it.eventType == UsageEvents.Event.ACTIVITY_RESUMED }
            .forEach { event ->
                if (lastApp.isNotEmpty() &&
                    event.packageName != lastApp &&
                    event.timeStamp - lastTime < 30000) { // Within 30 seconds
                    switches++
                }
                lastApp = event.packageName
                lastTime = event.timeStamp
            }

        return switches
    }

    private fun getAppName(packageName: String): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            // Fallback to known apps
            when (packageName) {
                "com.instagram.android" -> "Instagram"
                "com.facebook.katana" -> "Facebook"
                "com.twitter.android", "com.x.android" -> "X (Twitter)"
                "com.zhiliaoapp.musically", "com.ss.android.ugc.trill" -> "TikTok"
                "com.snapchat.android" -> "Snapchat"
                "com.whatsapp" -> "WhatsApp"
                "com.google.android.youtube" -> "YouTube"
                "com.netflix.mediaclient" -> "Netflix"
                "com.spotify.music" -> "Spotify"
                "com.tinder" -> "Tinder"
                "com.bumble.app" -> "Bumble"
                "com.hinge.app" -> "Hinge"
                "com.reddit.frontpage" -> "Reddit"
                "com.discord" -> "Discord"
                "com.telegram.messenger" -> "Telegram"
                "com.facebook.orca" -> "Messenger"
                "com.king.candycrushsaga" -> "Candy Crush"
                "com.supercell.clashofclans" -> "Clash of Clans"
                "com.roblox.client" -> "Roblox"
                "com.mojang.minecraftpe" -> "Minecraft"
                else -> packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }
            }
        }
    }
}
