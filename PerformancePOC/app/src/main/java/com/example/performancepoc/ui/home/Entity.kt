package com.example.performancepoc.ui.home

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Vessel entity.
 * Used to wrap data classes for serialization into the database.
 */
@Entity(tableName = "vessel")
data class VesselEntity (
    /**
     * Type (qualified name) of the data.
     */
    @PrimaryKey val type: String,

    /**
     * Data serialized as json.
     */
    val data: String?
)
