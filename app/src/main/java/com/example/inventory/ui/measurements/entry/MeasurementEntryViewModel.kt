package com.example.inventory.ui.measurements.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.measurement.Measurement
import com.example.inventory.data.measurement.MeasurementsRepository
import com.example.inventory.ui.components.getCurrentDate

/**
 * ViewModel to validate and insert measurements in the Room database.
 */
class MeasurementEntryViewModel(private val measurementsRepository: MeasurementsRepository) : ViewModel() {

    /**
     * Holds current measurement ui state
     */
    var measurementUiState by mutableStateOf(
        MeasurementUiState(
            measurementDetails = MeasurementDetails(
                date = getCurrentDate()
            )

        )
    )
        private set

    /**
     * Updates the [measurementUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(measurementDetails: MeasurementDetails) {
        measurementUiState =
            MeasurementUiState(measurementDetails = measurementDetails, isMeasurementValid = validateMeasurementInput(measurementDetails))
    }

    /**
     * Inserts an [Measurement] in the Room database
     */
    suspend fun saveMeasurement() {
        if (validateMeasurementInput(measurementUiState.measurementDetails)) {
            measurementsRepository.insertMeasurement(measurementUiState.measurementDetails.toMeasurement())
        }
    }
}

fun validateMeasurementInput(uiState: MeasurementDetails): Boolean {
    return with(uiState) {
        bodyWeightKg.isNotEmpty() && bodyWeightKg.toDoubleOrNull() != null
                && leanMuscleMassKg.isNotEmpty() && leanMuscleMassKg.toDoubleOrNull() != null
                && bodyFatKg.isNotEmpty() && bodyFatKg.toDoubleOrNull() != null
                && visceralFat.isNotEmpty() && visceralFat.toDoubleOrNull() != null
                && mineralsKg.isNotEmpty() && mineralsKg.toDoubleOrNull() != null
                && metabolicAge.isNotEmpty() && metabolicAge.toDoubleOrNull() != null
    }
}

//These functions and data classes are used to separate the UI state from the database model.
/**
 * Represents Ui State for a Client.
 */
data class MeasurementUiState(
    val measurementDetails: MeasurementDetails = MeasurementDetails(),
    val isMeasurementValid : Boolean = false
)

data class MeasurementDetails(
    val id: Int = 0,
    val clientId: Int = 0,
    val date: String = "",
    val bodyWeightKg: String = "",
    val leanMuscleMassKg: String = "",
    val bodyFatKg: String = "",
    val visceralFat: String = "",
    val mineralsKg: String = "",
    val metabolicAge: String = ""
)

/**
 * Extension function to convert [MeasurementUiState] to [Measurement].
 */
fun MeasurementDetails.toMeasurement(): Measurement = Measurement(
    id = id,
    clientId = clientId,
    date = date,
    bodyWeightKg = bodyWeightKg.toDoubleOrNull() ?: 0.0,
    leanMuscleMassKg = leanMuscleMassKg.toDoubleOrNull() ?: 0.0,
    bodyFatKg = bodyFatKg.toDoubleOrNull() ?: 0.0,
    visceralFat = visceralFat.toDoubleOrNull() ?: 0.0,
    mineralsKg = mineralsKg.toDoubleOrNull() ?: 0.0,
    metabolicAge = metabolicAge.toDoubleOrNull() ?: 0.0
)

/**
 * Extension function to convert [Measurement] to [MeasurementUiState]
 */
fun Measurement.toMeasurementUiState(isMeasurementCompleted: Boolean = false): MeasurementUiState = MeasurementUiState(
    measurementDetails = this.toMeasurementDetails(),
    isMeasurementValid = isMeasurementCompleted
)

/**
 * Extension function to convert [Measurement] to [MeasurementDetails]
 */
fun Measurement.toMeasurementDetails(): MeasurementDetails = MeasurementDetails(
    id = id,
    clientId = clientId,
    date = date,
    bodyWeightKg = bodyWeightKg.toString(),
    leanMuscleMassKg = leanMuscleMassKg.toString(),
    bodyFatKg = bodyFatKg.toString(),
    visceralFat = visceralFat.toString(),
    mineralsKg = mineralsKg.toString(),
    metabolicAge = metabolicAge.toString()
)