package com.example.spycheck.ui.main.demos.exif.components

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
import androidx.compose.ui.unit.sp
import com.example.spycheck.R
import com.example.spycheck.ui.main.demos.exif.utils.ExifDataExtractor
import com.example.spycheck.ui.main.demos.exif.utils.PhotoExifData
import com.example.spycheck.ui.main.demos.exif.utils.SoftwareCategory
import com.example.spycheck.ui.theme.DangerRed
import com.example.spycheck.ui.theme.SuccessGreen

@Composable
fun ExifInfoCard(photoData: PhotoExifData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (photoData.latitude != null && photoData.longitude != null) {
            Text(
                text = stringResource(R.string.apps_can_see),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ExifDataSection(photoData = photoData)

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
            NoGpsDataFallback(photoData = photoData)
        }
    }
}

@Composable
fun ExifDataSection(photoData: PhotoExifData) {
    val exifExtractor = ExifDataExtractor(LocalContext.current)
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // LOCATION DATA
        photoData.dateTime?.let {
            ExifDataItem(
                icon = "📅",
                label = stringResource(R.string.exif_date_time),
                value = it,
                explanation = stringResource(R.string.explain_date_time)
            )
        }

        if (photoData.latitude != null && photoData.longitude != null) {
            val coordinates = exifExtractor.formatCoordinates(photoData.latitude, photoData.longitude)

            ExifDataItem(
                icon = "🌍",
                label = stringResource(R.string.exif_gps_coords),
                value = coordinates,
                highlighted = true,
                explanation = stringResource(R.string.explain_gps_coords)
            )

            photoData.address?.let {
                ExifDataItem(
                    icon = "📍",
                    label = stringResource(R.string.exif_address),
                    value = it,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_address)
                )
            }

            photoData.altitude?.let {
                ExifDataItem(
                    icon = "⛰️",
                    label = stringResource(R.string.exif_altitude),
                    value = it,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_altitude)
                )
            }

            photoData.gpsTimestamp?.let {
                ExifDataItem(
                    icon = "⏰",
                    label = stringResource(R.string.exif_gps_timestamp),
                    value = it,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_gps_timestamp)
                )
            }

            photoData.gpsSpeed?.let {
                ExifDataItem(
                    icon = "🏃",
                    label = stringResource(R.string.exif_speed),
                    value = it,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_speed)
                )
            }

            photoData.gpsImgDirection?.let {
                ExifDataItem(
                    icon = "🧭",
                    label = stringResource(R.string.exif_direction),
                    value = it,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_direction)
                )
            }

            photoData.locationAccuracy?.let {
                ExifDataItem(
                    icon = "🎯",
                    label = stringResource(R.string.exif_accuracy),
                    value = it
                )
            }
        }

        // DEVICE INFO
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
                highlighted = true,
                explanation = stringResource(R.string.explain_device_serial)
            )
        }

        photoData.uniqueImageId?.let {
            ExifDataItem(
                icon = "🔢",
                label = stringResource(R.string.exif_image_id),
                value = it,
                highlighted = true,
                explanation = stringResource(R.string.explain_image_id)
            )
        }

        // CAMERA & SOFTWARE
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
            val cameraInfo = listOfNotNull(photoData.cameraMake, photoData.cameraModel).joinToString(" ")
            ExifDataItem(
                icon = "📷",
                label = stringResource(R.string.exif_camera),
                value = cameraInfo,
                explanation = stringResource(R.string.explain_camera)
            )
        }

        if (photoData.lensMake != null || photoData.lensModel != null) {
            val lensInfo = listOfNotNull(photoData.lensMake, photoData.lensModel).joinToString(" ")
            ExifDataItem(
                icon = "🔭",
                label = stringResource(R.string.exif_lens),
                value = lensInfo
            )
        }

        photoData.softwareInfo?.let { info ->
            val displayText = if (info.formatArgs.isEmpty())
                context.getString(info.displayTextResId)
            else
                context.getString(info.displayTextResId, *info.formatArgs)

            val explanation = if (info.formatArgs.isEmpty())
                context.getString(info.explanationResId)
            else
                context.getString(info.explanationResId, *info.formatArgs)

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
                explanation = explanation
            )
        }

        // CAMERA SETTINGS
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
            ExifDataItem(icon = "🔍", label = stringResource(R.string.exif_focal_length), value = it)
        }
        photoData.aperture?.let {
            ExifDataItem(icon = "⭕", label = stringResource(R.string.exif_aperture), value = it)
        }
        photoData.iso?.let {
            ExifDataItem(icon = "🌓", label = stringResource(R.string.exif_iso), value = it)
        }
        photoData.exposureTime?.let {
            ExifDataItem(icon = "⏱️", label = stringResource(R.string.exif_exposure), value = it)
        }
        photoData.flash?.let {
            ExifDataItem(icon = "⚡", label = stringResource(R.string.exif_flash), value = it)
        }
        photoData.digitalZoom?.let {
            ExifDataItem(icon = "🔎", label = stringResource(R.string.exif_zoom), value = it)
        }
        photoData.orientation?.let {
            ExifDataItem(icon = "🔄", label = stringResource(R.string.exif_orientation), value = it)
        }
        photoData.artistName?.let {
            ExifDataItem(icon = "👤", label = stringResource(R.string.exif_artist), value = it)
        }
        photoData.copyright?.let {
            ExifDataItem(icon = "©️", label = stringResource(R.string.exif_copyright), value = it)
        }
        photoData.userComment?.let {
            ExifDataItem(icon = "💬", label = stringResource(R.string.exif_user_comment), value = it)
        }
    }
}

@Composable
fun ExifDataItem(
    icon: String,
    label: String,
    value: String,
    highlighted: Boolean = false,
    explanation: String? = null
) {
    var showExplanation by remember { mutableStateOf(false) }

    Surface(
        color = if (highlighted)
            DangerRed.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable(enabled = explanation != null) { showExplanation = !showExplanation }
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
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
                        color = if (highlighted) DangerRed else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (explanation != null) {
                    Text(
                        text = "ℹ️",
                        fontSize = 20.sp
                    )
                }
            }

            if (explanation != null && showExplanation) {
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
fun NoGpsDataFallback(photoData: PhotoExifData) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
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
                    explanation = stringResource(R.string.explain_date_time)
                )
            }

            if (photoData.cameraMake != null || photoData.cameraModel != null) {
                val cameraInfo = listOfNotNull(photoData.cameraMake, photoData.cameraModel).joinToString(" ")
                ExifDataItem(
                    icon = "📷",
                    label = stringResource(R.string.exif_camera),
                    value = cameraInfo,
                    explanation = stringResource(R.string.explain_camera)
                )
            }

            photoData.softwareInfo?.let { info ->
                val displayText = if (info.formatArgs.isEmpty())
                    context.getString(info.displayTextResId)
                else
                    context.getString(info.displayTextResId, *info.formatArgs)

                val explanation = if (info.formatArgs.isEmpty())
                    context.getString(info.explanationResId)
                else
                    context.getString(info.explanationResId, *info.formatArgs)

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
                    explanation = explanation
                )
            }

            photoData.deviceSerial?.let {
                ExifDataItem(
                    icon = "🆔",
                    label = stringResource(R.string.exif_device_serial),
                    value = it,
                    highlighted = true,
                    explanation = stringResource(R.string.explain_device_serial)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f)),
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
