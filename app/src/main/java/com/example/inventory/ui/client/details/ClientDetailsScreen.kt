package com.example.inventory.ui.client.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.inventory.HpgTopAppBar
import com.example.inventory.R
import com.example.inventory.data.client.Client
import com.example.inventory.data.measurement.Measurement
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.client.entry.toClient
import com.example.inventory.ui.client.entry.toClientDetails
import com.example.inventory.ui.components.DeleteConfirmationDialog
import com.example.inventory.ui.components.DetailsRow
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.HpgTheme
import kotlinx.coroutines.launch

object ClientDetailsDestination : NavigationDestination {
    override val route = "client_details"
    override val titleRes = R.string.client_detail_title
    const val clientIdArg = "clientId"
    val routeWithArgs = "$route/{$clientIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(
    navigateToEditClientPersonalInformation: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToMeasurementEntry: (Int) -> Unit,
    navigateToMeasurementUpdate: (Int) -> Unit
) {
    val clientDetailsUiState = viewModel.clientDetailsStateFlow.collectAsState()
    val measurementsUiState = viewModel.measurementsStateFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HpgTopAppBar(
                title = stringResource(ClientDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToMeasurementEntry(clientDetailsUiState.value.clientDetails.id)/* TODO decide if even needed */ },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_new_measurement),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        ClientDetailsBody(
            clientDetailsUiState = clientDetailsUiState.value,
            onUpdatePersonalInformation = { navigateToEditClientPersonalInformation(clientDetailsUiState.value.clientDetails.id) },
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.deleteClient()
                    navigateBack()
                }
            },
            navigateToMeasurementEntry = { navigateToMeasurementEntry(clientDetailsUiState.value.clientDetails.id) },
            navigateToMeasurementUpdate = navigateToMeasurementUpdate,
            measurementsList = measurementsUiState.value,
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
private fun ClientDetailsBody(
    clientDetailsUiState: ClientDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onUpdatePersonalInformation: () -> Unit,
    navigateToMeasurementEntry: () -> Unit,
    measurementsList: List<Measurement>,
    navigateToMeasurementUpdate: (Int) -> Unit
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        ClientPersonalInformation(
            onUpdatePersonalInformation = onUpdatePersonalInformation,
            client = clientDetailsUiState.clientDetails.toClient(),
            modifier = Modifier.fillMaxWidth()
        )
        ClientMeasurements(
            measurementsList = measurementsList,
            navigateToMeasurementUpdate = navigateToMeasurementUpdate,
            navigateToMeasurementEntry = navigateToMeasurementEntry,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(stringResource(R.string.delete_client))
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

@Composable
fun ClientMeasurements(
    modifier: Modifier = Modifier,
    navigateToMeasurementEntry: () -> Unit,
    measurementsList: List<Measurement>,
    navigateToMeasurementUpdate: (Int) -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {

            Row(modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.measurements),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(
                            id = R.dimen
                                .padding_medium
                        )
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = navigateToMeasurementEntry) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_new_measurement),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            if (measurementsList.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_measurements),
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(
                            id = R.dimen
                                .padding_medium
                        )
                    )
                )
            } else {
                MeasurementsList(
                    measurementsList = measurementsList,
                    onMeasurementItemClick = { navigateToMeasurementUpdate(it.id) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (measurementsList.size > 1) {
                    MeasurementCharts(
                        measurementsList = measurementsList,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

    }
}

@Composable
fun MeasurementCharts(measurementsList: List<Measurement>, modifier: Modifier) {
    MeasurementChart(
        measurementData = measurementsList.map { MeasurementData(it.date, it.bodyWeightKg) },
        dataPoints = measurementsList.mapIndexed { index, measurement -> Point(index.toFloat(), measurement.bodyWeightKg.toFloat()) },
        chartTitle = stringResource(R.string.bodyweight_in_kg),
        modifier = modifier
    )
    MeasurementChart(
        measurementData = measurementsList.map { MeasurementData(it.date, it.leanMuscleMassKg) },
        dataPoints = measurementsList.mapIndexed { index, measurement -> Point(index.toFloat(), measurement.leanMuscleMassKg.toFloat()) },
        chartTitle = stringResource(R.string.lean_muscle_mass_in_kg),
        modifier = modifier
    )
    MeasurementChart(
        measurementData = measurementsList.map { MeasurementData(it.date, it.bodyFatKg) },
        dataPoints = measurementsList.mapIndexed { index, measurement -> Point(index.toFloat(), measurement.bodyFatKg.toFloat()) },
        chartTitle = stringResource(R.string.body_fat_in_kg),
        modifier = modifier
    )
    MeasurementChart(
        measurementData = measurementsList.map { MeasurementData(it.date, it.visceralFat) },
        dataPoints = measurementsList.mapIndexed { index, measurement -> Point(index.toFloat(), measurement.visceralFat.toFloat()) },
        chartTitle = stringResource(R.string.visceral_fat),
        modifier = modifier
    )
    MeasurementChart(
        measurementData = measurementsList.map { MeasurementData(it.date, it.mineralsKg) },
        dataPoints = measurementsList.mapIndexed { index, measurement -> Point(index.toFloat(), measurement.mineralsKg.toFloat()) },
        chartTitle = stringResource(R.string.minerals_in_kg),
        modifier = modifier
    )
    MeasurementChart(
        measurementData = measurementsList.map { MeasurementData(it.date, it.metabolicAge) },
        dataPoints = measurementsList.mapIndexed { index, measurement -> Point(index.toFloat(), measurement.metabolicAge.toFloat()) },
        chartTitle = stringResource(R.string.metabolic_age),
        modifier = modifier
    )
}

data class MeasurementData(val date: String, val value: Double)

/**
 * Single line chart with grid lines
 *
 * @param measurementData
 */
@Composable
private fun MeasurementChart(
    measurementData: List<MeasurementData>,
    dataPoints: List<Point>,
    chartTitle: String,
    modifier: Modifier,
) {
    val steps = measurementData.size
    val xAxisData = AxisData.Builder()
        .axisStepSize(80.dp)
        .steps(measurementData.size - 1)
        .labelData { i -> measurementData[i].date }
        .labelAndAxisLinePadding(15.dp)
        .backgroundColor(Color.Transparent)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            // Add yMin to get the negative axis values to the scale
            val yMin = measurementData.minOf { it.value }
            val yMax = measurementData.maxOf { it.value }
            val yScale = (yMax - yMin) / steps
            String.format("%.2f", (i * yScale) + yMin)
        }.build()
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = dataPoints,
                    lineStyle = LineStyle(lineType = LineType.Straight(false)),
                    IntersectionPoint(),
                    SelectionHighlightPoint(),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp(
                        popUpLabel = { x, y -> measurementData[x.toInt()].value.toString() },
                        backgroundColor = Color.White,
                    )
                )
            )
        ),
        yAxisData = yAxisData,
        xAxisData = xAxisData,
        gridLines = GridLines(),
        paddingRight = 0.dp,
        backgroundColor = Color.White
    )
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
        ) {
            Text(
                text = chartTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)
            )
            LineChart(
                modifier = modifier
                    .fillMaxWidth()
                    //.padding(50.dp, 0.dp, 0.dp, 0.dp)
                    .height(200.dp),
                lineChartData = data
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MeasurementChartYPreview() {
    HpgTheme {
        MeasurementCharts(
            measurementsList = emptyList(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MeasurementsList(
    modifier: Modifier = Modifier,
    onMeasurementItemClick: (Measurement) -> Unit,
    measurementsList: List<Measurement>
) {
    Column(
        modifier = modifier,
    ) {
        measurementsList.forEach { item ->
            MeasurementsListItem(item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onMeasurementItemClick(item) })
        }
    }
}

@Composable
private fun MeasurementsListItem(
    item: Measurement, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
        ) {
            Text(
                text = item.date,
            )
        }
    }
}

@Composable
fun ClientPersonalInformation(
    client: Client,
    modifier: Modifier = Modifier,
    onUpdatePersonalInformation: () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Row(modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.personal_information),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(
                            id = R.dimen
                                .padding_medium
                        )
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onUpdatePersonalInformation ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_personal_informations)
                    )
                }
            }
            DetailsRow(labelResId = R.string.firstname, value = client.firstName, modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen
                        .padding_medium
                )
            ))
            DetailsRow(labelResId = R.string.lastname, value = client.lastName, modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen
                        .padding_medium
                )
            ))
            DetailsRow(labelResId = R.string.email, value = client.email, modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen
                        .padding_medium
                )
            ))
            DetailsRow(labelResId = R.string.date_of_birth, value = client.dateOfBirth, modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen
                        .padding_medium
                )
            ))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailsScreenPreview() {
    HpgTheme {
        ClientDetailsBody(
            ClientDetailsUiState(
                clientDetails = Client(
                    1,
                    "John",
                    "Doe",
                    "john.doe@mail.com",
                    "01.01.2000"
                ).toClientDetails()
            ),
            onDelete = {},
            onUpdatePersonalInformation = { },
            navigateToMeasurementEntry = {},
            measurementsList = emptyList(),
            navigateToMeasurementUpdate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailsScreenPreviewWithMeasurements() {
    HpgTheme {
        ClientDetailsBody(
            ClientDetailsUiState(
                clientDetails = Client(
                    1,
                    "John",
                    "Doe",
                    "john.doe@mail.com",
                    "01.01.2000"
                ).toClientDetails()
            ),
            onDelete = {},
            onUpdatePersonalInformation = { },
            navigateToMeasurementEntry = {},
            measurementsList = listOf(
                Measurement(1, 1, "01.01.2021", 80.0, 20.0, 10.0, 5.0, 5.0, 20.0),
                Measurement(2, 1, "01.02.2021", 79.7, 21.6, 8.7, 5.0, 5.0, 20.0),
            ),
            navigateToMeasurementUpdate = {}
        )
    }
}