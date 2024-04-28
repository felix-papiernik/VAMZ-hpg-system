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

package com.example.inventory.data

import android.content.Context
import com.example.inventory.data.client.ClientDatabase
import com.example.inventory.data.client.ClientsRepository
import com.example.inventory.data.client.OfflineClientsRepository
import com.example.inventory.data.inventory.InventoryDatabase
import com.example.inventory.data.inventory.ItemsRepository
import com.example.inventory.data.inventory.OfflineItemsRepository
import com.example.inventory.data.measurement.HpgDatabase
import com.example.inventory.data.measurement.HpgRepository
import com.example.inventory.data.measurement.OfflineHpgRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val itemsRepository: ItemsRepository
    val clientsRepository: ClientsRepository
    val hpgRepository: HpgRepository
}

/**
 * [AppContainer] implementation that provides instances of repositories.
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
    }
    /**
     * Implementation for [ClientsRepository]
     */
    override val clientsRepository: ClientsRepository by lazy {
        OfflineClientsRepository(HpgDatabase.getDatabase(context).clientDao())
    }

    /**
     * Implementation for [HpgRepository]
     */
    override val hpgRepository: HpgRepository by lazy {
        OfflineHpgRepository(HpgDatabase.getDatabase(context).measurementDao())
    }

}
