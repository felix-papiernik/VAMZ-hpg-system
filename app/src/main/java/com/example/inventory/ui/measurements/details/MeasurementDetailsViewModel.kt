package com.example.inventory.ui.measurements.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.inventory.ItemsRepository
import com.example.inventory.data.measurement.MeasurementsRepository
import com.example.inventory.ui.client.entry.ClientDetails
import com.example.inventory.ui.measurements.entry.MeasurementDetails
import com.example.inventory.ui.measurements.entry.toMeasurement
import com.example.inventory.ui.measurements.entry.toMeasurementDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve, update and delete an item from the [MeasurementsRepository]'s data source.
 */
class MeasurementDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val measurementsRepository: MeasurementsRepository,
) : ViewModel() {

    private val measurementId: Int = checkNotNull(savedStateHandle[MeasurementDetailsDestination.measurementIdArg])

    /**
     * Holds the measurement details ui state. The data is retrieved from [MeasurementsRepository] and mapped to
     * the UI state.
     */
    val measurementDetailsStateFlow: StateFlow<MeasurementDetailsUiState> =
        measurementsRepository.getMeasurementStream(measurementId)
            .filterNotNull()
            .map {
                MeasurementDetailsUiState(measurementDetails = it.toMeasurementDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MeasurementDetailsUiState()
            )

    /**
     * Deletes the measurement from the [MeasurementsRepository]'s data source.
     */
    suspend fun deleteMeasurement() {
        measurementsRepository.deleteMeasurement(measurementDetailsStateFlow.value.measurementDetails.toMeasurement())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5000L
    }
}

/**
 * UI state for ClientDetailsScreen
 */
data class MeasurementDetailsUiState(
    val measurementDetails: MeasurementDetails = MeasurementDetails(),
)