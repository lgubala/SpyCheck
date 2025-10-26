package com.example.spycheck.ui.main.demos.fingerprinting.combined

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
import com.example.spycheck.ui.main.demos.fingerprinting.combined.components.SuperFingerprintDemoContent

@Composable
fun SuperFingerprintDemoScreen(
    onBack: () -> Unit,
    viewModel: SuperFingerprintDemoViewModel = viewModel()
) {
    val context = LocalContext.current

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasSensorPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
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

    val permissions = buildList {
        add(
            DemoPermission(
                name = "Audio Recording",
                description = "For audio hardware fingerprinting",
                isGranted = hasAudioPermission,
                onRequest = {
                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(
                DemoPermission(
                    name = "Activity Recognition",
                    description = "For sensor fingerprinting",
                    isGranted = hasSensorPermission,
                    onRequest = {
                        sensorPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                    },
                    settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
                )
            )
        }
    }

    DemoScaffold(
        title = stringResource(R.string.fp_combined_demo_title),
        onBack = onBack,
        permissions = permissions,
        descriptionContent = {
            DescriptionSection(
                icon = "🎯",
                title = stringResource(R.string.fp_combined_desc_title),
                description = stringResource(R.string.fp_combined_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getCombinedRealWorldExamples())
        },
        demoContent = {
            SuperFingerprintDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getCombinedRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.fp_combined_example1_title),
            story = stringResource(R.string.fp_combined_example1_desc),
            impact = stringResource(R.string.fp_combined_example1_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_combined_example2_title),
            story = stringResource(R.string.fp_combined_example2_desc),
            impact = stringResource(R.string.fp_combined_example2_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_combined_example3_title),
            story = stringResource(R.string.fp_combined_example3_desc),
            impact = stringResource(R.string.fp_combined_example3_reveal)
        )
    )
}