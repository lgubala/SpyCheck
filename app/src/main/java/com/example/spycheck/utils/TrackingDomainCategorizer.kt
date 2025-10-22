package com.example.spycheck.services.tracking

/**
 * Categories of tracking domains
 */
enum class TrackingCategory(val displayName: String, val description: String) {
    ANALYTICS("Analytics Provider", "collects data about your behavior"),
    ADVERTISING("Ad Network", "tracks you to show targeted ads"),
    DATA_BROKER("Data Broker", "sells your personal information"),
    FINGERPRINTING("Fingerprinting Service", "identifies your device uniquely"),
    CRASH_REPORTING("Crash Reporter", "monitors app crashes and collects device info"),
    SOCIAL_TRACKING("Social Media Tracker", "tracks you across websites"),
    UNKNOWN("Unknown Tracker", "collects your data")
}

/**
 * Categorizes tracking domains based on known patterns and services
 */
object TrackingDomainCategorizer {

    /**
     * Categorize a domain based on its name and known services
     */
    fun categorize(domain: String): TrackingCategory {
        val lowerDomain = domain.lowercase()

        // Check exact matches first
        EXACT_DOMAIN_CATEGORIES.forEach { (domainPattern, category) ->
            if (lowerDomain.contains(domainPattern)) {
                return category
            }
        }

        // Check keyword patterns
        KEYWORD_CATEGORIES.forEach { (keyword, category) ->
            if (lowerDomain.contains(keyword)) {
                return category
            }
        }

        return TrackingCategory.UNKNOWN
    }

    /**
     * Get a user-friendly explanation of what this tracker does
     */
    fun getTrackingExplanation(category: TrackingCategory): String {
        return when (category) {
            TrackingCategory.ANALYTICS -> "tracking your behavior and collecting usage data"
            TrackingCategory.ADVERTISING -> "building an ad profile to target you with ads"
            TrackingCategory.DATA_BROKER -> "selling your personal data to third parties"
            TrackingCategory.FINGERPRINTING -> "creating a unique device fingerprint to track you"
            TrackingCategory.CRASH_REPORTING -> "collecting device info and app crash data"
            TrackingCategory.SOCIAL_TRACKING -> "tracking your activity across different apps and websites"
            TrackingCategory.UNKNOWN -> "collecting your personal data"
        }
    }

