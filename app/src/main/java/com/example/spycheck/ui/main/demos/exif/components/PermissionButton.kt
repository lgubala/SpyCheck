package com.example.spycheck.ui.main.demos.exif.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.spycheck.R
import com.example.spycheck.ui.theme.SuccessGreen

@Composable
fun PermissionButton(hasPermission: Boolean, onRequest: () -> Unit) {
    if (!hasPermission) {
        Button(
            onClick = onRequest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.grant_permission_button))
        }
    } else {
        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            enabled = false
        ) {
            Text(stringResource(R.string.permission_granted))
        }
    }
}