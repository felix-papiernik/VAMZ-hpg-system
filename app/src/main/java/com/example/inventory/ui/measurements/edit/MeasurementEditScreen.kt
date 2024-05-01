package com.example.inventory.ui.measurements.edit

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.HpgTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.measurements.entry.MeasurementEntryBody
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.HpgTheme
import kotlinx.coroutines.launch


object MeasurementEditDestination : NavigationDestination {
    override val route = "measurement_edit"
    override val titleRes = R.string.edit_measurement
    const val measurementIdArg = "measurementId"
    val routeWithArgs = "$route/{$measurementIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeasurementEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HpgTopAppBar(
                title = stringResource(MeasurementEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        MeasurementEntryBody(
            measurementUiState = viewModel.measurementUiState,
            onMeasurementValueChange = viewModel::updateUiState,
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be updated in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.updateMeasurement()
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

@Preview(showBackground = true)
@Composable
fun MeasurementEditScreenPreview() {
    HpgTheme {
        MeasurementEditScreen(
            navigateBack = { },
            onNavigateUp = { }
        )
    }
}