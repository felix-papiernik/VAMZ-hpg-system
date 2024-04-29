package com.example.inventory.ui.measurements.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.components.getCurrentDate
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object MeasurementEntryDestination : NavigationDestination {
    override val route = "measurement_entry"
    override val titleRes = R.string.add_measurement
    const val clientIdArg = "clientId"
    val routeWithArgs = "${route}/{$clientIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: MeasurementEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    clientId: Int
) {
    viewModel.updateUiState(MeasurementDetails(
        date = getCurrentDate(),
        clientId = clientId)
    )
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(MeasurementEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        MeasurementEntryBody(
            measurementUiState = viewModel.measurementUiState,
            onMeasurementValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveMeasurement()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun MeasurementEntryBody(
    measurementUiState: MeasurementUiState,
    onMeasurementValueChange: (MeasurementDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        MeasurementInputForm(
            measurementDetails = measurementUiState.measurementDetails,
            onMeasurementValueChange = onMeasurementValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = measurementUiState.isMeasurementValid
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun MeasurementInputForm(
    measurementDetails: MeasurementDetails,
    onMeasurementValueChange: (MeasurementDetails) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = measurementDetails.bodyWeightKg,
            onValueChange = { onMeasurementValueChange(measurementDetails.copy(bodyWeightKg = it)) },
            label = { Text(stringResource(R.string.weight_in_kg)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = measurementDetails.bodyWeightKg.isNotEmpty() && measurementDetails.bodyWeightKg.toDoubleOrNull() == null
        )
        OutlinedTextField(
            value = measurementDetails.leanMuscleMassKg,
            onValueChange = { onMeasurementValueChange(measurementDetails.copy(leanMuscleMassKg = it)) },
            label = { Text(stringResource(R.string.lean_muscle_mass_in_kg)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = measurementDetails.leanMuscleMassKg.isNotEmpty() && measurementDetails.leanMuscleMassKg.toDoubleOrNull() == null
        )
        OutlinedTextField(
            value = measurementDetails.bodyFatKg,
            onValueChange = { onMeasurementValueChange(measurementDetails.copy(bodyFatKg = it)) },
            label = { Text(stringResource(R.string.body_fat_in_kg)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = measurementDetails.bodyFatKg.isNotEmpty() && measurementDetails.bodyFatKg.toDoubleOrNull() == null
        )
        OutlinedTextField(
            value = measurementDetails.visceralFat,
            onValueChange = { onMeasurementValueChange(measurementDetails.copy(visceralFat = it)) },
            label = { Text(stringResource(R.string.visceral_fat)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = measurementDetails.visceralFat.isNotEmpty() && measurementDetails.visceralFat.toDoubleOrNull() == null
        )
        OutlinedTextField(
            value = measurementDetails.mineralsKg,
            onValueChange = { onMeasurementValueChange(measurementDetails.copy(mineralsKg = it)) },
            label = { Text(stringResource(R.string.minerals_in_kg)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = measurementDetails.mineralsKg.isNotEmpty() && measurementDetails.mineralsKg.toDoubleOrNull() == null
        )
        OutlinedTextField(
            value = measurementDetails.metabolicAge,
            onValueChange = { onMeasurementValueChange(measurementDetails.copy(metabolicAge = it)) },
            label = { Text(stringResource(R.string.metabolic_age)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = measurementDetails.metabolicAge.isNotEmpty() && measurementDetails.metabolicAge.toDoubleOrNull() == null
        )
    }
}
