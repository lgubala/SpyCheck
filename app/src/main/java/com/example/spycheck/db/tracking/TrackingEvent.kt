package com.example.spycheck.db.tracking

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracking_events",
    indices = [Index(value = ["packageName", "domain"], unique = false)]
)
data class TrackingEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val domain: String,
    val timestamp: Long,
    val count: Int = 1,
    val category: String = "UNKNOWN"
)