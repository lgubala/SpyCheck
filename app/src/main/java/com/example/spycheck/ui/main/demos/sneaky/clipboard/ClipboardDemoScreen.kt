package com.example.spycheck.ui.main.demos.sneaky.clipboard

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.sneaky.clipboard.components.ClipboardDemoContent

@Composable
fun ClipboardDemoScreen(
    onBack: () -> Unit,
    viewModel: ClipboardDemoViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopMonitoring()
        }
    }

    DemoScaffold(
        title = stringResource(R.string.clipboard_snooping_title),
        onBack = onBack,
        permissions = emptyList(), // No permission needed
        descriptionContent = {
            DescriptionSection(
                icon = "📋",
                title = stringResource(R.string.clipboard_snooping_title),
                description = stringResource(R.string.clipboard_snooping_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getClipboardRealWorldExamples())
        },
        demoContent = {
            ClipboardDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getClipboardRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.clipboard_example_password_manager_title),
            story = stringResource(R.string.clipboard_example_password_manager_story),
            impact = stringResource(R.string.clipboard_example_password_manager_impact),
            criticalInfo = stringResource(R.string.clipboard_example_password_manager_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.clipboard_example_2fa_bypass_title),
            story = stringResource(R.string.clipboard_example_2fa_bypass_story),
            impact = stringResource(R.string.clipboard_example_2fa_bypass_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.clipboard_example_crypto_wallet_title),
            story = stringResource(R.string.clipboard_example_crypto_wallet_story),
            impact = stringResource(R.string.clipboard_example_crypto_wallet_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.clipboard_example_corporate_espionage_title),
            story = stringResource(R.string.clipboard_example_corporate_espionage_story),
            impact = stringResource(R.string.clipboard_example_corporate_espionage_impact)
        )
    )
}