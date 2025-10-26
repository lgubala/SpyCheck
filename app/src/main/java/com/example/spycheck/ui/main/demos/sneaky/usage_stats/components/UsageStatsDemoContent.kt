package com.example.spycheck.ui.main.demos.sneaky.usage_stats.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.sneaky.usage_stats.UsageStatsDemoViewModel
import com.example.spycheck.ui.main.demos.sneaky.usage_stats.utils.*
import kotlinx.coroutines.launch

@Composable
fun UsageStatsDemoContent(viewModel: UsageStatsDemoViewModel) {
    val context = LocalContext.current
    val insights by viewModel.insights.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val error by viewModel.error.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        when {
            // Analyzing state
            isAnalyzing -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF4ECDC4))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.usage_stats_analyzing),
                        color = Color(0xFF4ECDC4),
                        fontSize = 14.sp
                    )
                }
            }

            // Error state
            error != null -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B1B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.usage_stats_error_title),
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.analyzeUsageData() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4ECDC4)
                            )
                        ) {
                            Text(stringResource(R.string.usage_stats_retry), color = Color.Black)
                        }
                    }
                }
            }

            // No data yet
            insights == null -> {
                UsageNoPermissionState(
                    onRequestPermission = { viewModel.openUsageAccessSettings() }
                )
            }

            // Show insights with tabs
            else -> {
                var currentTab by remember { mutableStateOf(0) }

                PulsingWarning()

                Spacer(modifier = Modifier.height(16.dp))

                // Tab selector
                ScrollableTabRow(
                    selectedTabIndex = currentTab,
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color(0xFF4ECDC4),
                    edgePadding = 0.dp
                ) {
                    Tab(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        text = { Text(stringResource(R.string.usage_stats_tab_routine)) }
                    )
                    Tab(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        text = { Text(stringResource(R.string.usage_stats_tab_sleep)) }
                    )
                    Tab(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        text = { Text(stringResource(R.string.usage_stats_tab_addictions)) }
                    )
                    Tab(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        text = { Text(stringResource(R.string.usage_stats_tab_work)) }
                    )
                    Tab(
                        selected = currentTab == 4,
                        onClick = { currentTab = 4 },
                        text = { Text(stringResource(R.string.usage_stats_tab_mental)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab content
                when (currentTab) {
                    0 -> DailyRoutineView(insights!!.dailyRoutine)
                    1 -> SleepAnalysisView(insights!!.sleepAnalysis)
                    2 -> AppAddictionsView(insights!!.appAddictions)
                    3 -> WorkLifeBalanceView(insights!!.workLifeBalance)
                    4 -> MentalHealthView(insights!!.mentalHealthIndicators)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.analyzeUsageData()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4ECDC4)
                        )
                    ) {
                        Text(stringResource(R.string.usage_stats_refresh), color = Color.Black)
                    }

                    Button(
                        onClick = { viewModel.openUsageAccessSettings() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B6B)
                        )
                    ) {
                        Text(stringResource(R.string.usage_stats_revoke), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun UsageNoPermissionState(onRequestPermission: () -> Unit) {
    Column {
        // Shock examples
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.usage_stats_reveals_title),
                    color = Color(0xFFFFBE0B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val examples = listOf(
                    stringResource(R.string.usage_stats_reveal1),
                    stringResource(R.string.usage_stats_reveal2),
                    stringResource(R.string.usage_stats_reveal3),
                    stringResource(R.string.usage_stats_reveal4),
                    stringResource(R.string.usage_stats_reveal5),
                    stringResource(R.string.usage_stats_reveal6)
                )

                examples.forEach { example ->
                    Text(
                        text = example,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ECDC4)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.usage_stats_analyze_button), color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B6B)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.usage_stats_revoke_button), color = Color.White)
        }
    }
}

