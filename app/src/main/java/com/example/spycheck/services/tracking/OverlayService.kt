package com.example.spycheck.services.tracking

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.spycheck.R
import com.example.spycheck.db.tracking.TrackingEvent
import com.example.spycheck.ui.theme.Amber
import com.example.spycheck.ui.theme.BackgroundDark
import com.example.spycheck.ui.theme.Crimson
import com.example.spycheck.utils.ServiceLifecycleOwner
import kotlinx.coroutines.delay


class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var lifecycleOwner: ServiceLifecycleOwner

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        lifecycleOwner = ServiceLifecycleOwner()
        lifecycleOwner.performRestore()
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                TrackingOverlay()
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 100
        }

        windowManager.addView(composeView, params)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        VpnStateManager.setOverlayRunning(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        windowManager.removeView(composeView)
        VpnStateManager.setOverlayRunning(false)
    }
}

@Composable
fun TrackingOverlay() {
    // Collect the latest event
    val latestEvent by TrackingDataHolder.latestEvent.collectAsState()
    val lastEventTimestamp by TrackingDataHolder.lastEventTimestamp.collectAsState()

    // Auto-hide state: popup disappears 3 seconds after last event
    var shouldShow by remember { mutableStateOf(false) }

    // Monitor for inactivity
    LaunchedEffect(lastEventTimestamp) {
        if (lastEventTimestamp > 0L) {
            shouldShow = true
            // Wait 3 seconds, then hide if no new events
            delay(3000L)
            // Only hide if timestamp hasn't changed (no new events)
            if (lastEventTimestamp == TrackingDataHolder.lastEventTimestamp.value) {
                shouldShow = false
            }
        }
    }

    // Simple fade animation (fast, non-intrusive)
    AnimatedVisibility(
        visible = shouldShow && latestEvent != null,
        enter = fadeIn(animationSpec = tween(150)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        TrackingCard(event = latestEvent)
    }
}


@Composable
fun TrackingCard(event: TrackingEvent?) {
    val context = LocalContext.current
    val category = remember(event?.category) {
        try {
            TrackingCategory.valueOf(event?.category ?: "UNKNOWN")
        } catch (e: Exception) {
            TrackingCategory.UNKNOWN
        }
    }

    val explanation = TrackingDomainCategorizer.getTrackingExplanation(context, category)

    Card(
        modifier = Modifier
            .padding(16.dp)
            .width(300.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundDark
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with pulsing warning
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Crimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚠️",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.overlay_warning),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Crimson,
                        letterSpacing = 1.sp
                    )
                }

                // Counter badge
                event?.let { evt ->
                    if (evt.count > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Crimson)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${evt.count}×",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.overlay_you_are_being_tracked),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.White.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(12.dp))

            event?.let { evt ->
                // Main tracking message
                Text(
                    text = stringResource(R.string.overlay_app_sent_data, evt.appName),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Domain in box
                Surface(
                    color = Crimson.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = evt.domain,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Crimson,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category and explanation
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Amber)
                    )
                    Column {
                        Text(
                            text = stringResource(
                                R.string.overlay_domain_is_category,
                                evt.domain,
                                stringResource(category.displayNameRes)
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber
                        )
                        Text(
                            text = explanation,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
/**
 * Extract company name from domain
 */
fun extractCompanyName(domain: String): String {
    val lowerDomain = domain.lowercase()

    val companyMap = mapOf(
        "facebook" to "Facebook",
        "fb" to "Facebook",
        "instagram" to "Instagram",
        "whatsapp" to "WhatsApp",
        "google" to "Google",
        "doubleclick" to "Google",
        "youtube" to "YouTube",
        "googlesyndication" to "Google Ads",
        "googleadservices" to "Google Ads",
        "tiktok" to "TikTok",
        "bytedance" to "TikTok",
        "twitter" to "Twitter/X",
        "amazon" to "Amazon",
        "microsoft" to "Microsoft",
        "apple" to "Apple",
        "snapchat" to "Snapchat",
        "linkedin" to "LinkedIn",
        "reddit" to "Reddit",
        "pinterest" to "Pinterest",
        "spotify" to "Spotify",
        "netflix" to "Netflix",
        "firebase" to "Google Firebase"
    )

    for ((keyword, company) in companyMap) {
        if (lowerDomain.contains(keyword)) {
            return company
        }
    }

    val parts = lowerDomain.split(".")
    if (parts.size >= 2) {
        val mainPart = parts[parts.size - 2]
        return mainPart.replaceFirstChar { it.uppercase() }
    }

    return domain.split(".").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: domain
}
