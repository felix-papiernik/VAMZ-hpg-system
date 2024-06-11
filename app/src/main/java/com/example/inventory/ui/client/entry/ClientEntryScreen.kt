package com.example.inventory.ui.client.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.HpgTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.components.DateInput
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.HpgTheme
import kotlinx.coroutines.launch

object ClientEntryDestination : NavigationDestination {
    override val route = "client_entry"
    override val titleRes = R.string.add_client
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ClientEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HpgTopAppBar(
                title = stringResource(ClientEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ClientEntryBody(
            clientUiState = viewModel.clientUiState,
            onClientValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveClient()
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
fun ClientEntryBody(
    clientUiState: ClientUiState,
    onClientValueChange: (ClientDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        ClientInputForm(
            clientDetails = clientUiState.clientDetails,
            onValueChange = onClientValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = clientUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientInputForm(
    clientDetails: ClientDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ClientDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = clientDetails.firstName,
            onValueChange = { onValueChange(clientDetails.copy(firstName = it)) },
            label = { Text(stringResource(R.string.client_firstname)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = clientDetails.lastName,
            onValueChange = { onValueChange(clientDetails.copy(lastName = it)) },
            label = { Text(stringResource(R.string.client_lastname)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = clientDetails.email,
            onValueChange = { onValueChange(clientDetails.copy(email = it)) },
            label = { Text(stringResource(R.string.client_email)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = clientDetails.email.isNotEmpty() && !clientDetails.email.contains("@")
        )
        DateInput(
            value = clientDetails.dateOfBirth,
            onValueChange = { onValueChange(clientDetails.copy(dateOfBirth = it)) },
            labelResId = R.string.client_date_of_birth_
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
            DatePicker(
                state = state,
                modifier = Modifier.padding(16.dp),
                dateFormatter = )

            Text(
                "Entered date timestamp: ${state.selectedDateMillis ?: "no input"}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClientEntryScreenPreview() {
    HpgTheme {
        ClientEntryBody(clientUiState = ClientUiState(
            ClientDetails(
                firstName = "Félix",
                lastName = "Papiernik",
                email = "felixpapiernik42@gmail.com",
                dateOfBirth = "01.01.2000"
            )
        ), onClientValueChange = {}, onSaveClick = {})
    }
}