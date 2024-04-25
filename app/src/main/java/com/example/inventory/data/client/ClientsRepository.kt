package com.example.inventory.data.client

import com.example.inventory.data.Item
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update?, delete, and retrieve of [Client] from a given data source.
 */
interface ClientsRepository {
    /**
     * Retrieve all the clients from the the given data source.
     */
    fun getAllClientsStream(): Flow<List<Client>>

    /**
     * Retrieve a client from the given data source that matches with the [id].
     */
    fun getClientStream(id: Int): Flow<Client?>

    /**
     * Insert client in the data source
     */
    suspend fun insertClient(client: Client)

    /**
     * Delete client from the data source
     */
    suspend fun deleteClient(client: Client)

    /**
     * Update client in the data source
     */
    suspend fun updateClient(client: Client)
}