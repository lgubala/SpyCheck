package com.example.spycheck.ui.main.demos.sneaky.notifications

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.sneaky.notifications.components.NotificationDemoContent

@Composable
fun NotificationDemoScreen(
    onBack: () -> Unit,
    viewModel: NotificationDemoViewModel = viewModel()
) {
    val context = LocalContext.current
    val hasPermission by viewModel.hasPermission.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.checkPermission(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.checkPermission(context)
    }

    DemoScaffold(
        title = stringResource(R.string.notification_stealing_title),
        onBack = onBack,
        permissions = listOf(
            DemoPermission(
                name = stringResource(R.string.perm_notifications_name),
                description = stringResource(R.string.notification_permission_explanation),
                isGranted = hasPermission,
                onRequest = {
                    settingsLauncher.launch(
                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    )
                },
                settingsAction = "android.settings.NOTIFICATION_LISTENER_SETTINGS"
            )
        ),
        descriptionContent = {
            DescriptionSection(
                icon = "🔔",
                title = stringResource(R.string.notification_stealing_title),
                description = stringResource(R.string.notification_stealing_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getNotificationRealWorldExamples())
        },
        demoContent = {
            if (hasPermission) {
                NotificationDemoContent(viewModel = viewModel)
            }
        }
    )
}

@Composable
private fun getNotificationRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.notification_example_banking_theft_title),
            story = stringResource(R.string.notification_example_banking_theft_story),
            impact = stringResource(R.string.notification_example_banking_theft_impact),
            criticalInfo = stringResource(R.string.notification_example_banking_theft_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.notification_example_stalker_title),
            story = stringResource(R.string.notification_example_stalker_story),
            impact = stringResource(R.string.notification_example_stalker_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.notification_example_delivery_tracking_title),
            story = stringResource(R.string.notification_example_delivery_tracking_story),
            impact = stringResource(R.string.notification_example_delivery_tracking_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.notification_example_corporate_spy_title),
            story = stringResource(R.string.notification_example_corporate_spy_story),
            impact = stringResource(R.string.notification_example_corporate_spy_impact)
        )
    )
}