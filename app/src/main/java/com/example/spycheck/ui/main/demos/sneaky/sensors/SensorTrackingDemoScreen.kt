package com.example.spycheck.ui.main.demos.sneaky.sensors

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.sneaky.sensors.components.SensorTrackingDemoContent

@Composable
fun SensorTrackingDemoScreen(
    onBack: () -> Unit
) {
    val viewModel: SensorTrackingDemoViewModel = viewModel()
    val context = LocalContext.current
    val hasPermission by viewModel.hasPermission.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermission(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updatePermission(isGranted)
    }

    DemoScaffold(
        title = stringResource(R.string.sensor_tracking_title),
        onBack = onBack,
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(
                DemoPermission(
                    name = stringResource(R.string.perm_activity_name),
                    description = stringResource(R.string.sensor_tracking_permission_desc),
                    isGranted = hasPermission,
                    onRequest = { permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) },
                    settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
                )
            )
        } else emptyList(),
        descriptionContent = {
            DescriptionSection(
                icon = "📱",
                title = stringResource(R.string.sensor_tracking_desc_title),
                description = stringResource(R.string.sensor_tracking_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getSensorTrackingRealWorldExamples())
        },
        demoContent = {
            if (hasPermission) {
                SensorTrackingDemoContent(viewModel = viewModel)
            }
        }
    )
}

@Composable
private fun getSensorTrackingRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.sensor_tracking_example_fitness_title),
            story = stringResource(R.string.sensor_tracking_example_fitness_story),
            impact = stringResource(R.string.sensor_tracking_example_fitness_impact),
            criticalInfo = stringResource(R.string.sensor_tracking_example_fitness_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.sensor_tracking_example_insurance_title),
            story = stringResource(R.string.sensor_tracking_example_insurance_story),
            impact = stringResource(R.string.sensor_tracking_example_insurance_impact),
            criticalInfo = stringResource(R.string.sensor_tracking_example_insurance_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.sensor_tracking_example_employer_title),
            story = stringResource(R.string.sensor_tracking_example_employer_story),
            impact = stringResource(R.string.sensor_tracking_example_employer_impact),
            criticalInfo = stringResource(R.string.sensor_tracking_example_employer_critical)
        )
    )
}