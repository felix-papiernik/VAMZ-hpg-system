package com.example.inventory.data.measurement

import kotlinx.coroutines.flow.Flow

class OfflineMeasurementsRepository(private val measurementDao: MeasurementDao) : MeasurementsRepository {
    override fun getAllClientMeasurementsStream(clientId: Int): Flow<List<Measurement>> = measurementDao.getAllClientMeasurements(clientId)

    override fun getMeasurementStream(id: Int, clientId: Int): Flow<Measurement> = measurementDao.getMeasurement(id, clientId)

    override suspend fun insertMeasurement(measurement: Measurement) = measurementDao.insert(measurement)

    override suspend fun deleteMeasurement(measurement: Measurement) = measurementDao.delete(measurement)

    override suspend fun updateMeasurement(measurement: Measurement) = measurementDao.update(measurement)
}