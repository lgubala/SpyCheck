package com.example.spycheck.ui.main.demos.sneaky.exif

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
import com.example.spycheck.ui.main.demos.sneaky.exif.components.ExifDemoContent

@Composable
fun ExifDemoScreen(
    onBack: () -> Unit,
    viewModel: ExifDemoViewModel = viewModel()
) {
    val context = LocalContext.current
    val hasPermission by viewModel.hasPermission.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermission(context)
    }



    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasAllPermissions = permissions.all { it.value }
        viewModel.updatePermission(hasAllPermissions)
        if (hasAllPermissions) {
            viewModel.loadRandomPhoto(context)
        }
    }

    DemoScaffold(
        title = stringResource(R.string.exif_gps_title),
        onBack = onBack,
        permissions = listOf(
            DemoPermission(
                name = stringResource(R.string.perm_photos_name),
                description = stringResource(R.string.exif_permission_explanation),
                isGranted = hasPermission,
                onRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissionLauncher.launch(arrayOf(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.ACCESS_MEDIA_LOCATION
                        ))
                    } else {
                        permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                    }
                },
                settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
            )
        ),
        descriptionContent = {
            DescriptionSection(
                icon = "📸",
                title = stringResource(R.string.exif_gps_title),
                description = stringResource(R.string.exif_gps_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getExifRealWorldExamples())
        },
        demoContent = {
            if (hasPermission) {
                ExifDemoContent(viewModel = viewModel)
            }
        }
    )
}

@Composable
private fun getExifRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.exif_example_dating_stalker_title),
            story = stringResource(R.string.exif_example_dating_stalker_story),
            impact = stringResource(R.string.exif_example_dating_stalker_impact),
            criticalInfo = stringResource(R.string.exif_example_dating_stalker_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.exif_example_social_profile_title),
            story = stringResource(R.string.exif_example_social_profile_story),
            impact = stringResource(R.string.exif_example_social_profile_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.exif_example_insurance_fraud_title),
            story = stringResource(R.string.exif_example_insurance_fraud_story),
            impact = stringResource(R.string.exif_example_insurance_fraud_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.exif_example_job_rejection_title),
            story = stringResource(R.string.exif_example_job_rejection_story),
            impact = stringResource(R.string.exif_example_job_rejection_impact)
        )
    )
}