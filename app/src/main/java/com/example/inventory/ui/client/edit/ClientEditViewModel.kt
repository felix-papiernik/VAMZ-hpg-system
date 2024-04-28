package com.example.inventory.ui.client.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.R
import com.example.inventory.data.client.ClientsRepository
import com.example.inventory.ui.client.entry.ClientDetails
import com.example.inventory.ui.client.entry.ClientUiState
import com.example.inventory.ui.client.entry.toClient
import com.example.inventory.ui.client.entry.toClientUiState
import com.example.inventory.ui.components.LongToDateString
import com.example.inventory.ui.components.isValidDate
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update a client from the [ClientsRepository]'s data source.
 */
class ClientEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val clientsRepository: ClientsRepository
) : ViewModel() {

    /**
     * Holds current client ui state
     */
    var clientUiState by mutableStateOf(ClientUiState())
        private set

    private val clientId: Int = checkNotNull(savedStateHandle[ClientEditDestination.clientIdArg])

    init {
        viewModelScope.launch {
            clientUiState = clientsRepository.getClientStream(clientId)
                .filterNotNull()
                .first()
                .toClientUiState(true)
        }
    }

    /**
     * Update the client in the [ClientsRepository]'s data source
     */
    suspend fun updateClient() {
        if (validateInput(clientUiState.clientDetails)) {
            clientsRepository.updateClient(clientUiState.clientDetails.toClient())
        }
    }

    /**
     * Updates the [clientUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(clientDetails: ClientDetails) {
        clientUiState =
            ClientUiState(clientDetails = clientDetails, isEntryValid = validateInput(clientDetails))
    }

    private fun validateInput(uiState: ClientDetails = clientUiState.clientDetails): Boolean {
        return with(uiState) {
            firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && dateOfBirth.isNotBlank() &&
                    email.contains("@") && isValidDate(dateOfBirth)
        }
    }
}