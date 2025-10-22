package com.example.spycheck.ui.main.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spycheck.db.tracking.TrackingDatabase
import com.example.spycheck.db.tracking.TrackingEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = TrackingDatabase.getDatabase(application)

    val trackingEvents: StateFlow<List<TrackingEvent>> = database.trackingEventDao()
        .getGroupedEvents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}