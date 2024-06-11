/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inventory.HpgApplication
import com.example.inventory.ui.client.details.ClientDetailsViewModel
import com.example.inventory.ui.client.edit.ClientEditViewModel
import com.example.inventory.ui.client.entry.ClientEntryViewModel
import com.example.inventory.ui.home.HomeViewModel
import com.example.inventory.ui.measurements.details.MeasurementDetailsViewModel
import com.example.inventory.ui.measurements.edit.MeasurementEditViewModel
import com.example.inventory.ui.measurements.entry.MeasurementEntryViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire HPG app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(inventoryApplication().container.clientsRepository)
        }
        // Initializer for ClientEntryViewModel
        initializer {
            ClientEntryViewModel(inventoryApplication().container.clientsRepository)
        }
        // Initializer for ClientDetailsViewModel
        initializer {
            ClientDetailsViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.clientsRepository,
                inventoryApplication().container.measurementsRepository,
            )
        }
        // Initializer for ClientEditViewModel
        initializer {
            ClientEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.clientsRepository
            )
        }
        // Initializer for MeasurementEntryViewModel
        initializer {
            MeasurementEntryViewModel(
                inventoryApplication().container.measurementsRepository
            )
        }
        // Initializer for MeasurementDetailsViewModel
        initializer {
            MeasurementDetailsViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.measurementsRepository
            )
        }
        // Initializer for ClientEditViewModel
        initializer {
            MeasurementEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.measurementsRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [HpgApplication].
 */
fun CreationExtras.inventoryApplication(): HpgApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as HpgApplication)
