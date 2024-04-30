/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.measurements.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.HpgTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.components.DeleteConfirmationDialog
import com.example.inventory.ui.components.DetailsCard
import com.example.inventory.ui.measurements.entry.MeasurementDetails
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.HpgTheme
import kotlinx.coroutines.launch

object MeasurementDetailsDestination : NavigationDestination {
    override val route = "measurement_details"
    override val titleRes = R.string.measurement_detail_title
    const val measurementIdArg = "measurementId"
    val routeWithArgs = "$route/{$measurementIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementDetailsScreen(
    navigateToEditMeasurement: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeasurementDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.measurementDetailsStateFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HpgTopAppBar(
                title = stringResource(MeasurementDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditMeasurement(uiState.value.measurementDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_measurement),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        MeasurementDetailsBody(
            measuremntDetailsUiState = uiState.value,
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.deleteMeasurement()
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
        )
    }
}

@Composable
private fun MeasurementDetailsBody(
    measuremntDetailsUiState: MeasurementDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        DetailsCard(
            labelValuePairArray = arrayOf(
                Pair(R.string.date_of_measurement, measuremntDetailsUiState.measurementDetails.date),
                Pair(R.string.bodyweight_in_kg, measuremntDetailsUiState.measurementDetails.bodyWeightKg),
                Pair(R.string.lean_muscle_mass_in_kg, measuremntDetailsUiState.measurementDetails.leanMuscleMassKg),
                Pair(R.string.body_fat_in_kg, measuremntDetailsUiState.measurementDetails.bodyFatKg),
                Pair(R.string.visceral_fat, measuremntDetailsUiState.measurementDetails.visceralFat),
                Pair(R.string.minerals_in_kg, measuremntDetailsUiState.measurementDetails.mineralsKg),
                Pair(R.string.metabolic_age, measuremntDetailsUiState.measurementDetails.metabolicAge),
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MeasurementDetailsScreenPreview() {
    HpgTheme {
        MeasurementDetailsBody(
            MeasurementDetailsUiState(
                measurementDetails = MeasurementDetails(
                    1,
                    1,
                    "24.05.2024",
                    "70.0",
                    "60.0",
                    "10.0",
                    "5.0",
                    "5.0",
                    "20.0"
                )
            ),
            onDelete = {}
        )
    }
}
