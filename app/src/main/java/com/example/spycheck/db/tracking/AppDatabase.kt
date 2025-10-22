package com.example.spycheck.db.tracking

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TrackingEvent::class], version = 3, exportSchema = false)
abstract class TrackingDatabase : RoomDatabase() {

    abstract fun trackingEventDao(): TrackingEventDao

    companion object {
        @Volatile
        private var INSTANCE: TrackingDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tracking_events ADD COLUMN appName TEXT NOT NULL DEFAULT 'Unknown'")
                database.execSQL("ALTER TABLE tracking_events ADD COLUMN count INTEGER NOT NULL DEFAULT 1")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tracking_events_packageName_domain ON tracking_events(packageName, domain)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tracking_events ADD COLUMN category TEXT NOT NULL DEFAULT 'UNKNOWN'")
            }
        }

        fun getDatabase(context: Context): TrackingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackingDatabase::class.java,
                    "tracking_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}