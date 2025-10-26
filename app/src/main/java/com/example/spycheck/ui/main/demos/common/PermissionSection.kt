package com.example.spycheck.ui.main.demos.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.theme.SuccessGreen

/**
 * Permission request/status section
 * Shows permission button or granted status
 */
@Composable
fun PermissionSection(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    permissionDescription: String? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (hasPermission) {
                SuccessGreen.copy(alpha = 0.15f)
            } else {
                Color(0xFF2A2A2A)
            }
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (!hasPermission) {
                Text(
                    text = "🔐 " + stringResource(R.string.permission_required_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                permissionDescription?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.grant_permission_button),
                        fontSize = 15.sp
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "✓",
                        fontSize = 24.sp,
                        color = SuccessGreen
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.permission_granted),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SuccessGreen
                        )
                        Text(
                            text = stringResource(R.string.permission_granted_demo_ready),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
