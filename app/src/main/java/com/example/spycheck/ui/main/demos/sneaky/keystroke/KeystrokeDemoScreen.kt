package com.example.spycheck.ui.main.demos.sneaky.keystroke

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.common.*
import com.example.spycheck.ui.main.demos.sneaky.keystroke.components.KeystrokeDemoContent

@Composable
fun KeystrokeDemoScreen(
    onBack: () -> Unit
) {
    val viewModel: KeystrokeDemoViewModel = viewModel()

    DemoScaffold(
        title = stringResource(R.string.keystroke_title),
        onBack = onBack,
        permissions = emptyList(), // No permission needed
        descriptionContent = {
            DescriptionSection(
                icon = "⌨️",
                title = stringResource(R.string.keystroke_desc_title),
                description = stringResource(R.string.keystroke_desc_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getKeystrokeRealWorldExamples())
        },
        demoContent = {
            KeystrokeDemoContent(viewModel = viewModel)
        }
    )
}

@Composable
private fun getKeystrokeRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.keystroke_example_banking_title),
            story = stringResource(R.string.keystroke_example_banking_story),
            impact = stringResource(R.string.keystroke_example_banking_impact),
            criticalInfo = stringResource(R.string.keystroke_example_banking_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.keystroke_example_corporate_title),
            story = stringResource(R.string.keystroke_example_corporate_story),
            impact = stringResource(R.string.keystroke_example_corporate_impact),
            criticalInfo = stringResource(R.string.keystroke_example_corporate_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.keystroke_example_gaming_title),
            story = stringResource(R.string.keystroke_example_gaming_story),
            impact = stringResource(R.string.keystroke_example_gaming_impact),
            criticalInfo = stringResource(R.string.keystroke_example_gaming_critical)
        )
    )
}