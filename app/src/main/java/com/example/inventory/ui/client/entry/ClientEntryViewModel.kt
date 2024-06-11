package com.example.inventory.ui.client.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.client.Client
import com.example.inventory.data.client.ClientsRepository
import com.example.inventory.ui.components.isValidDate

/**
 * ViewModel to validate and insert clients in the Room database.
 */
class ClientEntryViewModel(private val clientsRepository: ClientsRepository) : ViewModel() {

    /**
     * Holds current client ui state
     */
    var clientUiState by mutableStateOf(ClientUiState())
        private set

    /**
     * Updates the [clientUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(clientDetails: ClientDetails) {
        clientUiState =
            ClientUiState(clientDetails = clientDetails, isEntryValid = validateClientDetailsInput(clientDetails))
    }

    /**
     * Inserts an [Client] in the Room database
     */
    suspend fun saveClient() {
        if (validateClientDetailsInput(clientUiState.clientDetails)) {
            clientsRepository.insertClient(clientUiState.clientDetails.toClient())
        }
    }
}
fun validateClientDetailsInput(uiState: ClientDetails): Boolean {
    return with(uiState) {
        firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && dateOfBirth.isNotBlank() &&
                email.contains("@") && isValidDate(dateOfBirth)
    }
}

fun validEmail(email: String): Boolean {
    return email.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
}
//These functions and data classes are used to separate the UI state from the database model.

/**
 * Represents Ui State for a Client.
 */
data class ClientUiState(
    val clientDetails: ClientDetails = ClientDetails(),
    val isEntryValid: Boolean = false
)

data class ClientDetails(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
)

/**
 * Extension function to convert [ClientUiState] to [Client].
 */
fun ClientDetails.toClient(): Client = Client(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    dateOfBirth = dateOfBirth
)

/**
 * Extension function to convert [Client] to [ClientUiState]
 */
fun Client.toClientUiState(isEntryValid: Boolean = false): ClientUiState = ClientUiState(
    clientDetails = this.toClientDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Client] to [ClientDetails]
 */
fun Client.toClientDetails(): ClientDetails = ClientDetails(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    dateOfBirth = dateOfBirth
)