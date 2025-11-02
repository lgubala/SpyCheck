package com.example.spycheck.ui.main

import android.app.Activity
import android.app.AppOpsManager
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.VpnService
import android.os.Build
import android.os.LocaleList
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.spycheck.R
import com.example.spycheck.services.tracking.OverlayService
import com.example.spycheck.services.tracking.TrackingDataHolder
import com.example.spycheck.services.tracking.TrackingVpnService
import com.example.spycheck.services.tracking.VpnStateManager
import com.example.spycheck.ui.theme.Amber

import com.example.spycheck.ui.theme.Crimson
import com.example.spycheck.ui.theme.CrimsonDark
import com.example.spycheck.ui.theme.LightGreen

import com.example.spycheck.ui.theme.TextPrimary
import com.example.spycheck.utils.PreferencesManager
import java.util.Locale

@Composable
fun HomeScreen(
    currentThemeMode: Int = PreferencesManager.THEME_MODE_DARK,
    onThemeModeChanged: (Int) -> Unit = {},
    onLocaleChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // State from VpnStateManager
    val isVpnRunning by VpnStateManager.isVpnRunning.collectAsState()
    val isOverlayRunning by VpnStateManager.isOverlayRunning.collectAsState()
    val trackingCount by TrackingDataHolder.totalTrackingCount.collectAsState()

    // Permission states
    var hasVpnPermission by remember { mutableStateOf(checkVpnPermission(context)) }
    var hasUsageStatsPermission by remember { mutableStateOf(checkUsageStatsPermission(context)) }
    var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(context)) }

    val allPermissionsGranted = hasVpnPermission && hasUsageStatsPermission && hasOverlayPermission
    val isMonitoring = isVpnRunning && isOverlayRunning

    // VPN permission launcher
    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        hasVpnPermission = checkVpnPermission(context)
    }

    // Refresh permissions when app resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasVpnPermission = checkVpnPermission(context)
                hasUsageStatsPermission = checkUsageStatsPermission(context)
                hasOverlayPermission = Settings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Reset counter when monitoring stops
    LaunchedEffect(isMonitoring) {
        if (!isMonitoring) {
            TrackingDataHolder.clearCounts()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme & Language Section - OPTIMIZED
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Theme Toggle - INSTANT SWITCH
            IconButton(
                onClick = {
                    val newMode = if (currentThemeMode == PreferencesManager.THEME_MODE_DARK) {
                        PreferencesManager.THEME_MODE_LIGHT
                    } else {
                        PreferencesManager.THEME_MODE_DARK
                    }
                    onThemeModeChanged(newMode)
                }
            ) {
                Icon(
                    imageVector = if (currentThemeMode == PreferencesManager.THEME_MODE_DARK)
                        Icons.Default.DarkMode
                    else
                        Icons.Default.LightMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Language Switcher
            LanguageSwitcher()
        }

        // Rest of your UI...
        HeaderSection()

        Spacer(modifier = Modifier.height(8.dp))

        // Start Watching Card
        MonitoringCard(
            isMonitoring = isMonitoring,
            allPermissionsGranted = allPermissionsGranted,
            trackingCount = trackingCount,
            onStartStop = {
                if (isMonitoring) {
                    stopMonitoring(context)
                } else {
                    startMonitoring(context)
                }
            }
        )

        // Permissions Section
        PermissionsSection(
            hasVpnPermission = hasVpnPermission,
            hasUsageStatsPermission = hasUsageStatsPermission,
            hasOverlayPermission = hasOverlayPermission,
            vpnPermissionLauncher = vpnPermissionLauncher,
            context = context
        )

        // Monitoring Active Badge
        AnimatedVisibility(visible = isMonitoring) {
            MonitoringActiveBadge(
                hasVpnPermission = hasVpnPermission,
                hasUsageStatsPermission = hasUsageStatsPermission,
                hasOverlayPermission = hasOverlayPermission
            )
        }

        // Instructions
        InstructionsCard()

        // Revoke Permissions Button
        AnimatedVisibility(visible = !isMonitoring && allPermissionsGranted) {
            RevokePermissionsCard(context = context)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun LanguageSwitcher() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = "Change Language",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.language)) },
            text = {
                Column {
                    LanguageButton(
                        flag = "🇬🇧",
                        name = stringResource(R.string.english),
                        onClick = {
                            setLocale(context, context.getString(R.string.locale_code_english))
                            showDialog = false
                        }
                    )
                    LanguageButton(
                        flag = "🇸🇰",
                        name = stringResource(R.string.slovak),
                        onClick = {
                            setLocale(context, context.getString(R.string.locale_code_slovak))
                            showDialog = false
                        }
                    )
                    LanguageButton(
                        flag = "🇪🇸",
                        name = stringResource(R.string.spanish),
                        onClick = {
                            setLocale(context, context.getString(R.string.locale_code_spanish))
                            showDialog = false
                        }
                    )
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun LanguageButton(flag: String, name: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("$flag $name")
    }
}

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = null,
            modifier = Modifier.size(140.dp),
            tint = Crimson
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.home_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.home_subtitle),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MonitoringCard(
    isMonitoring: Boolean,
    allPermissionsGranted: Boolean,
    trackingCount: Int,
    onStartStop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isMonitoring) MaterialTheme.colorScheme.secondaryContainer
            else if (allPermissionsGranted) Crimson.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMonitoring) {
                    PulsingDot()
                }
                Text(
                    text = if (isMonitoring) stringResource(R.string.home_watching_live)
                    else if (allPermissionsGranted) stringResource(R.string.home_ready_to_start)
                    else stringResource(R.string.home_grant_permissions_first),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMonitoring) Crimson
                    else if (allPermissionsGranted) Crimson
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            AnimatedVisibility(visible = isMonitoring && trackingCount > 0) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$trackingCount",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Crimson
                    )
                    Text(
                        text = stringResource(R.string.home_tracking_attempts),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartStop,
                enabled = allPermissionsGranted,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMonitoring) CrimsonDark else Crimson,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isMonitoring) stringResource(R.string.home_stop_watching)
                    else stringResource(R.string.home_start_watching),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PermissionsSection(
    hasVpnPermission: Boolean,
    hasUsageStatsPermission: Boolean,
    hasOverlayPermission: Boolean,
    vpnPermissionLauncher: androidx.activity.result.ActivityResultLauncher<Intent>,
    context: Context
) {
    Text(
        text = stringResource(R.string.home_required_permissions),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 8.dp)
    )

    Surface(
        color = Amber.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.home_permissions_warning),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }

    PermissionCard(
        title = stringResource(R.string.home_vpn_permission_title),
        description = stringResource(R.string.home_vpn_permission_desc),
        warningText = stringResource(R.string.home_vpn_permission_warning),
        isGranted = hasVpnPermission,
        onGrant = {
            val vpnIntent = VpnService.prepare(context)
            if (vpnIntent != null) {
                vpnPermissionLauncher.launch(vpnIntent)
            }
        },
        onRevoke = {
            context.startActivity(Intent(Settings.ACTION_VPN_SETTINGS))
        }
    )

    PermissionCard(
        title = stringResource(R.string.home_usage_stats_title),
        description = stringResource(R.string.home_usage_stats_desc),
        warningText = stringResource(R.string.home_usage_stats_warning),
        isGranted = hasUsageStatsPermission,
        onGrant = {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        },
        onRevoke = {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    )

    PermissionCard(
        title = stringResource(R.string.home_overlay_permission_title),
        description = stringResource(R.string.home_overlay_permission_desc),
        warningText = stringResource(R.string.home_overlay_permission_warning),
        isGranted = hasOverlayPermission,
        onGrant = {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
            context.startActivity(intent)
        },
        onRevoke = {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
            context.startActivity(intent)
        }
    )
}

