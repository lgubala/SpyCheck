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
                hasInteractiveDemo = true
            ),
            Detail(
                id = "wifi",
                title = R.string.demo_wifi_title,
                shortDescription = R.string.demo_wifi_short_desc,
                icon = Icons.Default.Wifi,
                longDescription = R.string.demo_wifi_long_desc,
                realLifeExamples = listOf(R.string.demo_wifi_example_1, R.string.demo_wifi_example_2),
                hasInteractiveDemo = true
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
                title = R.string.fp_device_title,
                shortDescription = R.string.fp_device_short_desc,
                icon = Icons.Default.PhoneAndroid,
                longDescription = R.string.fp_device_desc_long,
                realLifeExamples = listOf(
                    R.string.fp_device_example1_desc,
                    R.string.fp_device_example2_desc,
                    R.string.fp_device_example3_desc
                ),
                hasInteractiveDemo = true
            ),
            Detail(
                id = "sensor",
                title = R.string.fp_sensor_title,
                shortDescription = R.string.fp_sensor_short_desc,
                icon = Icons.Default.Sensors,
                longDescription = R.string.fp_sensor_desc_long,
                realLifeExamples = listOf(
                    R.string.fp_sensor_example1_desc,
                    R.string.fp_sensor_example2_desc,
                    R.string.fp_sensor_example3_desc
                ),
                hasInteractiveDemo = true
            ),
            Detail(
                id = "battery",
                title = R.string.fp_battery_title,
                shortDescription = R.string.fp_battery_short_desc,
                icon = Icons.Default.BatteryChargingFull,
                longDescription = R.string.fp_battery_desc_long,
                realLifeExamples = listOf(
                    R.string.fp_battery_example1_desc,
                    R.string.fp_battery_example2_desc,
                    R.string.fp_battery_example3_desc
                ),
                hasInteractiveDemo = true
            ),
            Detail(
                id = "audio",
                title = R.string.fp_audio_title,
                shortDescription = R.string.fp_audio_short_desc,
                icon = Icons.Default.Mic,
                longDescription = R.string.fp_audio_desc_long,
                realLifeExamples = listOf(
                    R.string.fp_audio_example1_desc,
                    R.string.fp_audio_example2_desc,
                    R.string.fp_audio_example3_desc
                ),
                hasInteractiveDemo = true
            ),
            Detail(
                id = "network",
                title = R.string.fp_network_title,
                shortDescription = R.string.fp_network_short_desc,
                icon = Icons.Default.Wifi,
                longDescription = R.string.fp_network_desc_long,
                realLifeExamples = listOf(
                    R.string.fp_network_example1_desc,
                    R.string.fp_network_example2_desc,
                    R.string.fp_network_example3_desc
                ),
                hasInteractiveDemo = true
            ),
            Detail(
                id = "performance",
                title = R.string.fp_performance_title,
                shortDescription = R.string.fp_performance_short_desc,
                icon = Icons.Default.Speed,
                longDescription = R.string.fp_performance_desc_long,
                realLifeExamples = listOf(
                    R.string.fp_performance_example1_desc,
                    R.string.fp_performance_example2_desc,
                    R.string.fp_performance_example3_desc
                ),
                hasInteractiveDemo = true
            ),
            Detail(
                id = "combined",
                title = R.string.fp_combined_title,
                shortDescription = R.string.fp_combined_short_desc,
                icon = Icons.Default.Fingerprint,
                longDescription = R.string.fp_combined_desc_long,
                realLifeExamples = listOf(
                    R.string.fp_combined_example1_desc,
                    R.string.fp_combined_example2_desc,
                    R.string.fp_combined_example3_desc
                ),
                hasInteractiveDemo = true
            )
        )
    }
}