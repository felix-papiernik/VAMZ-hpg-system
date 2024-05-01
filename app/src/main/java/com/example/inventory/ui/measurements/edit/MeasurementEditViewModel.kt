package com.example.inventory.ui.measurements.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.client.ClientsRepository
import com.example.inventory.data.measurement.MeasurementsRepository
import com.example.inventory.ui.client.entry.ClientDetails
import com.example.inventory.ui.client.entry.ClientUiState
import com.example.inventory.ui.client.entry.toClient
import com.example.inventory.ui.components.isValidDate
import com.example.inventory.ui.measurements.entry.MeasurementDetails
import com.example.inventory.ui.measurements.entry.MeasurementUiState
import com.example.inventory.ui.measurements.entry.toMeasurement
import com.example.inventory.ui.measurements.entry.toMeasurementUiState
import com.example.inventory.ui.measurements.entry.validateMeasurementInput
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update a client from the [ClientsRepository]'s data source.
 */
class MeasurementEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val measurementsRepository: MeasurementsRepository
) : ViewModel() {

    /**
     * Holds current measurement ui state
     */
    var measurementUiState by mutableStateOf(MeasurementUiState())
        private set

    private val measurementId: Int = checkNotNull(savedStateHandle[MeasurementEditDestination.measurementIdArg])

    init {
        viewModelScope.launch {
            measurementUiState = measurementsRepository.getMeasurementStream(measurementId)
                .filterNotNull()
                .first()
                .toMeasurementUiState(true)
        }
    }

    /**
     * Update the measurement in the [MeasurementsRepository]'s data source
     */
    suspend fun updateMeasurement() {
        if (validateMeasurementInput(measurementUiState.measurementDetails)) {
            measurementsRepository.updateMeasurement(measurementUiState.measurementDetails.toMeasurement())
        }
    }

    /**
     * Updates the [measurementUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(measurementDetails: MeasurementDetails) {
        measurementUiState =
            MeasurementUiState(measurementDetails = measurementDetails, isMeasurementValid = validateMeasurementInput(measurementDetails))
    }
}