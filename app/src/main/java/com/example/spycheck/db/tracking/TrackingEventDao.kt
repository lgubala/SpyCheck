package com.example.spycheck.db.tracking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: TrackingEvent)

    @Query("SELECT * FROM tracking_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<TrackingEvent>>

    @Query("SELECT * FROM tracking_events WHERE packageName = :packageName ORDER BY timestamp DESC")
    fun getEventsForPackage(packageName: String): Flow<List<TrackingEvent>>

    // Get the most recent event for a specific app+domain combo
    @Query("SELECT * FROM tracking_events WHERE packageName = :packageName AND domain = :domain ORDER BY timestamp DESC LIMIT 1")
    suspend fun getExistingEvent(packageName: String, domain: String): TrackingEvent?

    // Get grouped events with counts
    @Query("""
        SELECT packageName, appName, domain, category, MAX(timestamp) as timestamp, 
               SUM(count) as count, MAX(id) as id
        FROM tracking_events 
        GROUP BY packageName, domain, category 
        ORDER BY timestamp DESC
    """)
    fun getGroupedEvents(): Flow<List<TrackingEvent>>

    // Update count for existing event
    @Query("UPDATE tracking_events SET count = count + 1, timestamp = :newTimestamp WHERE id = :id")
    suspend fun incrementCount(id: Long, newTimestamp: Long)

    @Transaction
    suspend fun insertOrUpdate(event: TrackingEvent) {
        val existing = getExistingEvent(event.packageName, event.domain)
        if (existing != null) {
            incrementCount(existing.id, System.currentTimeMillis())
        } else {
            insert(event)
        }
    }
}