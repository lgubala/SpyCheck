package com.example.spycheck.ui.main.demos.fingerprinting.network

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.fingerprinting.network.components.NetworkFingerprintDemoContent

@Composable
fun NetworkFingerprintDemoScreen(
    onBack: () -> Unit,
    viewModel: NetworkFingerprintDemoViewModel = viewModel()
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
        title = stringResource(R.string.fp_network_demo_title),
        onBack = onBack,
        permissions = emptyList(), // No permission needed
        descriptionContent = {
            DescriptionSection(
                icon = "🌐",
                title = stringResource(R.string.fp_network_desc_title),
                description = stringResource(R.string.fp_network_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getNetworkRealWorldExamples())
        },
        demoContent = {
            NetworkFingerprintDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getNetworkRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.fp_network_example1_title),
            story = stringResource(R.string.fp_network_example1_desc),
            impact = stringResource(R.string.fp_network_example1_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_network_example2_title),
            story = stringResource(R.string.fp_network_example2_desc),
            impact = stringResource(R.string.fp_network_example2_reveal)
        ),
        RealWorldExample(
            title = stringResource(R.string.fp_network_example3_title),
            story = stringResource(R.string.fp_network_example3_desc),
            impact = stringResource(R.string.fp_network_example3_reveal)
        )
    )
}