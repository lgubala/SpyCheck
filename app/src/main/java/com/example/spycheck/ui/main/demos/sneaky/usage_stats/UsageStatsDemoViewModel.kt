package com.example.spycheck.ui.main.demos.sneaky.usage_stats

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.ui.main.demos.sneaky.usage_stats.utils.UsageInsights
import com.example.spycheck.ui.main.demos.sneaky.usage_stats.utils.UsageStatsReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsageStatsDemoViewModel : ViewModel() {
    private lateinit var reader: UsageStatsReader
    private var context: Context? = null

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val _insights = MutableStateFlow<UsageInsights?>(null)
    val insights: StateFlow<UsageInsights?> = _insights.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun checkPermission(context: Context) {
        this.context = context

        if (!::reader.isInitialized) {
            reader = UsageStatsReader(context)
        }

        val hasPermission = reader.hasUsageStatsPermission()
        _hasPermission.value = hasPermission

        // Auto-analyze if permission is granted
        if (hasPermission) {
            analyzeUsageData()
        }
    }

    fun updatePermission(granted: Boolean) {
        _hasPermission.value = granted

        if (granted && !::reader.isInitialized && context != null) {
            reader = UsageStatsReader(context!!)
            analyzeUsageData()
        }
    }

    fun analyzeUsageData() {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _error.value = null
            try {
                val insights = reader.analyzeUsageData()
                _insights.value = insights
            } catch (e: Exception) {
                _error.value = "Failed to analyze usage data: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun openUsageAccessSettings() {
        if (::reader.isInitialized) {
            reader.openUsageAccessSettings()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // No cleanup needed for this reader
    }
}
