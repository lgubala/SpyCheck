package com.example.spycheck.ui.main.demos.sneaky.usage_stats

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.sneaky.usage_stats.components.UsageStatsDemoContent

@Composable
fun UsageStatsDemoScreen(
    onBack: () -> Unit
) {
    val viewModel: UsageStatsDemoViewModel = viewModel()
    val context = LocalContext.current
    val hasPermission by viewModel.hasPermission.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermission(context)
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.checkPermission(context)
    }

    DemoScaffold(
        title = stringResource(R.string.usage_stats_title),
        onBack = onBack,
        permissions = listOf(
            DemoPermission(
                name = stringResource(R.string.perm_usage_stats_name),
                description = stringResource(R.string.usage_stats_permission_desc),
                isGranted = hasPermission,
                onRequest = {
                    settingsLauncher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                },
                settingsAction = "android.settings.USAGE_ACCESS_SETTINGS"
            )
        ),
        descriptionContent = {
            DescriptionSection(
                icon = "📊",
                title = stringResource(R.string.usage_stats_desc_title),
                description = stringResource(R.string.usage_stats_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(
                examples = listOf(
                    RealWorldExample(
                        title = stringResource(R.string.usage_stats_example1_title),
                        story = stringResource(R.string.usage_stats_example1_desc),
                        impact = stringResource(R.string.usage_stats_example1_reveal),
                        criticalInfo = stringResource(R.string.usage_stats_critical_info)
                    ),
                    RealWorldExample(
                        title = stringResource(R.string.usage_stats_example2_title),
                        story = stringResource(R.string.usage_stats_example2_desc),
                        impact = stringResource(R.string.usage_stats_example2_reveal)
                    ),
                    RealWorldExample(
                        title = stringResource(R.string.usage_stats_example3_title),
                        story = stringResource(R.string.usage_stats_example3_desc),
                        impact = stringResource(R.string.usage_stats_example3_reveal)
                    )
                )
            )
        },
        demoContent = {
            if (hasPermission) {
                UsageStatsDemoContent(viewModel = viewModel)
            }
        }
    )
}