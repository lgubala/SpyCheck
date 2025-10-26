package com.example.spycheck.ui.main.demos.sneaky.clipboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.sneaky.clipboard.utils.ClipboardMonitor
import com.example.spycheck.ui.main.demos.sneaky.clipboard.utils.ClipboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClipboardDemoViewModel : ViewModel() {

    private var clipboardMonitor: ClipboardMonitor? = null

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    private val _clipboardHistory = MutableStateFlow<List<ClipboardEntry>>(emptyList())
    val clipboardHistory: StateFlow<List<ClipboardEntry>> = _clipboardHistory.asStateFlow()

    fun initialize(context: Context) {
        if (clipboardMonitor == null) {
            clipboardMonitor = ClipboardMonitor(context)
            
            // Observe clipboard history
            viewModelScope.launch {
                clipboardMonitor?.clipboardHistory?.collect { history ->
                    _clipboardHistory.value = history
                }
            }
            
            // Observe monitoring state
            viewModelScope.launch {
                clipboardMonitor?.isMonitoring?.collect { monitoring ->
                    _isMonitoring.value = monitoring
                }
            }
        }
    }

    fun startMonitoring() {
        clipboardMonitor?.startMonitoring()
    }

    fun stopMonitoring() {
        clipboardMonitor?.stopMonitoring()
    }

    fun clearHistory() {
        clipboardMonitor?.clearHistory()
    }

    fun getTypeIcon(entry: ClipboardEntry): String {
        return clipboardMonitor?.getTypeIcon(entry.type) ?: "📄"
    }

    fun getTypeLabel(entry: ClipboardEntry): String {
        return clipboardMonitor?.getTypeLabel(entry.type) ?: "Text"
    }

    fun isSensitive(entry: ClipboardEntry): Boolean {
        return clipboardMonitor?.isSensitiveType(entry.type) ?: false
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}
