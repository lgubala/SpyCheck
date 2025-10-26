package com.example.spycheck.ui.main.demos.sneaky.clipboard.utils

import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ClipboardEntry(
    val content: String,
    val timestamp: Long,
    val type: ClipboardContentType
)

enum class ClipboardContentType {
    PASSWORD,
    CREDIT_CARD,
    EMAIL,
    PHONE,
    URL,
    CODE,
    TEXT
}

class ClipboardMonitor(private val context: Context) {

    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private val _clipboardHistory = MutableStateFlow<List<ClipboardEntry>>(emptyList())
    val clipboardHistory: StateFlow<List<ClipboardEntry>> = _clipboardHistory.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    private val clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
        captureClipboard()
    }

    fun startMonitoring() {
        _isMonitoring.value = true
        captureClipboard()
        clipboardManager.addPrimaryClipChangedListener(clipboardListener)
    }

    fun stopMonitoring() {
        _isMonitoring.value = false
        clipboardManager.removePrimaryClipChangedListener(clipboardListener)
    }

    fun clearHistory() {
        _clipboardHistory.value = emptyList()
    }

    private fun captureClipboard() {
        try {
            val clip = clipboardManager.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()

                if (!text.isNullOrBlank()) {
                    val type = detectContentType(text)
                    val entry = ClipboardEntry(
                        content = text,
                        timestamp = System.currentTimeMillis(),
                        type = type
                    )

                    val history = _clipboardHistory.value.toMutableList()
                    if (history.isEmpty() || history.first().content != text) {
                        history.add(0, entry)
                        if (history.size > 20) history.removeAt(history.size - 1)
                        _clipboardHistory.value = history
                    }
                }
            }
        } catch (e: Exception) {}
    }

    private fun detectContentType(text: String): ClipboardContentType {
        val trimmed = text.trim()
        if (trimmed.replace(Regex("[\\s-]"), "").matches(Regex("\\d{13,19}"))) return ClipboardContentType.CREDIT_CARD
        if (trimmed.matches(Regex("^\\d{4,8}$"))) return ClipboardContentType.CODE
        if (trimmed.matches(Regex("^[+]?[\\d\\s().-]{10,}$"))) return ClipboardContentType.PHONE
        if (trimmed.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) return ClipboardContentType.EMAIL
        if (trimmed.matches(Regex("^(https?://|www\\.).*", RegexOption.IGNORE_CASE))) return ClipboardContentType.URL

        val hasUpper = trimmed.any { it.isUpperCase() }
        val hasLower = trimmed.any { it.isLowerCase() }
        val hasDigit = trimmed.any { it.isDigit() }
        val hasSymbol = trimmed.any { !it.isLetterOrDigit() }
        if (trimmed.length >= 8 && listOf(hasUpper, hasLower, hasDigit, hasSymbol).count { it } >= 3) return ClipboardContentType.PASSWORD

        return ClipboardContentType.TEXT
    }

    fun getTypeIcon(type: ClipboardContentType): String = when (type) {
        ClipboardContentType.PASSWORD -> "🔐"
        ClipboardContentType.CREDIT_CARD -> "💳"
        ClipboardContentType.EMAIL -> "📧"
        ClipboardContentType.PHONE -> "📞"
        ClipboardContentType.URL -> "🔗"
        ClipboardContentType.CODE -> "🔢"
        ClipboardContentType.TEXT -> "📄"
    }

    fun getTypeLabel(type: ClipboardContentType): String = when (type) {
        ClipboardContentType.PASSWORD -> "Password"
        ClipboardContentType.CREDIT_CARD -> "Credit Card"
        ClipboardContentType.EMAIL -> "Email"
        ClipboardContentType.PHONE -> "Phone Number"
        ClipboardContentType.URL -> "URL"
        ClipboardContentType.CODE -> "OTP/Code"
        ClipboardContentType.TEXT -> "Text"
    }

    fun isSensitiveType(type: ClipboardContentType): Boolean = type in listOf(
        ClipboardContentType.PASSWORD, ClipboardContentType.CREDIT_CARD, ClipboardContentType.CODE
    )
}
