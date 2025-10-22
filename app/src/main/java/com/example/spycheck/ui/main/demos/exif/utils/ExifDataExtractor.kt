package com.example.spycheck.ui.main.demos.exif.utils

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.spycheck.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

data class PhotoExifData(
    val uri: Uri,
    val dateTime: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val cameraMake: String? = null,
    val cameraModel: String? = null,
    val locationAccuracy: String? = null,
    val altitude: String? = null,
    val lensMake: String? = null,
    val lensModel: String? = null,
    val focalLength: String? = null,
    val aperture: String? = null,
    val iso: String? = null,
    val exposureTime: String? = null,
    val flash: String? = null,
    val orientation: String? = null,
    val softwareRaw: String? = null,
    val softwareInfo: SoftwareInfo? = null,
    val deviceSerial: String? = null,
    val uniqueImageId: String? = null,
    val xResolution: String? = null,
    val yResolution: String? = null,
    val gpsTimestamp: String? = null,
    val gpsSpeed: String? = null,
    val gpsSpeedRef: String? = null,
    val gpsImgDirection: String? = null,
    val digitalZoom: String? = null,
    val sceneType: String? = null,
    val whiteBalance: String? = null,
    val subjectDistance: String? = null,
    val artistName: String? = null,
    val copyright: String? = null,
    val userComment: String? = null
)

data class SoftwareInfo(
    val displayTextResId: Int,
    val explanationResId: Int,
    val formatArgs: Array<String> = emptyArray(),
    val category: SoftwareCategory
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SoftwareInfo
        if (displayTextResId != other.displayTextResId) return false
        if (explanationResId != other.explanationResId) return false
        if (!formatArgs.contentEquals(other.formatArgs)) return false
        if (category != other.category) return false
        return true
    }

    override fun hashCode(): Int {
        var result = displayTextResId
        result = 31 * result + explanationResId
        result = 31 * result + formatArgs.contentHashCode()
        result = 31 * result + category.hashCode()
        return result
    }
}

enum class SoftwareCategory {
    STOCK_CAMERA,
    SOCIAL_MEDIA,
    PHOTO_EDITOR,
    THIRD_PARTY_CAMERA,
    OS_INFO,
    UNKNOWN
}

class ExifDataExtractor(private val context: Context) {

    suspend fun extractExifData(uri: Uri): PhotoExifData = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = inputStream?.let { ExifInterface(it) }

            // Basic info
            val dateTime = exif?.getAttribute(ExifInterface.TAG_DATETIME)
            val make = exif?.getAttribute(ExifInterface.TAG_MAKE)
            val model = exif?.getAttribute(ExifInterface.TAG_MODEL)

            // GPS data
            val latLong = exif?.latLong
            val latitude = latLong?.get(0)
            val longitude = latLong?.get(1)

            val address = if (latitude != null && longitude != null) {
                getAddressFromCoordinates(latitude, longitude)
            } else null

            val accuracy = if (latitude != null && longitude != null) {
                "±5-50m (typical GPS accuracy)"
            } else null

