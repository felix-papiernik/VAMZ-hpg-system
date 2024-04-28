package com.example.inventory.data.measurement

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventory.data.client.Client
import com.example.inventory.data.client.ClientDao

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Measurement::class, Client::class], version = 3, exportSchema = false)
abstract class HpgDatabase : RoomDatabase() {

    abstract fun measurementDao(): MeasurementDao
    abstract fun clientDao(): ClientDao

    companion object {
        @Volatile
        private var Instance: HpgDatabase? = null

        fun getDatabase(context: Context): HpgDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, HpgDatabase::class.java, "hpg_database")
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