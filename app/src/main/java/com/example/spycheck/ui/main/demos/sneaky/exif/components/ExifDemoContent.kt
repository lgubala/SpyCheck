package com.example.spycheck.ui.main.demos.sneaky.exif.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.sneaky.exif.ExifDemoViewModel
import com.example.spycheck.ui.theme.DangerRed
import com.example.spycheck.ui.theme.SuccessGreen

/**
 * Demo content showing EXIF data extraction
 */
@Composable
fun ExifDemoContent(viewModel: ExifDemoViewModel) {
    val context = LocalContext.current  // ✅ Get context properly
    val photoData by viewModel.currentPhoto.collectAsState()
    val exifData by viewModel.exifData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Load Photo Button
        Button(
            onClick = { viewModel.loadRandomPhoto(context) },  // ✅ Pass context here
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isLoading) stringResource(R.string.loading_photo)
                else stringResource(R.string.load_random_photo)
            )
        }

        // Photo Preview
        photoData?.let { photo ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Photo
                    Image(
                        painter = rememberAsyncImagePainter(photo.uri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.photo_loaded_exif_data_below),
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // EXIF Data Display
        exifData?.let { data ->
            if (data.latitude != null && data.longitude != null) {
                // Has GPS Data - Show warning
                LocationFoundCard(data)
            } else {
                // No GPS Data - Show good news
                NoLocationCard()
            }

            // Other metadata that's always present
            MetadataCards(data)

            // What if GPS was enabled
            if (data.latitude == null) {
                WhatIfGpsEnabledCard()
            }
        }
    }
}

@Composable
private fun LocationFoundCard(data: com.example.spycheck.ui.main.demos.sneaky.exif.utils.PhotoExifData) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = DangerRed.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "⚠️", fontSize = 32.sp)
                Column {
                    Text(
                        text = stringResource(R.string.gps_location_found),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DangerRed
                    )
                    Text(
                        text = stringResource(R.string.this_photo_reveals_exact_location),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GPS Coordinates
            DataItem(
                icon = Icons.Default.LocationOn,
                label = stringResource(R.string.gps_coordinates),
                value = "${data.latitude?.let { "%.6f".format(it) } ?: ""}, ${data.longitude?.let { "%.6f".format(it) } ?: ""}",
                dangerous = true
            )

            data.address?.let { address ->
                Spacer(modifier = Modifier.height(8.dp))
                DataItem(
                    icon = Icons.Default.Home,
                    label = stringResource(R.string.approximate_address),
                    value = address,
                    dangerous = true
                )
            }

            data.altitude?.let { altitude ->
                Spacer(modifier = Modifier.height(8.dp))
                DataItem(
                    icon = Icons.Default.Terrain,
                    label = stringResource(R.string.altitude),
                    value = altitude
                )
            }
        }
    }
}

@Composable
private fun NoLocationCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SuccessGreen.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "✅", fontSize = 32.sp)
                Column {
                    Text(
                        text = stringResource(R.string.good_news_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SuccessGreen
                    )
                    Text(
                        text = stringResource(R.string.no_gps_description),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataCards(data: com.example.spycheck.ui.main.demos.sneaky.exif.utils.PhotoExifData) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFBE0B).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = if (data.latitude != null) {
                    stringResource(R.string.apps_can_also_see)
                } else {
                    stringResource(R.string.apps_can_still_see)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFFBE0B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            data.dateTime?.let { dateTime ->
                DataItem(
                    icon = Icons.Default.DateRange,
                    label = stringResource(R.string.date_time_label),
                    value = dateTime
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            data.cameraModel?.let { cameraModel ->
                DataItem(
                    icon = Icons.Default.CameraAlt,
                    label = stringResource(R.string.camera_model_label),
                    value = "${data.cameraMake ?: ""} $cameraModel".trim()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            data.softwareRaw?.let { software ->
                DataItem(
                    icon = Icons.Default.Apps,
                    label = stringResource(R.string.software_used_label),
                    value = software
                )
            }
        }
    }
}

@Composable
private fun WhatIfGpsEnabledCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.if_gps_enabled_title),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFFFF6B6B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.if_gps_enabled_description),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BulletPoint(stringResource(R.string.exact_gps_coordinates_accuracy))
                BulletPoint(stringResource(R.string.your_home_work_addresses))
                BulletPoint(stringResource(R.string.complete_location_history_photos))
                BulletPoint(stringResource(R.string.which_floor_building_altitude))
                BulletPoint(stringResource(R.string.your_speed_direction_moving))
                BulletPoint(stringResource(R.string.patterns_showing_regularly))
                BulletPoint(stringResource(R.string.sensitive_locations_hospitals))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = DangerRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "💡 " + stringResource(R.string.tip_keep_location_services_off),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DangerRed,
                    modifier = Modifier.padding(10.dp),
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun DataItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    dangerous: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (dangerous) DangerRed else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = if (dangerous) FontWeight.Bold else FontWeight.Normal,
                color = if (dangerous) DangerRed else Color.White
            )
        }
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "•",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}