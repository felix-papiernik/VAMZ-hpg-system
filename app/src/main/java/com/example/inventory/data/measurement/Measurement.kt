package com.example.inventory.data.measurement

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.inventory.data.client.Client

/**
 * Entity data class represents a single row in the database.
 */
@Entity(
    tableName = "measurements",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Measurement (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientId: Int,//foreign key
    val date: String,
    val bodyWeightKg: Double,
    val leanMuscleMassKg: Double,
    val bodyFatKg: Double,
    val visceralFat: Double,
    val mineralsKg: Double,
    val metabolicAge: Double,
)