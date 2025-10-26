package com.example.spycheck.ui.main.demos.fingerprinting.sensor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.fingerprinting.sensor.components.SensorFingerprintDemoContent

@Composable
fun SensorFingerprintDemoScreen(
    onBack: () -> Unit,
    viewModel: SensorFingerprintDemoViewModel = viewModel()
) {
    val context = LocalContext.current

    var hasSensorPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // No permission needed on Android 9 and below
            }
        )
    }

    val sensorPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasSensorPermission = isGranted
    }

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAnalysis()
        }
    }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(
            DemoPermission(
                name = "Activity Recognition",
                description = stringResource(R.string.fp_sensor_permission_desc),
                isGranted = hasSensorPermission,
                onRequest = {
                    sensorPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                },
                settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
            )
        )
    } else {
        emptyList() // No permission needed on Android 9 and below
    }

    DemoScaffold(
        title = stringResource(R.string.fp_sensor_demo_title),
        onBack = onBack,
        permissions = permissions,
        descriptionContent = {
            DescriptionSection(
                icon = "📊",
                title = stringResource(R.string.fp_sensor_desc_title),
                description = stringResource(R.string.fp_sensor_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getSensorRealWorldExamples())
        },
        demoContent = {
            SensorFingerprintDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getSensorRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.fp_sensor_example1_title),
            story = stringResource(R.string.fp_sensor_example1_desc),
            impact = stringResource(R.string.fp_sensor_example1_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_sensor_example2_title),
            story = stringResource(R.string.fp_sensor_example2_desc),
            impact = stringResource(R.string.fp_sensor_example2_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_sensor_example3_title),
            story = stringResource(R.string.fp_sensor_example3_desc),
            impact = stringResource(R.string.fp_sensor_example3_reveal)
        )
    )
}