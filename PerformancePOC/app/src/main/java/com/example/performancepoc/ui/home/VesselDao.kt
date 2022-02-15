package com.example.performancepoc.ui.home

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Vessel data access.
 * This should only be accessed via [VesselImpl].
 */
@Dao
abstract class VesselDao {
    // region blocking accessors

    /**
     * Get a single entity, by type.
     *
     * @param type qualified name of the data class represented by the enclosed data
     * @return the entity holding the enclosed data, or null if it does not exist
     */
    @Query("SELECT * FROM vessel WHERE type = :type")
    abstract fun getBlocking(type: String): VesselEntity?

    /**
     * Set a single entity.
     *
     * @param entity to store/replace in the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun setBlocking(entity: VesselEntity)

    /**
     * Delete a single entity.
     *
     * @param type qualified name of the data class represented by the enclosed data
     */
    @Query("DELETE FROM vessel WHERE type = :type")
    abstract fun deleteBlocking(type: String)

    // endregion

    // region suspend accessors

    /**
     * Get a single entity, by type, in a suspend function.
     *
     * @param type qualified name of the data class represented by the enclosed data
     * @return the entity holding the enclosed data, or null if it does not exist
     */
    @Query("SELECT * FROM vessel WHERE type = :type")
    abstract suspend fun get(type: String): VesselEntity?

    /**
     * Set a single entity, in a suspend function.
     *
     * @param entity to store/replace in the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun set(entity: VesselEntity)

    /**
     * Delete a single entity, in a suspend function.
     *
     * @param type qualified name of the data class represented by the enclosed data
     */
    @Query("DELETE FROM vessel WHERE type = :type")
    abstract suspend fun delete(type: String)

    // endregion

    // region observers

    /**
     * Return a flow that monitors when the specified type is updated.
     *
     * @param type qualified name of the data class represented by the enclosed data
     * @return a flow of entities
     */
    @Query("SELECT * FROM vessel WHERE type = :type")
    abstract fun getFlow(type: String): Flow<VesselEntity?>

    /**
     * Return a livedata that monitors when the specified type is updated.
     *
     * @param type qualified name of the data class represented by the enclosed data
     * @return a livedata of entities
     */
    @Query("SELECT * FROM vessel WHERE type = :type")
    abstract fun getLiveData(type: String): LiveData<VesselEntity?>

    // endregion

    // region utilities

    /**
     * Replace an old entity with a new entity inside a single suspending transaction.
     *
     * @param old entity to remove
     * @param new entity to add
     */
    @Transaction
    open suspend fun replace(old: VesselEntity, new: VesselEntity) {
        set(new)
        delete(old.type)
    }

    // endregion
}
