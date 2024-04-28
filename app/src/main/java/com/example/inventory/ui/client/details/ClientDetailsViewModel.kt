package com.example.inventory.ui.client.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.client.ClientsRepository
import com.example.inventory.data.inventory.ItemsRepository
import com.example.inventory.ui.client.entry.ClientDetails
import com.example.inventory.ui.client.entry.toClient
import com.example.inventory.ui.client.entry.toClientDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve, update and delete an item from the [ItemsRepository]'s data source.
 */
class ClientDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val clientsRepository: ClientsRepository,
) : ViewModel() {

    private val clientId: Int = checkNotNull(savedStateHandle[ClientDetailsDestination.clientIdArg])

    /**
     * Holds the item details ui state. The data is retrieved from [ItemsRepository] and mapped to
     * the UI state.
     */
    val uiState: StateFlow<ClientDetailsUiState> =
        clientsRepository.getClientStream(clientId)
            .filterNotNull()
            .map {
                ClientDetailsUiState(clientDetails = it.toClientDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ClientDetailsUiState()
            )

    /**
     * Deletes the client from the [ClientsRepository]'s data source.
     */
    suspend fun deleteItem() {
        clientsRepository.deleteClient(uiState.value.clientDetails.toClient())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for ClientDetailsScreen
 */
data class ClientDetailsUiState(
    val clientDetails: ClientDetails = ClientDetails(),
)