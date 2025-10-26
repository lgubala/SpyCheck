package com.example.spycheck.ui.main.demos.fingerprinting.battery

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.fingerprinting.battery.components.BatteryFingerprintDemoContent

@Composable
fun BatteryFingerprintDemoScreen(
    onBack: () -> Unit,
    viewModel: BatteryFingerprintDemoViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAnalysis()
        }
    }

    DemoScaffold(
        title = stringResource(R.string.fp_battery_demo_title),
        onBack = onBack,
        permissions = emptyList(), // No permission needed
        descriptionContent = {
            DescriptionSection(
                icon = "🔋",
                title = stringResource(R.string.fp_battery_desc_title),
                description = stringResource(R.string.fp_battery_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getBatteryRealWorldExamples())
        },
        demoContent = {
            BatteryFingerprintDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getBatteryRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.fp_battery_example1_title),
            story = stringResource(R.string.fp_battery_example1_desc),
            impact = stringResource(R.string.fp_battery_example1_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_battery_example2_title),
            story = stringResource(R.string.fp_battery_example2_desc),
            impact = stringResource(R.string.fp_battery_example2_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_battery_example3_title),
            story = stringResource(R.string.fp_battery_example3_desc),
            impact = stringResource(R.string.fp_battery_example3_reveal)
        )
    )
}