package com.example.spycheck.ui.main.demos.common

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.theme.DangerRed

/**
 * Warning banner that appears at the top when demo has permission
 * Encourages users to revoke permission after demo
 */
@Composable
fun PermissionWarningBanner() {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = DangerRed.copy(alpha = 0.15f)
        ),
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
            Text(
                text = stringResource(R.string.revoke_permission_description),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DangerRed
                )
            ) {
                Text(stringResource(R.string.open_settings_button))
            }
        }
    }
}
