package com.example.spycheck.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.spycheck.R

// Helper function to get color from resources
@Composable
private fun colorRes(@androidx.annotation.ColorRes id: Int): Color {
    val context = LocalContext.current
    return Color(ContextCompat.getColor(context, id))
}

// Extension function for non-Composable contexts
fun Context.colorResCompat(@androidx.annotation.ColorRes id: Int): Color =
    Color(ContextCompat.getColor(this, id))

// =============================================================================
// AppColors object - ALL colors load from colors.xml
// =============================================================================
object AppColors {
    // Core Brand Colors
    val Crimson: Color @Composable get() = colorRes(R.color.primary)
    val CrimsonDark: Color @Composable get() = colorRes(R.color.primary_dark)
    val IcyBlue: Color @Composable get() = colorRes(R.color.accent)
    val Amber: Color @Composable get() = colorRes(R.color.warning)
    val LightGreen: Color @Composable get() = colorRes(R.color.success)

    // Backgrounds & Surfaces
    val BackgroundDark: Color @Composable get() = colorRes(R.color.background_dark)
    val SurfaceDark: Color @Composable get() = colorRes(R.color.surface_dark)
    val CardBackground: Color @Composable get() = colorRes(R.color.card_background)
    val BackgroundLight: Color @Composable get() = colorRes(R.color.background_light)
    val SurfaceLight: Color @Composable get() = colorRes(R.color.surface_light)

    // Text
    val TextPrimary: Color @Composable get() = colorRes(R.color.text_primary)
    val TextSecondary: Color @Composable get() = colorRes(R.color.text_secondary)
    val TextPrimaryLight: Color @Composable get() = colorRes(R.color.text_primary_light)
    val TextSecondaryLight: Color @Composable get() = colorRes(R.color.text_secondary_light)

    // Alerts / Status
    val DangerRed: Color @Composable get() = colorRes(R.color.danger_red)
    val SuccessGreen: Color @Composable get() = colorRes(R.color.success_green)
    val InfoBlue: Color @Composable get() = colorRes(R.color.info_blue)
    val WarningYellow: Color @Composable get() = colorRes(R.color.warning_yellow)

    // Utility colors
    val White: Color @Composable get() = colorRes(R.color.white)
    val Black: Color @Composable get() = colorRes(R.color.black)
    val Transparent: Color @Composable get() = colorRes(R.color.transparent)

    // Audio demo colors
    val AudioAnalyzingBg: Color @Composable get() = colorRes(R.color.audio_analyzing_bg)
    val AudioAnalyzingColor: Color @Composable get() = colorRes(R.color.audio_analyzing_color)
    val AudioProgressTrack: Color @Composable get() = colorRes(R.color.audio_progress_track)
    val AudioResultBg: Color @Composable get() = colorRes(R.color.audio_result_bg)
    val AudioResultColor: Color @Composable get() = colorRes(R.color.audio_result_color)
    val AudioCodecColor: Color @Composable get() = colorRes(R.color.audio_codec_color)

    // Battery demo colors
    val BatteryProgressTrack: Color @Composable get() = colorRes(R.color.battery_progress_track)
    val BatteryResultBg: Color @Composable get() = colorRes(R.color.battery_result_bg)
    val BatteryResultColor: Color @Composable get() = colorRes(R.color.battery_result_color)
    val BatteryCodecColor: Color @Composable get() = colorRes(R.color.battery_codec_color)
    val SuperPersistenceColor: Color @Composable get() = colorRes(R.color.super_persistence_color)

    // Permission Card Colors - Dark Mode
    val PermissionGrantedBgDark: Color @Composable get() = colorRes(R.color.permission_granted_bg_dark)
    val PermissionGrantedTitleDark: Color @Composable get() = colorRes(R.color.permission_granted_title_dark)
    val PermissionWarningTextDark: Color @Composable get() = colorRes(R.color.permission_warning_text_dark)
    val MonitoringActiveBgDark: Color @Composable get() = colorRes(R.color.monitoring_active_bg_dark)
    val MonitoringActiveTextDark: Color @Composable get() = colorRes(R.color.monitoring_active_text_dark)
    val WarningAmberTextDark: Color @Composable get() = colorRes(R.color.warning_amber_text_dark)

