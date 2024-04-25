package com.example.inventory.data.client

import kotlinx.coroutines.flow.Flow

class OfflineClientsRepository(private val clientDao: ClientDao) : ClientsRepository{
    override fun getAllClientsStream(): Flow<List<Client>> = clientDao.getAllClients()

    override fun getClientStream(id: Int): Flow<Client?> = clientDao.getClient(id)

    override suspend fun insertClient(client: Client) = clientDao.insert(client)

    override suspend fun deleteClient(client: Client) = clientDao.delete(client)

    override suspend fun updateClient(client: Client) = clientDao.update(client)


}