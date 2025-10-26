package com.example.spycheck.ui.main.demos.common

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.theme.DangerRed
import com.example.spycheck.ui.theme.SuccessGreen

data class DemoPermission(
    val name: String,
    val description: String,
    val isGranted: Boolean,
    val onRequest: () -> Unit,
    val settingsAction: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScaffold(
    title: String,
    onBack: () -> Unit,
    permissions: List<DemoPermission> = emptyList(),
    descriptionContent: @Composable () -> Unit,
    examplesContent: @Composable () -> Unit,
    demoContent: @Composable () -> Unit
) {
    val allGranted = permissions.isEmpty() || permissions.all { it.isGranted }
    val anyGranted = permissions.any { it.isGranted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (anyGranted) {
                MultiPermissionWarningBanner(permissions)
            }

            descriptionContent()
            examplesContent()

            if (permissions.isNotEmpty()) {
                MultiPermissionSection(permissions)
            }

            demoContent()
        }
    }
}

@Composable
private fun MultiPermissionWarningBanner(permissions: List<DemoPermission>) {
    val context = LocalContext.current
    val grantedPerms = permissions.filter { it.isGranted }

    if (grantedPerms.isEmpty()) return

    Card(
        colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "⚠️ " + stringResource(R.string.revoke_permission_title),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DangerRed
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic description based on granted permissions
            val permNames = grantedPerms.joinToString(", ") { it.name }
            Text(
                text = "This demo now has $permNames. For your privacy, revoke these permissions immediately after testing.",
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            grantedPerms.forEach { perm ->
                Button(
                    onClick = {
                        val intent = when (perm.settingsAction) {
                            "android.settings.NOTIFICATION_LISTENER_SETTINGS" -> {
                                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            }
                            "android.settings.USAGE_ACCESS_SETTINGS" -> {
                                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                            }
                            else -> {
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            }
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("🔒 Revoke ${perm.name}")
                }
            }
        }
    }
}

@Composable
private fun MultiPermissionSection(permissions: List<DemoPermission>) {
    permissions.forEach { perm ->
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (perm.isGranted)
                    SuccessGreen.copy(alpha = 0.15f)
                else
                    Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                if (!perm.isGranted) {
                    Text(
                        text = "🔒 ${perm.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = perm.description,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = perm.onRequest,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Grant ${perm.name}", fontSize = 15.sp)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("✅", fontSize = 24.sp, color = SuccessGreen)
                        Column {
                            Text(
                                text = "${perm.name} Granted",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SuccessGreen
                            )
                            Text(
                                text = perm.description,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}