package com.example.spycheck.ui.main.demos.fingerprinting.performance

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.fingerprinting.performance.components.PerformanceFingerprintDemoContent

@Composable
fun PerformanceFingerprintDemoScreen(
    onBack: () -> Unit,
    viewModel: PerformanceFingerprintDemoViewModel = viewModel()
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
        title = stringResource(R.string.fp_performance_demo_title),
        onBack = onBack,
        permissions = emptyList(), // No permission needed
        descriptionContent = {
            DescriptionSection(
                icon = "⚡",
                title = stringResource(R.string.fp_performance_desc_title),
                description = stringResource(R.string.fp_performance_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getPerformanceRealWorldExamples())
        },
        demoContent = {
            PerformanceFingerprintDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getPerformanceRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.fp_performance_example1_title),
            story = stringResource(R.string.fp_performance_example1_desc),
            impact = stringResource(R.string.fp_performance_example1_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_performance_example2_title),
            story = stringResource(R.string.fp_performance_example2_desc),
            impact = stringResource(R.string.fp_performance_example2_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_performance_example3_title),
            story = stringResource(R.string.fp_performance_example3_desc),
            impact = stringResource(R.string.fp_performance_example3_reveal)
        )
    )
}