    // Permission Card Colors - Light Mode
    val PermissionGrantedBgLight: Color @Composable get() = colorRes(R.color.permission_granted_bg_light)
    val PermissionGrantedTitleLight: Color @Composable get() = colorRes(R.color.permission_granted_title_light)
    val PermissionWarningTextLight: Color @Composable get() = colorRes(R.color.permission_warning_text_light)
    val MonitoringActiveBgLight: Color @Composable get() = colorRes(R.color.monitoring_active_bg_light)
    val MonitoringActiveTextLight: Color @Composable get() = colorRes(R.color.monitoring_active_text_light)
    val WarningAmberTextLight: Color @Composable get() = colorRes(R.color.warning_amber_text_light)
}

// =============================================================================
// Top-level aliases - ALL forward to AppColors (loads from XML)
// =============================================================================

// Core brand colors
val Crimson: Color @Composable get() = AppColors.Crimson
val CrimsonDark: Color @Composable get() = AppColors.CrimsonDark
val IcyBlue: Color @Composable get() = AppColors.IcyBlue
val Amber: Color @Composable get() = AppColors.Amber
val LightGreen: Color @Composable get() = AppColors.LightGreen

// Backgrounds
val BackgroundDark: Color @Composable get() = AppColors.BackgroundDark
val SurfaceDark: Color @Composable get() = AppColors.SurfaceDark
val CardBackground: Color @Composable get() = AppColors.CardBackground
val BackgroundLight: Color @Composable get() = AppColors.BackgroundLight
val SurfaceLight: Color @Composable get() = AppColors.SurfaceLight

// Text
val TextPrimary: Color @Composable get() = AppColors.TextPrimary
val TextSecondary: Color @Composable get() = AppColors.TextSecondary
val TextPrimaryLight: Color @Composable get() = AppColors.TextPrimaryLight
val TextSecondaryLight: Color @Composable get() = AppColors.TextSecondaryLight

// Alerts / status
val DangerRed: Color @Composable get() = AppColors.DangerRed
val SuccessGreen: Color @Composable get() = AppColors.SuccessGreen
val InfoBlue: Color @Composable get() = AppColors.InfoBlue
val WarningYellow: Color @Composable get() = AppColors.WarningYellow
val WarningAmber: Color @Composable get() = AppColors.Amber

// Utility
val White: Color @Composable get() = AppColors.White
val Black: Color @Composable get() = AppColors.Black
val Transparent: Color @Composable get() = AppColors.Transparent

// Audio
val audio_analyzing_bg: Color @Composable get() = AppColors.AudioAnalyzingBg
val audio_analyzing_color: Color @Composable get() = AppColors.AudioAnalyzingColor
val audio_progress_track: Color @Composable get() = AppColors.AudioProgressTrack
val audio_result_bg: Color @Composable get() = AppColors.AudioResultBg
val audio_result_color: Color @Composable get() = AppColors.AudioResultColor
val audio_codec_color: Color @Composable get() = AppColors.AudioCodecColor

// Battery
val battery_progress_track: Color @Composable get() = AppColors.BatteryProgressTrack
val battery_result_bg: Color @Composable get() = AppColors.BatteryResultBg
val battery_result_color: Color @Composable get() = AppColors.BatteryResultColor
val battery_codec_color: Color @Composable get() = AppColors.BatteryCodecColor
val super_persistence_color: Color @Composable get() = AppColors.SuperPersistenceColor

// Permission card - dark
val PermissionGrantedBgDark: Color @Composable get() = AppColors.PermissionGrantedBgDark
val PermissionGrantedTitleDark: Color @Composable get() = AppColors.PermissionGrantedTitleDark
val PermissionWarningTextDark: Color @Composable get() = AppColors.PermissionWarningTextDark
val MonitoringActiveBgDark: Color @Composable get() = AppColors.MonitoringActiveBgDark
val MonitoringActiveTextDark: Color @Composable get() = AppColors.MonitoringActiveTextDark
val WarningAmberTextDark: Color @Composable get() = AppColors.WarningAmberTextDark

// Permission card - light
val PermissionGrantedBgLight: Color @Composable get() = AppColors.PermissionGrantedBgLight
val PermissionGrantedTitleLight: Color @Composable get() = AppColors.PermissionGrantedTitleLight
val PermissionWarningTextLight: Color @Composable get() = AppColors.PermissionWarningTextLight
val MonitoringActiveBgLight: Color @Composable get() = AppColors.MonitoringActiveBgLight
val MonitoringActiveTextLight: Color @Composable get() = AppColors.MonitoringActiveTextLight
val WarningAmberTextLight: Color @Composable get() = AppColors.WarningAmberTextLight