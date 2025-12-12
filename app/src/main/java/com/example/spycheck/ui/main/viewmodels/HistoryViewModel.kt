package com.example.spycheck.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.db.tracking.TrackingDatabase
import com.example.spycheck.db.tracking.TrackingEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.example.spycheck.services.tracking.TrackingDataHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TrackingDatabase.getDatabase(application)
    private val dao = database.trackingEventDao()

    val trackingEvents: StateFlow<List<TrackingEvent>> = dao
        .getGroupedEvents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            // Clear DB entries
            dao.clearAll()

            // Reset in-memory counters / overlay stats
            TrackingDataHolder.clearCounts()
        }
    }
}