            // Altitude
            val altitude = exif?.getAttribute(ExifInterface.TAG_GPS_ALTITUDE)?.let { alt ->
                val altRef = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF)
                val altValue = alt.toDoubleOrNull() ?: 0.0
                val sign = if (altRef == "1") "-" else ""
                "${sign}${String.format("%.1f", altValue)}m above sea level"
            }

            // GPS timestamp
            val gpsTimestamp = exif?.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)?.let { date ->
                val time = exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP)
                if (time != null) "$date $time UTC" else date
            }

            // GPS speed
            val gpsSpeed = exif?.getAttribute(ExifInterface.TAG_GPS_SPEED)?.let { speed ->
                val speedRef = exif.getAttribute(ExifInterface.TAG_GPS_SPEED_REF) ?: "K"
                val unit = when (speedRef) {
                    "K" -> "km/h"
                    "M" -> "mph"
                    "N" -> "knots"
                    else -> "km/h"
                }
                "${speed} ${unit}"
            }

            // Direction
            val gpsImgDirection = exif?.getAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION)?.let { dir ->
                val degrees = dir.toDoubleOrNull() ?: 0.0
                val direction = getCardinalDirection(degrees)
                "${String.format("%.1f", degrees)}° ($direction)"
            }

            // Lens info
            val lensMake = exif?.getAttribute(ExifInterface.TAG_LENS_MAKE)
            val lensModel = exif?.getAttribute(ExifInterface.TAG_LENS_MODEL)

            // Camera settings
            val focalLength = exif?.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)?.let {
                "${it}mm"
            }
            val aperture = exif?.getAttribute(ExifInterface.TAG_F_NUMBER)?.let {
                "f/${it}"
            }
            val iso = exif?.getAttribute(ExifInterface.TAG_ISO_SPEED)
            val exposureTime = exif?.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)?.let {
                "${it}s"
            }

            // Flash
            val flash = exif?.getAttributeInt(ExifInterface.TAG_FLASH, -1)?.let { flashValue ->
                when {
                    flashValue == 0 -> "Flash did not fire"
                    flashValue and 0x1 != 0 -> "Flash fired"
                    else -> "Unknown"
                }
            }

            // Orientation
            val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)?.let { orient ->
                when (orient) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> "Rotated 90° (Portrait)"
                    ExifInterface.ORIENTATION_ROTATE_180 -> "Rotated 180° (Upside down)"
                    ExifInterface.ORIENTATION_ROTATE_270 -> "Rotated 270° (Portrait inverted)"
                    else -> "Normal (Landscape)"
                }
            }

            // SOFTWARE - Parse it intelligently
            val softwareRaw = exif?.getAttribute(ExifInterface.TAG_SOFTWARE)
            val softwareInfo = softwareRaw?.let { parseSoftwareInfo(it, make, model) }

            // Device identifiers
            val deviceSerial = exif?.getAttribute(ExifInterface.TAG_BODY_SERIAL_NUMBER)
            val uniqueImageId = exif?.getAttribute(ExifInterface.TAG_IMAGE_UNIQUE_ID)

            // Resolution
            val xRes = exif?.getAttribute(ExifInterface.TAG_X_RESOLUTION)
            val yRes = exif?.getAttribute(ExifInterface.TAG_Y_RESOLUTION)

            // Digital zoom
            val digitalZoom = exif?.getAttribute(ExifInterface.TAG_DIGITAL_ZOOM_RATIO)?.let {
                if (it.toDoubleOrNull() != null && it.toDouble() > 1.0) {
                    "${it}x digital zoom"
                } else null
            }

            // Scene type
            val sceneType = exif?.getAttributeInt(ExifInterface.TAG_SCENE_TYPE, -1)?.let {
                when (it) {
                    1 -> "Directly photographed"
                    else -> null
                }
            }

            // White balance
            val whiteBalance = exif?.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, -1)?.let {
                when (it) {
                    0 -> "Auto white balance"
                    1 -> "Manual white balance"
                    else -> null
                }
            }

            // Subject distance
            val subjectDistance = exif?.getAttribute(ExifInterface.TAG_SUBJECT_DISTANCE)?.let {
                "${it}m from subject"
            }

            // Artist/copyright
            val artistName = exif?.getAttribute(ExifInterface.TAG_ARTIST)
            val copyright = exif?.getAttribute(ExifInterface.TAG_COPYRIGHT)
            val userComment = exif?.getAttribute(ExifInterface.TAG_USER_COMMENT)

            inputStream?.close()

            PhotoExifData(
                uri = uri,
                dateTime = dateTime,
                latitude = latitude,
                longitude = longitude,
                address = address,
                cameraMake = make,
                cameraModel = model,
                locationAccuracy = accuracy,
                altitude = altitude,
                lensMake = lensMake,
                lensModel = lensModel,
                focalLength = focalLength,
                aperture = aperture,
                iso = iso,
                exposureTime = exposureTime,
                flash = flash,
                orientation = orientation,
                softwareRaw = softwareRaw,
                softwareInfo = softwareInfo,
                deviceSerial = deviceSerial,
                uniqueImageId = uniqueImageId,
                xResolution = xRes,
                yResolution = yRes,
                gpsTimestamp = gpsTimestamp,
                gpsSpeed = gpsSpeed,
                gpsImgDirection = gpsImgDirection,
                digitalZoom = digitalZoom,
                sceneType = sceneType,
                whiteBalance = whiteBalance,
                subjectDistance = subjectDistance,
                artistName = artistName,
                copyright = copyright,
                userComment = userComment
            )
        } catch (e: IOException) {
            PhotoExifData(uri = uri)
        }
    }

    private fun parseSoftwareInfo(software: String, make: String?, model: String?): SoftwareInfo {
        val lower = software.lowercase()

        return when {
            // Social media apps
            lower.contains("instagram") -> SoftwareInfo(
                displayTextResId = R.string.software_name_instagram,
                explanationResId = R.string.software_instagram,
                category = SoftwareCategory.SOCIAL_MEDIA
            )
            lower.contains("snapchat") -> SoftwareInfo(
                displayTextResId = R.string.software_name_snapchat,
                explanationResId = R.string.software_snapchat,
                category = SoftwareCategory.SOCIAL_MEDIA
            )
            lower.contains("facebook") || lower.contains("fb") -> SoftwareInfo(
                displayTextResId = R.string.software_name_facebook,
                explanationResId = R.string.software_facebook,
                category = SoftwareCategory.SOCIAL_MEDIA
            )
            lower.contains("whatsapp") -> SoftwareInfo(
                displayTextResId = R.string.software_name_whatsapp,
                explanationResId = R.string.software_whatsapp,
                category = SoftwareCategory.SOCIAL_MEDIA
            )

            // Photo editors
            lower.contains("photoshop") || lower.contains("adobe") -> SoftwareInfo(
                displayTextResId = R.string.software_name_photoshop,
                explanationResId = R.string.software_photoshop,
                category = SoftwareCategory.PHOTO_EDITOR
            )
            lower.contains("lightroom") -> SoftwareInfo(
                displayTextResId = R.string.software_name_lightroom,
                explanationResId = R.string.software_lightroom,
                category = SoftwareCategory.PHOTO_EDITOR
            )
            lower.contains("snapseed") -> SoftwareInfo(
                displayTextResId = R.string.software_name_snapseed,
                explanationResId = R.string.software_snapseed,
                category = SoftwareCategory.PHOTO_EDITOR
            )
            lower.contains("vsco") -> SoftwareInfo(
                displayTextResId = R.string.software_name_vsco,
                explanationResId = R.string.software_vsco,
                category = SoftwareCategory.PHOTO_EDITOR
            )

            // Third-party cameras
            lower.contains("gcam") || lower.contains("google camera") -> SoftwareInfo(
                displayTextResId = R.string.software_name_gcam,
                explanationResId = R.string.software_gcam,
                category = SoftwareCategory.THIRD_PARTY_CAMERA
            )
            lower.contains("open camera") -> SoftwareInfo(
                displayTextResId = R.string.software_name_open_camera,
                explanationResId = R.string.software_open_camera,
                category = SoftwareCategory.THIRD_PARTY_CAMERA
            )

            // OS/Firmware indicators - Samsung/Android
            lower.contains("n770f") || lower.contains("sm-") || software.matches(Regex(".*[A-Z]\\d{3}[A-Z]{4}\\d.*")) -> {
                val deviceInfo = when {
                    make?.lowercase()?.contains("samsung") == true -> "Samsung ${model ?: "device"}"
                    else -> model ?: "Android device"
                }
                SoftwareInfo(
                    displayTextResId = R.string.software_category_stock,
                    explanationResId = R.string.software_stock_generic,
                    formatArgs = arrayOf(software, deviceInfo),
                    category = SoftwareCategory.STOCK_CAMERA
                )
            }

            // Generic Android
            lower.contains("android") -> SoftwareInfo(
                displayTextResId = R.string.software_name_android_stock,
                explanationResId = R.string.software_stock_android,
                formatArgs = arrayOf(software),
                category = SoftwareCategory.STOCK_CAMERA
            )

            // iOS
            lower.contains("ios") || lower.matches(Regex(".*\\d+\\.\\d+.*")) -> SoftwareInfo(
                displayTextResId = R.string.software_name_iphone,
                explanationResId = R.string.software_stock_ios,
                formatArgs = arrayOf(software),
                category = SoftwareCategory.STOCK_CAMERA
            )

            // Windows/Other desktop
            lower.contains("windows") || lower.contains("paint") -> SoftwareInfo(
                displayTextResId = R.string.software_name_windows,
                explanationResId = R.string.software_windows,
                formatArgs = arrayOf(software),
                category = SoftwareCategory.PHOTO_EDITOR
            )

            // Unknown but has something
            software.isNotBlank() -> SoftwareInfo(
                displayTextResId = R.string.software_category_firmware,
                explanationResId = R.string.software_firmware_info,
                formatArgs = arrayOf(software),
                category = SoftwareCategory.OS_INFO
            )

            // Truly unknown
            else -> SoftwareInfo(
                displayTextResId = R.string.software_category_unknown,
                explanationResId = R.string.software_unknown_info,
                category = SoftwareCategory.UNKNOWN
            )
        }
    }

    private suspend fun getAddressFromCoordinates(lat: Double, lon: Double): String? =
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                addresses?.firstOrNull()?.getAddressLine(0)
            } catch (e: Exception) {
                null
            }
        }

    private fun getCardinalDirection(degrees: Double): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((degrees + 22.5) / 45.0).toInt() % 8
        return directions[index]
    }

    fun formatCoordinates(lat: Double, lon: Double): String {
        return "%.6f°, %.6f°".format(lat, lon)
    }
}