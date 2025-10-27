package com.example.spycheck.ui.main
import android.app.Activity
import android.app.AppOpsManager
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Language
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
import com.example.spycheck.ui.theme.BackgroundDark
import com.example.spycheck.ui.theme.Crimson
import com.example.spycheck.ui.theme.CrimsonDark
import com.example.spycheck.ui.theme.LightGreen
import com.example.spycheck.ui.theme.SurfaceDark
import com.example.spycheck.ui.theme.TextPrimary
import java.util.Locale

@Composable
fun HomeScreen() {
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
        // Always recheck permission after result
        hasVpnPermission = checkVpnPermission(context)
    }

    // Refresh permissions when app resumes (user comes back from settings)
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
                        BackgroundDark,
                        SurfaceDark
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            var showLanguageDialog by remember { mutableStateOf(false) }

            IconButton(onClick = { showLanguageDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = stringResource(R.string.home_change_language),
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }

            if (showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = { showLanguageDialog = false },
                    title = { Text(stringResource(id = R.string.language)) },
                    text = {
                        Column {
                            TextButton(
                                onClick = {
                                    setLocale(context, context.getString(R.string.locale_code_english))
                                    showLanguageDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ðŸ‡¬ðŸ‡§ ${stringResource(id = R.string.english)}")
                            }
                            TextButton(
                                onClick = {
                                    setLocale(context, context.getString(R.string.locale_code_slovak))
                                    showLanguageDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ðŸ‡¸ðŸ‡° ${stringResource(id = R.string.slovak)}")
                            }
                            TextButton(
                                onClick = {
                                    setLocale(context, context.getString(R.string.locale_code_spanish))
                                    showLanguageDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ðŸ‡ªðŸ‡¸ ${stringResource(id = R.string.spanish)}")
                            }
                        }
                    },
                    confirmButton = {}
                )
            }
        }

        // Header Section
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
                text = stringResource(id = R.string.home_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.home_subtitle),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Start Watching Button - ALWAYS SHOW (but disabled if permissions missing)
        StartWatchingButton(
            isMonitoring = isMonitoring,
            allPermissionsGranted = allPermissionsGranted,
            trackingCount = trackingCount,
            onToggle = {
                if (isMonitoring) {
                    stopMonitoring(context)
                } else {
                    startMonitoring(context)
                }
            }
        )
        // Instructions - only show when all permissions granted but not monitoring
        if (allPermissionsGranted && !isMonitoring) {
            InstructionsCard()
        }
        // Warning Card - Show when permissions granted but NOT monitoring
        AnimatedVisibility(visible = allPermissionsGranted && !isMonitoring) {
            PermissionsGrantedWarningCard()
        }

        // Permission Cards Section - ALWAYS SHOW (except when monitoring)
        if (!isMonitoring) {
            Text(
                text = if (allPermissionsGranted) stringResource(R.string.home_manage_permissions) else stringResource(id = R.string.home_required_permissions),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (!allPermissionsGranted) {
                Text(
                    text = stringResource(id = R.string.home_permissions_warning),
                    fontSize = 12.sp,
                    color = Amber,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            // VPN Permission Card - ALWAYS VISIBLE
            PermissionCard(
                title = stringResource(id = R.string.home_vpn_permission_title),
                description = stringResource(id = R.string.home_vpn_permission_desc),
                icon = stringResource(id = R.string.icon_vpn),
                isGranted = hasVpnPermission,
                onGrant = {
                    val vpnIntent = VpnService.prepare(context)
                    if (vpnIntent != null) {
                        vpnPermissionLauncher.launch(vpnIntent)
                    } else {
                        hasVpnPermission = true
                    }
                },
                onRevoke = {
                    context.startActivity(Intent(Settings.ACTION_VPN_SETTINGS))
                },
                warningText = stringResource(id = R.string.home_vpn_permission_warning)
            )

            // Usage Stats Permission Card - ALWAYS VISIBLE
            PermissionCard(
                title = stringResource(id = R.string.home_usage_stats_title),
                description = stringResource(id = R.string.home_usage_stats_desc),
                icon = stringResource(id = R.string.icon_usage_stats),
                isGranted = hasUsageStatsPermission,
                onGrant = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
                onRevoke = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
                warningText = stringResource(id = R.string.home_usage_stats_warning)
            )

            // Overlay Permission Card - ALWAYS VISIBLE
            PermissionCard(
                title = stringResource(id = R.string.home_overlay_permission_title),
                description = stringResource(id = R.string.home_overlay_permission_desc),
                icon = stringResource(id = R.string.icon_overlay),
                isGranted = hasOverlayPermission,
                onGrant = {
                    val overlayIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${context.packageName}".toUri()
                    )
                    context.startActivity(overlayIntent)
                },
                onRevoke = {
                    val overlayIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:${context.packageName}".toUri()
                    )
                    context.startActivity(overlayIntent)
                },
                warningText = stringResource(id = R.string.home_overlay_permission_warning)
            )
        }



        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StartWatchingButton(
    isMonitoring: Boolean,
    allPermissionsGranted: Boolean,
    trackingCount: Int,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isMonitoring) {
                PulsingDot()
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = if (isMonitoring) {
                    stringResource(id = R.string.home_watching_live)
                } else if (allPermissionsGranted) {
                    stringResource(id = R.string.home_ready_to_start)
                } else {
                    stringResource(R.string.home_grant_permissions_first)
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMonitoring) Crimson else Color.White.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )

            if (isMonitoring) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = trackingCount.toString(),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Crimson
                )
                Text(
                    text = stringResource(id = R.string.home_tracking_attempts),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(id = R.string.home_apps_sending_data),
                    fontSize = 11.sp,
                    color = Amber,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onToggle,
                enabled = allPermissionsGranted || isMonitoring,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMonitoring) Crimson else SurfaceDark,
                    disabledContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isMonitoring)
                        stringResource(id = R.string.home_stop_watching)
                    else
                        stringResource(id = R.string.home_start_watching),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isMonitoring) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = R.string.home_minimize_app),
                    fontSize = 10.sp,
                    color = LightGreen,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PermissionsGrantedWarningCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Amber.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.icon_warning), fontSize = 32.sp)
                Column {
                    Text(
                        text = stringResource(R.string.home_all_permissions_granted),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonDark
                    )
                    Text(
                        text = stringResource(id = R.string.home_monitoring_active_desc),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PermissionBadge(stringResource(id = R.string.perm_badge_vpn))
            PermissionBadge(stringResource(id = R.string.perm_badge_usage))
            PermissionBadge(stringResource(id = R.string.perm_badge_overlay))

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color =Crimson.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_revoke_permissions_tip),
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 18.sp,
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
    icon: String,
    isGranted: Boolean,
    onGrant: () -> Unit,
    onRevoke: () -> Unit,
    warningText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted)
                LightGreen.copy(alpha = 0.15f)
            else
                SurfaceDark
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = icon, fontSize = 32.sp)
                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = description,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // Status Indicator
                Surface(
                    shape = CircleShape,
                    color = if (isGranted) Crimson else LightGreen.copy(alpha = 0.3f),
                    modifier = Modifier.size(12.dp)
                ) {}
            }

            // Warning text (only when not granted)
            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Crimson.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.icon_warning), fontSize = 14.sp)
                        Text(
                            text = warningText,
                            fontSize = 12.sp,
                            color = Crimson,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grant/Revoke Button
            if (!isGranted) {
                // Grant Button
                Button(
                    onClick = onGrant,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Crimson
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.home_grant_permission),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Revoke Button
                OutlinedButton(
                    onClick = onRevoke,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LightGreen
                    ),
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
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun InstructionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LightGreen.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.home_how_to_use),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = LightGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            InstructionStep("1", stringResource(id = R.string.home_instruction_1))
            InstructionStep("2", stringResource(id = R.string.home_instruction_2))
            InstructionStep("3", stringResource(id = R.string.home_instruction_3))
            InstructionStep("4", stringResource(id = R.string.home_instruction_4))

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Amber.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_real_tracking_warning),
                    fontSize = 12.sp,
                    color = Amber,
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
            color = LightGreen
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
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

private fun setLocale(context: Context, languageCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(languageCode)
    } else {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.create(Locale.forLanguageTag(languageCode))
        )
    }
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