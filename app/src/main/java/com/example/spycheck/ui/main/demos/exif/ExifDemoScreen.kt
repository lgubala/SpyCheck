package com.example.spycheck.ui.main.demos.exif

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.ui.main.demos.exif.components.ExifInfoCard
import com.example.spycheck.ui.main.demos.exif.components.InfoSection
import com.example.spycheck.ui.main.demos.exif.components.PermissionButton
import com.example.spycheck.ui.main.demos.exif.components.PermissionWarning
import com.example.spycheck.ui.main.demos.exif.components.PhotoPreview

@Composable
fun ExifDemoScreen() {
    val viewModel: ExifDemoViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    // Observe lifecycle to recheck permission when returning from settings
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.recheckPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Determine which permission to request based on Android version
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item { InfoSection() }
        item { Spacer(Modifier.height(16.dp)) }
        item {
            PermissionButton(
                hasPermission = state.hasPermission,
                onRequest = {
                    permissionLauncher.launch(permission)
                }
            )
        }

        if (state.hasPermission) {
            if (state.isLoading) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                }
            } else if (state.photoData != null) {
                item { Spacer(Modifier.height(16.dp)) }
                item { PhotoPreview(state.photoData!!, state.isRevealed) { viewModel.revealPhoto() } }
                item { Spacer(Modifier.height(16.dp)) }
                item { ExifInfoCard(state.photoData!!, state.isRevealed) }
            } else {
                item {
                    Spacer(Modifier.height(16.dp))
                    Text("No photos found in your gallery.")
                }
            }
        }

        if (state.hasPermission) {
            item { Spacer(Modifier.height(16.dp)) }
            item { PermissionWarning() }
        }
    }
}