    /**
     * Exact domain to category mapping
     * Organized by company/service
     */
    private val EXACT_DOMAIN_CATEGORIES = mapOf(
        // === ANALYTICS ===
        // Google Analytics
        "google-analytics.com" to TrackingCategory.ANALYTICS,
        "googletagmanager.com" to TrackingCategory.ANALYTICS,
        "googletagservices.com" to TrackingCategory.ANALYTICS,

        // Other Analytics
        "mixpanel.com" to TrackingCategory.ANALYTICS,
        "segment.com" to TrackingCategory.ANALYTICS,
        "amplitude.com" to TrackingCategory.ANALYTICS,
        "chartbeat.com" to TrackingCategory.ANALYTICS,
        "hotjar.com" to TrackingCategory.ANALYTICS,
        "heap.io" to TrackingCategory.ANALYTICS,
        "fullstory.com" to TrackingCategory.ANALYTICS,
        "logrocket.com" to TrackingCategory.ANALYTICS,
        "newrelic.com" to TrackingCategory.ANALYTICS,
        "datadog.com" to TrackingCategory.ANALYTICS,

        // === ADVERTISING ===
        // Google Ads
        "doubleclick.net" to TrackingCategory.ADVERTISING,
        "googlesyndication.com" to TrackingCategory.ADVERTISING,
        "googleadservices.com" to TrackingCategory.ADVERTISING,
        "admob.com" to TrackingCategory.ADVERTISING,
        "adservice.google.com" to TrackingCategory.ADVERTISING,

        // Facebook/Meta Ads
        "facebook.com/tr" to TrackingCategory.ADVERTISING,
        "facebook.net" to TrackingCategory.ADVERTISING,
        "fbcdn.net" to TrackingCategory.ADVERTISING,

        // Other Ad Networks
        "adcolony.com" to TrackingCategory.ADVERTISING,
        "unity3d.com" to TrackingCategory.ADVERTISING,
        "applovin.com" to TrackingCategory.ADVERTISING,
        "ironsrc.com" to TrackingCategory.ADVERTISING,
        "vungle.com" to TrackingCategory.ADVERTISING,
        "chartboost.com" to TrackingCategory.ADVERTISING,
        "inmobi.com" to TrackingCategory.ADVERTISING,
        "mopub.com" to TrackingCategory.ADVERTISING,
        "startapp.com" to TrackingCategory.ADVERTISING,
        "tapjoy.com" to TrackingCategory.ADVERTISING,
        "criteo.com" to TrackingCategory.ADVERTISING,
        "outbrain.com" to TrackingCategory.ADVERTISING,
        "taboola.com" to TrackingCategory.ADVERTISING,
        "pubmatic.com" to TrackingCategory.ADVERTISING,
        "rubiconproject.com" to TrackingCategory.ADVERTISING,
        "openx.net" to TrackingCategory.ADVERTISING,
        "indexww.com" to TrackingCategory.ADVERTISING,
        "adsrvr.org" to TrackingCategory.ADVERTISING,
        "adnxs.com" to TrackingCategory.ADVERTISING,

        // === DATA BROKERS ===
        "branch.io" to TrackingCategory.DATA_BROKER,
        "appsflyer.com" to TrackingCategory.DATA_BROKER,
        "adjust.com" to TrackingCategory.DATA_BROKER,
        "kochava.com" to TrackingCategory.DATA_BROKER,
        "tune.com" to TrackingCategory.DATA_BROKER,
        "singular.net" to TrackingCategory.DATA_BROKER,
        "tenjin.io" to TrackingCategory.DATA_BROKER,
        "scorecardresearch.com" to TrackingCategory.DATA_BROKER,
        "quantserve.com" to TrackingCategory.DATA_BROKER,
        "omtrdc.net" to TrackingCategory.DATA_BROKER,
        "moatads.com" to TrackingCategory.DATA_BROKER,
        "acxiom.com" to TrackingCategory.DATA_BROKER,
        "experian.com" to TrackingCategory.DATA_BROKER,
        "equifax.com" to TrackingCategory.DATA_BROKER,

        // === CRASH REPORTING ===
        "crashlytics.com" to TrackingCategory.CRASH_REPORTING,
        "fabric.io" to TrackingCategory.CRASH_REPORTING,
        "sentry.io" to TrackingCategory.CRASH_REPORTING,
        "bugsnag.com" to TrackingCategory.CRASH_REPORTING,
        "instabug.com" to TrackingCategory.CRASH_REPORTING,
        "rollbar.com" to TrackingCategory.CRASH_REPORTING,
        "airbrake.io" to TrackingCategory.CRASH_REPORTING,
        "raygun.com" to TrackingCategory.CRASH_REPORTING,

        // === FINGERPRINTING ===
        "fingerprintjs.com" to TrackingCategory.FINGERPRINTING,
        "clientid.com" to TrackingCategory.FINGERPRINTING,
        "deviceatlas.com" to TrackingCategory.FINGERPRINTING,
        "trustev.com" to TrackingCategory.FINGERPRINTING,
        "iovation.com" to TrackingCategory.FINGERPRINTING,
        "threatmetrix.com" to TrackingCategory.FINGERPRINTING,

        // === SOCIAL TRACKING ===
        "facebook.com" to TrackingCategory.SOCIAL_TRACKING,
        "fb.com" to TrackingCategory.SOCIAL_TRACKING,
        "instagram.com" to TrackingCategory.SOCIAL_TRACKING,
        "twitter.com" to TrackingCategory.SOCIAL_TRACKING,
        "x.com" to TrackingCategory.SOCIAL_TRACKING,
        "linkedin.com" to TrackingCategory.SOCIAL_TRACKING,
        "pinterest.com" to TrackingCategory.SOCIAL_TRACKING,
        "reddit.com" to TrackingCategory.SOCIAL_TRACKING,
        "tiktok.com" to TrackingCategory.SOCIAL_TRACKING,
        "snapchat.com" to TrackingCategory.SOCIAL_TRACKING,
        "connect.facebook.com" to TrackingCategory.SOCIAL_TRACKING,
        "graph.facebook.com" to TrackingCategory.SOCIAL_TRACKING
    )

    /**
     * Keyword-based categorization (fallback)
     */
    private val KEYWORD_CATEGORIES = mapOf(
        // Analytics keywords
        "analytics" to TrackingCategory.ANALYTICS,
        "metric" to TrackingCategory.ANALYTICS,
        "telemetry" to TrackingCategory.ANALYTICS,
        "stats" to TrackingCategory.ANALYTICS,

        // Advertising keywords
        "ads" to TrackingCategory.ADVERTISING,
        "advert" to TrackingCategory.ADVERTISING,
        "banner" to TrackingCategory.ADVERTISING,
        "campaign" to TrackingCategory.ADVERTISING,

        // Crash reporting keywords
        "crash" to TrackingCategory.CRASH_REPORTING,
        "error" to TrackingCategory.CRASH_REPORTING,
        "sentry" to TrackingCategory.CRASH_REPORTING,
        "bugsnag" to TrackingCategory.CRASH_REPORTING,

        // Fingerprinting keywords
        "fingerprint" to TrackingCategory.FINGERPRINTING,
        "deviceid" to TrackingCategory.FINGERPRINTING,
        "clientid" to TrackingCategory.FINGERPRINTING,

        // Tracking keywords (general)
        "track" to TrackingCategory.ANALYTICS,
        "beacon" to TrackingCategory.ANALYTICS,
        "pixel" to TrackingCategory.ADVERTISING
    )
}