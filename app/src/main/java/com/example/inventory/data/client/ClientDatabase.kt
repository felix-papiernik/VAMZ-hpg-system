package com.example.inventory.data.client

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Client::class], version = 1, exportSchema = false)
abstract class ClientDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao

    companion object {
        @Volatile
        private var Instance: ClientDatabase? = null

        fun getDatabase(context: Context): ClientDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ClientDatabase::class.java, "client_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}