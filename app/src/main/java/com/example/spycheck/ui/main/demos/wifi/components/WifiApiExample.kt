package com.example.spycheck.ui.main.demos.wifi.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.wifi.ApiProvider
import com.example.spycheck.ui.main.demos.wifi.LocationResult
import com.example.spycheck.ui.main.demos.wifi.WifiDemoViewModel
import com.example.spycheck.ui.theme.SuccessGreen

@Composable
fun WifiApiExample(
    viewModel: WifiDemoViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.wifi_api_example_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.wifi_api_example_description),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = stringResource(R.string.wifi_api_select_provider),
            fontWeight = FontWeight.SemiBold
        )

        ProviderSelector(
            selected = state.apiProvider,
            onSelect = { viewModel.setApiProvider(it) }
        )

        if (state.apiProvider != ApiProvider.MOZILLA) {
            OutlinedTextField(
                value = state.apiKey,
                onValueChange = viewModel::setApiKey,
                label = { Text(stringResource(R.string.wifi_api_key_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = { viewModel.locateUserViaApi() },
            enabled = !state.isLocating,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLocating) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Text(stringResource(R.string.wifi_locating))
            } else {
                Text(stringResource(R.string.wifi_locate_button))
            }
        }

        state.locationResult?.let { result ->
            LocationResultView(viewModel, result)
        }
    }
}

@Composable
fun ProviderSelector(selected: ApiProvider, onSelect: (ApiProvider) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ProviderOption(
            label = stringResource(R.string.wifi_api_provider_google),
            selected = selected == ApiProvider.GOOGLE
        ) { onSelect(ApiProvider.GOOGLE) }

        ProviderOption(
            label = stringResource(R.string.wifi_api_provider_mozilla),
            selected = selected == ApiProvider.MOZILLA
        ) { onSelect(ApiProvider.MOZILLA) }

        ProviderOption(
            label = stringResource(R.string.wifi_api_provider_unwired),
            selected = selected == ApiProvider.UNWIRED
        ) { onSelect(ApiProvider.UNWIRED) }
    }
}

@Composable
fun ProviderOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label)
    }
}

@Composable
fun LocationResultView(viewModel: WifiDemoViewModel, result: LocationResult) {
    val context = LocalContext.current
    val mapUrl = viewModel.getStaticMapUrl(result)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = SuccessGreen.copy(alpha = 0.1f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📍",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = stringResource(R.string.wifi_location_found),
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = "Lat: %.5f, Lng: %.5f".format(result.latitude, result.longitude),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                result.accuracy?.let { accuracy ->
                    Text(
                        text = "Accuracy: ~${accuracy.toInt()}m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Try to load map with AsyncImage (Coil)
        var imageLoadFailed by remember { mutableStateOf(false) }

        if (!imageLoadFailed) {
            AsyncImage(
                model = mapUrl,
                contentDescription = "Location map",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                onError = { imageLoadFailed = true }
            )
        }

        // If image fails or as alternative, show button to open in maps
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("geo:${result.latitude},${result.longitude}?q=${result.latitude},${result.longitude}")
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🗺️ Open in Maps")
        }

        if (imageLoadFailed) {
            Text(
                text = "Map preview unavailable. Use the button above to view location in your maps app.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}