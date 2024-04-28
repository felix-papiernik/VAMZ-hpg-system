package com.example.inventory.data.measurement

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the measurements table.
 */
@Dao
interface MeasurementDao {

    @Query("select * from measurements where clientId = :clientId")
    fun getAllClientMeasurements(clientId: Int ): Flow<List<Measurement>>

    @Query("SELECT * from measurements WHERE id = :id and clientId = :clientId")
    fun getMeasurement(id: Int, clientId: Int): Flow<Measurement>

    //TODO DECIDE
    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Measurement into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(measurement: Measurement)

    @Update
    suspend fun update(measurement: Measurement)

    @Delete
    suspend fun delete(measurement: Measurement)
}