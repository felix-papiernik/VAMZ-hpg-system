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

package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.ui.client.details.ClientDetailsDestination
import com.example.inventory.ui.client.details.ClientDetailsScreen
import com.example.inventory.ui.client.edit.ClientEditDestination
import com.example.inventory.ui.client.edit.ClientEditScreen
import com.example.inventory.ui.client.entry.ClientEntryDestination
import com.example.inventory.ui.client.entry.ClientEntryScreen
import com.example.inventory.ui.home.HomeDestination
import com.example.inventory.ui.home.HomeScreen
import com.example.inventory.ui.measurements.details.MeasurementDetailsDestination
import com.example.inventory.ui.measurements.details.MeasurementDetailsScreen
import com.example.inventory.ui.measurements.edit.MeasurementEditDestination
import com.example.inventory.ui.measurements.edit.MeasurementEditScreen
import com.example.inventory.ui.measurements.entry.MeasurementEntryDestination
import com.example.inventory.ui.measurements.entry.MeasurementEntryScreen

//home
//client_entry
/**
 * Provides Navigation graph for the application.
 */
@Composable
fun HpgNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToClientEntry = { navController.navigate(ClientEntryDestination.route) },
                navigateToClientUpdate = {
                    navController.navigate("${ClientDetailsDestination.route}/${it}")
                },
            )
        }
        composable(route = ClientEntryDestination.route) {
            ClientEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = ClientDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ClientDetailsDestination.clientIdArg) {
                type = NavType.IntType
            })
        ) {
            ClientDetailsScreen(
                navigateToEditClientPersonalInformation = { navController.navigate("${ClientEditDestination.route}/$it") },
                navigateToMeasurementEntry = { navController.navigate("${MeasurementEntryDestination.route}/$it") },
                navigateToMeasurementUpdate = { navController.navigate("${MeasurementDetailsDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = MeasurementEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(MeasurementEntryDestination.clientIdArg) {
                type = NavType.IntType
            })
        ) {backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt(MeasurementEntryDestination.clientIdArg)
            MeasurementEntryScreen(
                clientId = clientId ?: -1,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = MeasurementDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(MeasurementDetailsDestination.measurementIdArg) {
                type = NavType.IntType
            })
        ) {backStackEntry ->
            val measurementId = backStackEntry.arguments?.getInt(MeasurementDetailsDestination.measurementIdArg)
            MeasurementDetailsScreen(
                navigateToEditMeasurement = {navController.navigate("${MeasurementEditDestination.route}/${measurementId}")},
                navigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = MeasurementEditDestination.routeWithArgs,
            arguments = listOf(navArgument(MeasurementEditDestination.measurementIdArg) {
                type = NavType.IntType
            })
        ) {
            MeasurementEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = ClientEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ClientEditDestination.clientIdArg) {
                type = NavType.IntType
            })
        ) {
            ClientEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
