package com.example.inventory.ui.client.details

import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.client.Client
import com.example.inventory.data.inventory.Item
import com.example.inventory.data.measurement.Measurement
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.client.entry.toClient
import com.example.inventory.ui.client.entry.toClientDetails
import com.example.inventory.ui.item.DeleteConfirmationDialog
import com.example.inventory.ui.item.formatedPrice
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme
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
    navigateToMeasurementEntry: (Int) -> Unit
) {
    val clientDetailsUiState = viewModel.clientDetailsStateFlow.collectAsState()
    val measurementsUiState = viewModel.measurementsStateFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ClientDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO decide if even needed */ },
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
                    viewModel.deleteItem()
                    navigateBack()
                }
            },
            navigateToMeasurementEntry = { navigateToMeasurementEntry(clientDetailsUiState.value.clientDetails.id) },
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
    measurementsList: List<Measurement>
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
            navigateToMeasurementEntry = navigateToMeasurementEntry,
            modifier = Modifier.fillMaxWidth()
        )
        /*
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }*/
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
    measurementsList: List<Measurement>
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                    onItemClick = { /* TODO */},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }
}

@Composable
fun MeasurementsList(
    modifier: Modifier = Modifier,
    onItemClick: (Measurement) -> Unit,
    measurementsList: List<Measurement>
) {
    Column(
        modifier = modifier,
    ) {
        measurementsList.forEach { item ->
            MeasurementsListItem(item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
private fun MeasurementsListItem(
    item: Measurement, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(dimensionResource(id = R.dimen.padding_medium)),
        ) {
            Text(
                text = item.date,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = item.id.toString(),
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
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            ClientDetailsRow(
                labelResID = R.string.firstname,
                clientDetail = client.firstName,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )
            ClientDetailsRow(
                labelResID = R.string.lastname,
                clientDetail = client.lastName,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )
            ClientDetailsRow(
                labelResID = R.string.email,
                clientDetail = client.email,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )
            ClientDetailsRow(
                labelResID = R.string.date_of_birth,
                clientDetail = client.dateOfBirth,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )
        }

    }
}

@Composable
private fun ClientDetailsRow(
    @StringRes labelResID: Int, clientDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = clientDetail, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailsScreenPreview() {
    InventoryTheme {
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
            measurementsList = emptyList()
        )
    }
}