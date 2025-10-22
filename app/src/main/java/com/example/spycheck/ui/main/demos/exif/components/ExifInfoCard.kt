package com.example.spycheck.ui.main.demos.exif.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.exif.utils.ExifDataExtractor
import com.example.spycheck.ui.main.demos.exif.utils.PhotoExifData
import com.example.spycheck.ui.main.demos.exif.utils.SoftwareCategory
import com.example.spycheck.ui.theme.DangerRed
import com.example.spycheck.ui.theme.SuccessGreen

@Composable
fun ExifInfoCard(photoData: PhotoExifData, isPhotoRevealed: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (photoData.latitude != null && photoData.longitude != null) {
            // Has GPS data
            Text(
                text = stringResource(R.string.apps_can_see),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ExifDataSection(photoData = photoData, isPhotoRevealed = isPhotoRevealed)

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DangerRed.copy(alpha = 0.1f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(R.string.privacy_impact_title),
                        fontWeight = FontWeight.Bold,
                        color = DangerRed,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.privacy_impact_description),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        } else {
            // No GPS data
            NoGpsDataFallback(photoData = photoData, isPhotoRevealed = isPhotoRevealed)
        }
    }
}

@Composable
fun ExifDataSection(photoData: PhotoExifData, isPhotoRevealed: Boolean) {
    val exifExtractor = ExifDataExtractor(LocalContext.current)
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // LOCATION DATA (most shocking)
        photoData.dateTime?.let {
            ExifDataItem(
                icon = "📅",
                label = stringResource(R.string.exif_date_time),
                value = it,
                revealed = isPhotoRevealed,
                explanation = stringResource(R.string.explain_date_time)
            )
        }

        if (photoData.latitude != null && photoData.longitude != null) {
            val coordinates = exifExtractor.formatCoordinates(
                photoData.latitude,
                photoData.longitude
            )

            ExifDataItem(
                icon = "🌍",
                label = stringResource(R.string.exif_gps_coords),
                value = coordinates,
                revealed = isPhotoRevealed,
                highlighted = true,
                explanation = stringResource(R.string.explain_gps_coords)
            )

            photoData.address?.let {
                ExifDataItem(
                    icon = "📍",
                    label = stringResource(R.string.exif_address),
                    value = it,
                    revealed = isPhotoRevealed,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_address)
                )
            }

            photoData.altitude?.let {
                ExifDataItem(
                    icon = "⛰️",
                    label = stringResource(R.string.exif_altitude),
                    value = it,
                    revealed = isPhotoRevealed,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_altitude)
                )
            }

            photoData.gpsTimestamp?.let {
                ExifDataItem(
                    icon = "⏰",
                    label = stringResource(R.string.exif_gps_timestamp),
                    value = it,
                    revealed = isPhotoRevealed,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_gps_timestamp)
                )
            }

            photoData.gpsSpeed?.let {
                ExifDataItem(
                    icon = "🏃",
                    label = stringResource(R.string.exif_speed),
                    value = it,
                    revealed = isPhotoRevealed,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_speed)
                )
            }

            photoData.gpsImgDirection?.let {
                ExifDataItem(
                    icon = "🧭",
                    label = stringResource(R.string.exif_direction),
                    value = it,
                    revealed = isPhotoRevealed,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_direction)
                )
            }

            photoData.locationAccuracy?.let {
                ExifDataItem(
                    icon = "🎯",
                    label = stringResource(R.string.exif_accuracy),
                    value = it,
                    revealed = isPhotoRevealed
                )
            }
        }

        // DEVICE INFO (shocking - unique identifiers!)
        if (photoData.deviceSerial != null || photoData.uniqueImageId != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.section_device_identifiers),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DangerRed
            )
        }

        photoData.deviceSerial?.let {
            ExifDataItem(
                icon = "🆔",
                label = stringResource(R.string.exif_device_serial),
                value = it,
                revealed = isPhotoRevealed,
                highlighted = true,
                explanation = stringResource(R.string.explain_device_serial)
            )
        }

        photoData.uniqueImageId?.let {
            ExifDataItem(
                icon = "🔢",
                label = stringResource(R.string.exif_image_id),
                value = it,
                revealed = isPhotoRevealed,
                highlighted = true,
                explanation = stringResource(R.string.explain_image_id)
            )
        }

        // CAMERA & LENS INFO
        if (photoData.cameraMake != null || photoData.cameraModel != null ||
            photoData.lensMake != null || photoData.lensModel != null || photoData.softwareInfo != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.section_device_software),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (photoData.cameraMake != null || photoData.cameraModel != null) {
            val cameraInfo = listOfNotNull(
                photoData.cameraMake,
                photoData.cameraModel
            ).joinToString(" ")

            ExifDataItem(
                icon = "📷",
                label = stringResource(R.string.exif_camera),
                value = cameraInfo,
                revealed = isPhotoRevealed,
                explanation = stringResource(R.string.explain_camera)
            )
        }

        if (photoData.lensMake != null || photoData.lensModel != null) {
            val lensInfo = listOfNotNull(
                photoData.lensMake,
                photoData.lensModel
            ).joinToString(" ")

            ExifDataItem(
                icon = "🔭",
                label = stringResource(R.string.exif_lens),
                value = lensInfo,
                revealed = isPhotoRevealed
            )
        }

        // SOFTWARE - Using string resources with format arguments
        photoData.softwareInfo?.let { info ->
            val displayText = if (info.formatArgs.isEmpty()) {
                context.getString(info.displayTextResId)
            } else {
                context.getString(info.displayTextResId, *info.formatArgs)
            }

            val explanation = if (info.formatArgs.isEmpty()) {
                context.getString(info.explanationResId)
            } else {
                context.getString(info.explanationResId, *info.formatArgs)
            }

            ExifDataItem(
                icon = when (info.category) {
                    SoftwareCategory.STOCK_CAMERA -> "📱"
                    SoftwareCategory.SOCIAL_MEDIA -> "📲"
                    SoftwareCategory.PHOTO_EDITOR -> "🎨"
                    SoftwareCategory.THIRD_PARTY_CAMERA -> "📸"
                    SoftwareCategory.OS_INFO -> "⚙️"
                    SoftwareCategory.UNKNOWN -> "❓"
                },
                label = stringResource(R.string.exif_software),
                value = displayText,
                revealed = isPhotoRevealed,
                explanation = explanation
            )
        }

        // CAMERA SETTINGS (reveals photography habits)
        if (photoData.focalLength != null || photoData.aperture != null ||
            photoData.iso != null || photoData.exposureTime != null ||
            photoData.flash != null || photoData.digitalZoom != null) {

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.section_camera_settings),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        photoData.focalLength?.let {
            ExifDataItem(
                icon = "🔍",
                label = stringResource(R.string.exif_focal_length),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.aperture?.let {
            ExifDataItem(
                icon = "⭕",
                label = stringResource(R.string.exif_aperture),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.iso?.let {
            ExifDataItem(
                icon = "🌓",
                label = stringResource(R.string.exif_iso),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.exposureTime?.let {
            ExifDataItem(
                icon = "⏱️",
                label = stringResource(R.string.exif_exposure),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.flash?.let {
            ExifDataItem(
                icon = "⚡",
                label = stringResource(R.string.exif_flash),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.digitalZoom?.let {
            ExifDataItem(
                icon = "🔎",
                label = stringResource(R.string.exif_zoom),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.orientation?.let {
            ExifDataItem(
                icon = "🔄",
                label = stringResource(R.string.exif_orientation),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.artistName?.let {
            ExifDataItem(
                icon = "👤",
                label = stringResource(R.string.exif_artist),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.copyright?.let {
            ExifDataItem(
                icon = "©️",
                label = stringResource(R.string.exif_copyright),
                value = it,
                revealed = isPhotoRevealed
            )
        }

        photoData.userComment?.let {
            ExifDataItem(
                icon = "💬",
                label = stringResource(R.string.exif_user_comment),
                value = it,
                revealed = isPhotoRevealed
            )
        }
    }
}

@Composable
fun ExifDataItem(
    icon: String,
    label: String,
    value: String,
    revealed: Boolean,
    highlighted: Boolean = false,
    explanation: String? = null
) {
    var isValueRevealed by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }

    Surface(
        color = if (highlighted) {
            DangerRed.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable(enabled = revealed && !isValueRevealed) {
                        isValueRevealed = true
                    }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 24.sp)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (highlighted) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    AnimatedVisibility(visible = revealed) {
                        Text(
                            text = if (isValueRevealed || !highlighted) value else censorValue(value),
                            fontSize = 14.sp,
                            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
                            color = if (highlighted) DangerRed else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (revealed && !isValueRevealed && highlighted) {
                        Text(
                            text = stringResource(R.string.tap_to_reveal),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (explanation != null && revealed) {
                    Text(
                        text = "ℹ️",
                        fontSize = 20.sp,
                        modifier = Modifier.clickable { showExplanation = !showExplanation }
                    )
                }
            }

            if (explanation != null && showExplanation && revealed) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = explanation,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun NoGpsDataFallback(photoData: PhotoExifData, isPhotoRevealed: Boolean) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = SuccessGreen.copy(alpha = 0.1f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.good_news_title),
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.no_gps_description),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        if (photoData.dateTime != null || photoData.cameraMake != null ||
            photoData.softwareInfo != null || photoData.deviceSerial != null) {

            Text(
                text = stringResource(R.string.apps_can_still_see),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            photoData.dateTime?.let {
                ExifDataItem(
                    icon = "📅",
                    label = stringResource(R.string.exif_date_time),
                    value = it,
                    revealed = isPhotoRevealed,
                    highlighted = false,
                    explanation = stringResource(R.string.explain_date_time)
                )
            }

            if (photoData.cameraMake != null || photoData.cameraModel != null) {
                val cameraInfo = listOfNotNull(
                    photoData.cameraMake,
                    photoData.cameraModel
                ).joinToString(" ")

                ExifDataItem(
                    icon = "📷",
                    label = stringResource(R.string.exif_camera),
                    value = cameraInfo,
                    revealed = isPhotoRevealed,
                    highlighted = false,
                    explanation = stringResource(R.string.explain_camera)
                )
            }

            photoData.softwareInfo?.let { info ->
                val displayText = if (info.formatArgs.isEmpty()) {
                    context.getString(info.displayTextResId)
                } else {
                    context.getString(info.displayTextResId, *info.formatArgs)
                }

                val explanation = if (info.formatArgs.isEmpty()) {
                    context.getString(info.explanationResId)
                } else {
                    context.getString(info.explanationResId, *info.formatArgs)
                }

                ExifDataItem(
                    icon = when (info.category) {
                        SoftwareCategory.STOCK_CAMERA -> "📱"
                        SoftwareCategory.SOCIAL_MEDIA -> "📲"
                        SoftwareCategory.PHOTO_EDITOR -> "🎨"
                        SoftwareCategory.THIRD_PARTY_CAMERA -> "📸"
                        SoftwareCategory.OS_INFO -> "⚙️"
                        SoftwareCategory.UNKNOWN -> "❓"
                    },
                    label = stringResource(R.string.exif_software),
                    value = displayText,
                    revealed = isPhotoRevealed,
                    highlighted = false,
                    explanation = explanation
                )
            }

            photoData.deviceSerial?.let {
                ExifDataItem(
                    icon = "🆔",
                    label = stringResource(R.string.exif_device_serial),
                    value = it,
                    revealed = isPhotoRevealed,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_device_serial)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = DangerRed.copy(alpha = 0.1f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.if_gps_enabled_title),
                    fontWeight = FontWeight.Bold,
                    color = DangerRed,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.if_gps_enabled_description),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BulletPoint(stringResource(R.string.bullet_gps_coords))
                    BulletPoint(stringResource(R.string.bullet_addresses))
                    BulletPoint(stringResource(R.string.bullet_history))
                    BulletPoint(stringResource(R.string.bullet_altitude))
                    BulletPoint(stringResource(R.string.bullet_speed_direction))
                    BulletPoint(stringResource(R.string.bullet_patterns))
                    BulletPoint(stringResource(R.string.bullet_sensitive))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.tip_disable_gps),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "•", fontSize = 12.sp, color = DangerRed)
        Text(text = text, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

fun censorValue(value: String): String {
    return value.take(value.length / 3) + "◾".repeat(value.length - (value.length / 3))
}