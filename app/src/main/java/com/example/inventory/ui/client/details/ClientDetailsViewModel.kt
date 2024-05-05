package com.example.inventory.ui.client.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.client.ClientsRepository
import com.example.inventory.data.measurement.Measurement
import com.example.inventory.data.measurement.MeasurementsRepository
import com.example.inventory.ui.client.entry.ClientDetails
import com.example.inventory.ui.client.entry.toClient
import com.example.inventory.ui.client.entry.toClientDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve, update and delete an client from the [ClientsRepository]'s data source and to contain measurements from [MeasurementsRepository]'s data source.
 */
class ClientDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val clientsRepository: ClientsRepository,
    private val measurementsRepository: MeasurementsRepository,
) : ViewModel() {

    private val clientId: Int = checkNotNull(savedStateHandle[ClientDetailsDestination.clientIdArg])

    /**
     * Holds the client details ui state. The data is retrieved from [ClientsRepository] and mapped to
     * the UI state.
     */
    val clientDetailsStateFlow: StateFlow<ClientDetailsUiState> =
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
     * Holds the measurements list flow. The data is retrieved from [MeasurementsRepository].
     */
    val measurementsStateFlow: StateFlow<List<Measurement>> =
        measurementsRepository.getAllClientMeasurementsStream(clientId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    /**
     * Deletes the client from the [ClientsRepository]'s data source.
     */
    suspend fun deleteClient() {
        clientsRepository.deleteClient(clientDetailsStateFlow.value.clientDetails.toClient())
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