@Composable
private fun PulsingWarning() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B1B)),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚠️",
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(
                    text = stringResource(R.string.usage_stats_warning_title),
                    color = Color(0xFFFF6B6B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.usage_stats_warning_desc),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun DailyRoutineView(routine: DailyRoutine) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.usage_stats_routine_title),
                color = Color(0xFF4ECDC4),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(stringResource(R.string.usage_stats_wake_time), routine.wakeUpTime)
            InfoRow(stringResource(R.string.usage_stats_sleep_time), routine.sleepTime)
            InfoRow(stringResource(R.string.usage_stats_most_active), routine.mostActiveHour)
            InfoRow(stringResource(R.string.usage_stats_phone_checks), "${routine.phoneCheckCount}")
            InfoRow(stringResource(R.string.usage_stats_first_app), routine.firstAppOfDay)
            InfoRow(stringResource(R.string.usage_stats_last_app), routine.lastAppBeforeSleep)

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B1B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = routine.routineDescription,
                    color = Color(0xFFFFBE0B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun SleepAnalysisView(sleep: SleepAnalysis) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.usage_stats_sleep_title),
                color = Color(0xFF4ECDC4),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                stringResource(R.string.usage_stats_sleep_duration),
                stringResource(R.string.usage_stats_hours_format, sleep.lastNightSleepDuration)
            )
            InfoRow(
                stringResource(R.string.usage_stats_sleep_quality),
                "${sleep.sleepQualityScore}/100"
            )
            InfoRow(
                stringResource(R.string.usage_stats_midnight_usage),
                stringResource(R.string.usage_stats_minutes_format, sleep.midnightUsage)
            )
            InfoRow(
                stringResource(R.string.usage_stats_night_wakeups),
                "${sleep.timesWokeUpToCheckPhone}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B1B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = sleep.verdict,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun AppAddictionsView(addictions: List<AppAddiction>) {
    Column {
        if (addictions.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.usage_stats_no_addictions),
                    color = Color(0xFF4ECDC4),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            addictions.forEach { addiction ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (addiction.addictionLevel) {
                            "Severe" -> Color(0xFF3D1B1B)
                            "Moderate" -> Color(0xFF3D2B1B)
                            else -> Color(0xFF1A1A1A)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = addiction.appName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (addiction.addictionLevel) {
                                        "Severe" -> Color(0xFFFF6B6B)
                                        "Moderate" -> Color(0xFFFFBE0B)
                                        else -> Color(0xFF4ECDC4)
                                    }
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = addiction.addictionLevel,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(
                            stringResource(R.string.usage_stats_daily_usage),
                            stringResource(R.string.usage_stats_minutes_format, addiction.dailyUsage)
                        )
                        InfoRow(
                            stringResource(R.string.usage_stats_sessions),
                            "${addiction.sessionCount}"
                        )
                        InfoRow(
                            stringResource(R.string.usage_stats_longest_session),
                            stringResource(R.string.usage_stats_minutes_format, addiction.longestSession)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = addiction.concerningPattern,
                            color = Color(0xFFFFBE0B),
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkLifeBalanceView(balance: WorkLifeBalance) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.usage_stats_work_balance_title),
                color = Color(0xFF4ECDC4),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                stringResource(R.string.usage_stats_work_usage),
                stringResource(R.string.usage_stats_minutes_format, balance.workAppUsage)
            )
            InfoRow(
                stringResource(R.string.usage_stats_entertainment_usage),
                stringResource(R.string.usage_stats_minutes_format, balance.entertainmentUsage)
            )
            InfoRow(
                stringResource(R.string.usage_stats_productivity_score),
                "${balance.productivityScore}/100"
            )

            if (balance.workHoursViolations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.usage_stats_violations_title),
                    color = Color(0xFFFF6B6B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                balance.workHoursViolations.forEach { violation ->
                    Text(
                        text = "• $violation",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1B1B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = balance.balanceVerdict,
                    color = Color(0xFFFFBE0B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun MentalHealthView(mental: MentalHealthIndicators) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.usage_stats_mental_health_title),
                    color = Color(0xFF4ECDC4),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            mental.overallRiskLevel.contains("HIGH") -> Color(0xFFFF6B6B)
                            mental.overallRiskLevel.contains("MEDIUM") -> Color(0xFFFFBE0B)
                            else -> Color(0xFF4ECDC4)
                        }
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = mental.overallRiskLevel,
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(
                stringResource(R.string.usage_stats_doomscrolling),
                "${mental.doomScrollingSessions}"
            )
            InfoRow(
                stringResource(R.string.usage_stats_late_night_social),
                stringResource(R.string.usage_stats_minutes_format, mental.lateNightSocialMedia)
            )

            if (mental.anxietyIndicators.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.usage_stats_anxiety_indicators_title),
                    color = Color(0xFFFF6B6B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                mental.anxietyIndicators.forEach { indicator ->
                    Text(
                        text = "• $indicator",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            if (mental.moodAffectingApps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.usage_stats_mood_apps_title),
                    color = Color(0xFFFFBE0B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                mental.moodAffectingApps.forEach { app ->
                    Text(
                        text = "• $app",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