@Composable
private fun MonitoringActiveBadge(
    hasVpnPermission: Boolean,
    hasUsageStatsPermission: Boolean,
    hasOverlayPermission: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LightGreen.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null,
                    tint = LightGreen,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.home_monitoring_active_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Text(
                        text = stringResource(R.string.home_monitoring_active_desc),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            if (hasVpnPermission) {
                PermissionBadge(stringResource(R.string.perm_badge_vpn))
            }
            if (hasUsageStatsPermission) {
                PermissionBadge(stringResource(R.string.perm_badge_usage))
            }
            if (hasOverlayPermission) {
                PermissionBadge(stringResource(R.string.perm_badge_overlay))
            }
        }
    }
}

@Composable
private fun RevokePermissionsCard(context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Crimson.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.home_revoke_permissions_tip),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_manage_permissions),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    warningText: String,
    isGranted: Boolean,
    onGrant: () -> Unit,
    onRevoke: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGranted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = if (isGranted) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f) else Crimson.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = warningText,
                    fontSize = 12.sp,
                    color = if (isGranted) MaterialTheme.colorScheme.tertiaryContainer else Crimson,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (!isGranted) {
                Button(
                    onClick = onGrant,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_grant_permission),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onRevoke,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Crimson),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.home_checkmark), fontSize = 14.sp)
                        Text(
                            text = stringResource(R.string.home_permission_granted_revoke),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(LightGreen.copy(alpha = alpha))
            .scale(scale)
    )
}

@Composable
private fun PermissionBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun InstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightGreen.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.home_how_to_use),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            InstructionStep("1", stringResource(R.string.home_instruction_1))
            InstructionStep("2", stringResource(R.string.home_instruction_2))
            InstructionStep("3", stringResource(R.string.home_instruction_3))
            InstructionStep("4", stringResource(R.string.home_instruction_4))
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Amber.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_real_tracking_warning),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun InstructionStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        )
    }
}

private fun setLocale(context: Context, languageCode: String) {
    // Just update resources - NO activity restart
    val locale = Locale.forLanguageTag(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    // Update WITHOUT restarting
    @Suppress("DEPRECATION")
    context.createConfigurationContext(config)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

private fun checkVpnPermission(context: Context): Boolean {
    return VpnService.prepare(context) == null
}

private fun checkUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun startMonitoring(context: Context) {
    context.startService(Intent(context, TrackingVpnService::class.java))
    VpnStateManager.setVpnRunning(true)
    context.startService(Intent(context, OverlayService::class.java))
    VpnStateManager.setOverlayRunning(true)
    TrackingDataHolder.clearCounts()
}

private fun stopMonitoring(context: Context) {
    val vpnIntent = Intent(context, TrackingVpnService::class.java).apply {
        action = context.getString(R.string.action_stop_vpn)
    }
    context.startService(vpnIntent)
    VpnStateManager.setVpnRunning(false)
    context.stopService(Intent(context, OverlayService::class.java))
    VpnStateManager.setOverlayRunning(false)
}