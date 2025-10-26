package com.example.spycheck.ui.main.demos.fingerprinting.audio

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.fingerprinting.audio.components.AudioFingerprintDemoContent

@Composable
fun AudioFingerprintDemoScreen(
    onBack: () -> Unit,
    viewModel: AudioFingerprintDemoViewModel = viewModel()
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

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAnalysis()
        }
    }

    val permissions = listOf(
        DemoPermission(
            name = "Audio Recording",
            description = stringResource(R.string.fp_audio_permission_desc),
            isGranted = hasAudioPermission,
            onRequest = {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            },
            settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
        )
    )

    DemoScaffold(
        title = stringResource(R.string.fp_audio_demo_title),
        onBack = onBack,
        permissions = permissions,
        descriptionContent = {
            DescriptionSection(
                icon = "🎵",
                title = stringResource(R.string.fp_audio_desc_title),
                description = stringResource(R.string.fp_audio_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getAudioRealWorldExamples())
        },
        demoContent = {
            AudioFingerprintDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getAudioRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.fp_audio_example1_title),
            story = stringResource(R.string.fp_audio_example1_desc),
            impact = stringResource(R.string.fp_audio_example1_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_audio_example2_title),
            story = stringResource(R.string.fp_audio_example2_desc),
            impact = stringResource(R.string.fp_audio_example2_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_audio_example3_title),
            story = stringResource(R.string.fp_audio_example3_desc),
            impact = stringResource(R.string.fp_audio_example3_reveal)
        )
    )
}