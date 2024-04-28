package com.example.inventory.data.measurement

import com.example.inventory.data.client.Client
import kotlinx.coroutines.flow.Flow


/**
 * Repository that provides insert, update, delete, and retrieve of [Measurement] from a given data source.
 */
interface MeasurementsRepository {
    /**
     * Retrieve all the measurements from the the given data source.
     */
    fun getAllClientMeasurementsStream(clientId: Int): Flow<List<Measurement>>

    /**
     * Retrieve a measurement from the given data source that matches with the [id] and [clientId].
     */
    fun getMeasurementStream(id: Int, clientId: Int): Flow<Measurement?>

    /**
     * Insert measurement in the data source
     */
    suspend fun insertMeasurement(measurement: Measurement)

    /**
     * Delete measurement from the data source
     */
    suspend fun deleteMeasurement(measurement: Measurement)

    /**
     * Update measurement in the data source
     */
    suspend fun updateMeasurement(measurement: Measurement)
}