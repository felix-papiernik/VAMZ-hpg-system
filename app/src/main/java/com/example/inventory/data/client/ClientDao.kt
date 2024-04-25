package com.example.inventory.data.client

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("select * from clients")
    fun getAllClients(): Flow<List<Client>>

    @Query("SELECT * from clients WHERE id = :id")
    fun getClient(id: Int): Flow<Client>

    //TODO DECIDE
    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Client into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(client: Client)

    @Update
    suspend fun update(client: Client)

    @Delete
    suspend fun delete(client: Client)
}