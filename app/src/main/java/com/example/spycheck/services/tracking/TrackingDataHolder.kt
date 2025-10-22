package com.example.spycheck.services.tracking

import com.example.spycheck.db.tracking.TrackingEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A singleton object to hold and share tracking events between services.
 * Maintains both individual event stream and total counter.
 */
object TrackingDataHolder {
    // Individual events with counters (for popup display)
    private val _latestEvent = MutableStateFlow<TrackingEvent?>(null)
    val latestEvent = _latestEvent.asStateFlow()

    // Total count of ALL tracking attempts (for main screen)
    private val _totalTrackingCount = MutableStateFlow(0)
    val totalTrackingCount = _totalTrackingCount.asStateFlow()

    // Track counters for each app+domain combination
    private val eventCounts = mutableMapOf<String, Int>()

    // Track last event time for auto-hide logic
    private val _lastEventTimestamp = MutableStateFlow(0L)
    val lastEventTimestamp = _lastEventTimestamp.asStateFlow()

    fun updateLatestEvent(event: TrackingEvent) {
        val key = "${event.packageName}|${event.domain}"
        val currentCount = eventCounts.getOrDefault(key, 0) + 1
        eventCounts[key] = currentCount

        val newTimestamp = System.currentTimeMillis()

        // Increment total counter
        _totalTrackingCount.value = _totalTrackingCount.value + 1

        // Update latest event with counter
        val updatedEvent = event.copy(
            id = newTimestamp,
            count = currentCount,
            timestamp = newTimestamp
        )

        _latestEvent.value = updatedEvent
        _lastEventTimestamp.value = newTimestamp
    }

    fun clearCounts() {
        eventCounts.clear()
        _totalTrackingCount.value = 0
        _latestEvent.value = null
        _lastEventTimestamp.value = 0L
    }
}