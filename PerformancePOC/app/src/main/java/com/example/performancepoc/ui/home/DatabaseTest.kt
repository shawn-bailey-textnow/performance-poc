package com.example.performancepoc.ui.home

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Vessel database.
 */
@Database(entities = [VesselEntity::class], version = 1)
abstract class DatabaseTest : RoomDatabase() {
    abstract fun vesselDao(): VesselDao
}
