package com.example.spycheck.ui.main.demos.sneaky.wifi

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
import com.example.spycheck.ui.main.demos.sneaky.wifi.components.WifiDemoContent

@Composable
fun WifiDemoScreen(
    onBack: () -> Unit,
    viewModel: WifiDemoViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermissions(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onLocationPermissionResult(isGranted)
        if (isGranted) {
            viewModel.checkPermissions(context)
        }
    }

    val wifiPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onWifiPermissionResult(isGranted)
        if (isGranted) {
            viewModel.checkPermissions(context)
        }
    }

    val allGranted = state.hasLocationPermission &&
            (state.hasWifiPermission || !viewModel.needsWifiPermission())

    DemoScaffold(
        title = stringResource(R.string.wifi_location_title),
        onBack = onBack,
        permissions = buildList {
            add(
                DemoPermission(
                    name = stringResource(R.string.wifi_perm_location_name),
                    description = stringResource(R.string.wifi_perm_location_desc),
                    isGranted = state.hasLocationPermission,
                    onRequest = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                    settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
                )
            )
            if (viewModel.needsWifiPermission()) {
                add(
                    DemoPermission(
                        name = stringResource(R.string.wifi_perm_nearby_name),
                        description = stringResource(R.string.wifi_perm_nearby_desc),
                        isGranted = state.hasWifiPermission,
                        onRequest = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                wifiPermissionLauncher.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
                            }
                        },
                        settingsAction = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    )
                )
            }
        },
        descriptionContent = {
            DescriptionSection(
                icon = "📡",
                title = stringResource(R.string.wifi_location_title),
                description = stringResource(R.string.wifi_location_long)
            )
        },
        examplesContent = {
            RealWorldExamplesSection(examples = getWifiRealWorldExamples())
        },
        demoContent = {
            if (allGranted) {
                WifiDemoContent(viewModel = viewModel)
            }
        }
    )
}

@Composable
private fun getWifiRealWorldExamples(): List<RealWorldExample> {
    return listOf(
        RealWorldExample(
            title = stringResource(R.string.wifi_example_shopping_mall_title),
            story = stringResource(R.string.wifi_example_shopping_mall_story),
            impact = stringResource(R.string.wifi_example_shopping_mall_impact),
            criticalInfo = stringResource(R.string.wifi_example_shopping_mall_critical)
        ),
        RealWorldExample(
            title = stringResource(R.string.wifi_example_stalker_ex_title),
            story = stringResource(R.string.wifi_example_stalker_ex_story),
            impact = stringResource(R.string.wifi_example_stalker_ex_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.wifi_example_employee_surveillance_title),
            story = stringResource(R.string.wifi_example_employee_surveillance_story),
            impact = stringResource(R.string.wifi_example_employee_surveillance_impact)
        ),
        RealWorldExample(
            title = stringResource(R.string.wifi_example_protestor_tracking_title),
            story = stringResource(R.string.wifi_example_protestor_tracking_story),
            impact = stringResource(R.string.wifi_example_protestor_tracking_impact)
        )
    )
}