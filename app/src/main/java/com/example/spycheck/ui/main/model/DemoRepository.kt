package com.example.spycheck.ui.main.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.spycheck.R

object DemoRepository {

    fun getSneakyStuffDemos(): List<Detail> {
        return listOf(
            Detail(
                id = "exif_gps",
                icon = Icons.Default.LocationOn,
                title = R.string.exif_gps_title,
                shortDescription = R.string.exif_gps_short,
                longDescription = R.string.exif_gps_long,
                realLifeExamples = listOf(
                    R.string.exif_example_1,
                    R.string.exif_example_2,
                    R.string.exif_example_3
                ),
                hasInteractiveDemo = true  // Enable interactive demo
            ),
            Detail(
                id = "wifi",
                title = R.string.demo_wifi_title,
                shortDescription = R.string.demo_wifi_short_desc,
                icon = Icons.Default.Wifi,
                longDescription = R.string.demo_wifi_long_desc,
                realLifeExamples = listOf(R.string.demo_wifi_example_1, R.string.demo_wifi_example_2)
            ),
            Detail(
                id = "clipboard",
                title = R.string.demo_clipboard_title,
                shortDescription = R.string.demo_clipboard_short_desc,
                icon = Icons.Default.ContentPaste,
                longDescription = R.string.demo_clipboard_long_desc,
                realLifeExamples = listOf(R.string.demo_clipboard_example_1, R.string.demo_clipboard_example_2)
            )
        )
    }

    fun getFingerprintDemos(): List<Detail> {
        return listOf(
            Detail(
                id = "device",
                title = R.string.demo_device_fp_title,
                shortDescription = R.string.demo_device_fp_short_desc,
                icon = Icons.Default.PhoneAndroid,
                longDescription = R.string.demo_device_fp_long_desc,
                realLifeExamples = listOf(R.string.demo_device_fp_example_1, R.string.demo_device_fp_example_2)
            ),
            Detail(
                id = "sensor",
                title = R.string.demo_sensor_fp_title,
                shortDescription = R.string.demo_sensor_fp_short_desc,
                icon = Icons.Default.Sensors,
                longDescription = R.string.demo_sensor_fp_long_desc,
                realLifeExamples = listOf(R.string.demo_sensor_fp_example_1, R.string.demo_sensor_fp_example_2)
            )
        )
    }
}