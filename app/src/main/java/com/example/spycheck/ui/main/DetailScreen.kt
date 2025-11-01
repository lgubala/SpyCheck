package com.example.spycheck.ui.main

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spycheck.R
import com.example.spycheck.ui.main.model.Detail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detail: Detail,
    onBack: () -> Unit,
    onStartDemo: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = detail.title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Common info text
            Text(
                text = stringResource(id = detail.longDescription),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.real_life_examples),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            detail.realLifeExamples.forEach {
                Text(
                    text = stringResource(id = R.string.detail_bullet_point, stringResource(id = it)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (detail.id) {


                else -> {
                    // For demos not yet migrated, show a placeholder
                    Text(
                        text = stringResource(id = R.string.detail_migration_placeholder),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (detail.hasInteractiveDemo && onStartDemo != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onStartDemo,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(id = R.string.detail_try_interactive_demo))
                        }
                    }
                }
            }
        }
    }
}