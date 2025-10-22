package com.example.spycheck.services.tracking

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Manages tracking domain lists from multiple sources
 */
class TrackingDomainManager(private val context: Context) {

    private val trackingDomains = mutableSetOf<String>()
    private val trackingPatterns = mutableListOf<Regex>()

    companion object {
        // Popular tracking list sources
        val DEFAULT_LISTS = listOf(
            // EasyPrivacy (one of the most comprehensive)
            "https://easylist.to/easylist/easyprivacy.txt",
            // AdGuard Tracking Protection
            "https://raw.githubusercontent.com/AdguardTeam/FiltersRegistry/master/filters/filter_3_Spyware/filter.txt",
            // Disconnect.me tracking list
            "https://raw.githubusercontent.com/disconnectme/disconnect-tracking-protection/master/services.json",
            // Steven Black's ad/tracking hosts
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts"
        )

        // Built-in domains for immediate use (before lists load)
        val BUILTIN_TRACKING_DOMAINS = setOf(
            // Facebook
            "facebook.com", "facebook.net", "fbcdn.net", "fb.com", "fb.me",
            "connect.facebook.com", "graph.facebook.com", "an.facebook.com",

            // Google Analytics & Ads
            "google-analytics.com", "googleadservices.com", "googlesyndication.com",
            "doubleclick.net", "googletagmanager.com", "googletagservices.com",
            "google.com/pagead", "admob.com", "adservice.google.com",

            // Amazon tracking
            "amazon-adsystem.com", "amazonadvertising.com",

            // Microsoft
            "bing.com/fd", "microsoft.com/c.gif", "msn.com/c.gif",

            // Twitter
            "ads-twitter.com", "analytics.twitter.com", "t.co",

            // TikTok
            "tiktok.com/api/ad", "byteoversea.com",

            // Common trackers
            "scorecardresearch.com", "quantserve.com", "chartbeat.com",
            "newrelic.com", "mixpanel.com", "segment.com", "amplitude.com",
            "appsflyer.com", "branch.io", "adjust.com", "kochava.com",
            "crashlytics.com", "fabric.io", "flurry.com", "localytics.com",

            // Ad networks
            "adcolony.com", "admob.com", "adnxs.com", "advertising.com",
            "criteo.com", "moatads.com", "omtrdc.net", "taboola.com",
            "outbrain.com", "pubmatic.com", "rubiconproject.com",
            "openx.net", "indexww.com", "adsrvr.org"
        )

        val BUILTIN_TRACKING_KEYWORDS = listOf(
            "track", "analytic", "telemetry", "crash", "metric",
            "ads", "advert", "doubleclick", "pixel", "beacon"
        )
    }

    init {
        // Add built-in domains immediately
        trackingDomains.addAll(BUILTIN_TRACKING_DOMAINS)

        // Add keyword patterns
        BUILTIN_TRACKING_KEYWORDS.forEach { keyword ->
            trackingPatterns.add(Regex(".*$keyword.*", RegexOption.IGNORE_CASE))
        }
    }

    /**
     * Load tracking lists from remote sources
     */
    suspend fun loadTrackingLists() = withContext(Dispatchers.IO) {
        DEFAULT_LISTS.forEach { listUrl ->
            try {
                Log.d("TrackingDomainManager", "Loading list from: $listUrl")
                val domains = downloadList(listUrl)
                trackingDomains.addAll(domains)
                Log.d("TrackingDomainManager", "Loaded ${domains.size} domains from $listUrl")
            } catch (e: Exception) {
                Log.e("TrackingDomainManager", "Failed to load list from $listUrl", e)
            }
        }
        Log.d("TrackingDomainManager", "Total tracking domains: ${trackingDomains.size}")
    }

    /**
     * Download and parse a tracking list
     */
    private fun downloadList(urlString: String): Set<String> {
        val domains = mutableSetOf<String>()

        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let {
                        parseLine(it)?.let { domain -> domains.add(domain) }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TrackingDomainManager", "Error downloading list", e)
        }

        return domains
    }

    /**
     * Parse a line from a tracking list
     */
    private fun parseLine(line: String): String? {
        val trimmed = line.trim()

        // Skip comments and empty lines
        if (trimmed.isEmpty() || trimmed.startsWith("#") ||
            trimmed.startsWith("!") || trimmed.startsWith("[")) {
            return null
        }

        // Handle hosts file format (0.0.0.0 domain.com or 127.0.0.1 domain.com)
        if (trimmed.startsWith("0.0.0.0") || trimmed.startsWith("127.0.0.1")) {
            val parts = trimmed.split(Regex("\\s+"))
            if (parts.size >= 2) {
                return parts[1].lowercase()
            }
        }

        // Handle AdBlock format (||domain.com^)
        if (trimmed.startsWith("||") && trimmed.contains("^")) {
            val domain = trimmed.substring(2, trimmed.indexOf("^"))
            if (domain.contains(".") && !domain.contains("/")) {
                return domain.lowercase()
            }
        }

        // Handle plain domain format
        if (trimmed.contains(".") && !trimmed.contains(" ") &&
            !trimmed.contains("#") && !trimmed.startsWith(".")) {
            return trimmed.lowercase()
        }

        return null
    }

    /**
     * Check if a domain is a tracking domain
     */
    fun isTrackingDomain(domain: String): Boolean {
        val lowerDomain = domain.lowercase()

        // Exact match
        if (trackingDomains.contains(lowerDomain)) {
            return true
        }

        // Check if any parent domain matches
        val parts = lowerDomain.split(".")
        for (i in 0 until parts.size - 1) {
            val parentDomain = parts.subList(i, parts.size).joinToString(".")
            if (trackingDomains.contains(parentDomain)) {
                return true
            }
        }

        // Check patterns
        if (trackingPatterns.any { it.matches(lowerDomain) }) {
            return true
        }

        return false
    }

    /**
     * Get statistics
     */
    fun getStats(): String {
        return "Tracking domains: ${trackingDomains.size}, Patterns: ${trackingPatterns.size}"
    }
}