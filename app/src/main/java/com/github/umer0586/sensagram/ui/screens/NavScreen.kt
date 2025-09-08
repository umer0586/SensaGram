/*
 *     This file is a part of SensaGram (https://github.com/umer0586/SensaGram)
 *     Copyright (C) 2024 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     SensaGram is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SensaGram is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SensaGram.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.github.umer0586.sensagram.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.umer0586.sensagram.data.repository.SensorsRepositoryImp
import com.github.umer0586.sensagram.data.repository.SettingsRepositoryImp
import com.github.umer0586.sensagram.data.util.LocationPermissionCheckerImp
import com.github.umer0586.sensagram.ui.components.AppInfo
import com.github.umer0586.sensagram.ui.screens.home.HomeScreen
import com.github.umer0586.sensagram.ui.screens.sensors.SensorsScreen
import com.github.umer0586.sensagram.ui.screens.sensors.SensorsScreenViewModel
import com.github.umer0586.sensagram.ui.screens.settings.SettingsScreen
import com.github.umer0586.sensagram.ui.screens.settings.SettingsScreenViewModel
import kotlinx.coroutines.launch


sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home : NavItem(route = "/Home", label = "Home", icon = Icons.Filled.Home)
    data object Sensors : NavItem("/Sensors", "Sensors", icon = Icons.AutoMirrored.Filled.List)
    data object Settings : NavItem("/Settings", "Settings", icon = Icons.Filled.Settings)
}

private val navItems = listOf(
    NavItem.Home,
    NavItem.Settings,
    NavItem.Sensors
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavScreen() {

    val navController = rememberNavController()
    var showInfoBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .shadow(
                        elevation = 5.dp,
                        spotColor = Color.DarkGray,
                    ),
                title = { Text("Sensagram") },
                actions = {
                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable { showInfoBottomSheet = true },
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                }
            )
        },

        bottomBar = {

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar {
                navItems.forEach { navItem ->
                    NavigationBarItem(
                        selected = navItem.route == currentRoute,
                        alwaysShowLabel = true,
                        label = { Text(navItem.label) },
                        onClick = {
                            navController.navigate(navItem.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }

                        },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = null
                            )
                        })
                }
            }
        },
    ) { innerPadding ->
        val applicationContext = LocalContext.current.applicationContext
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(
                route = NavItem.Home.route,
            ) {
                HomeScreen(
                    onStreamingError = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Streaming Error")
                        }
                    })
            }

            composable(
                route = NavItem.Sensors.route,
            ) {
                SensorsScreen(
                    viewModel = viewModel {
                        SensorsScreenViewModel(
                            settingsRepository = SettingsRepositoryImp(applicationContext),
                            sensorsRepository = SensorsRepositoryImp(applicationContext),
                            locationPermissionChecker = LocationPermissionCheckerImp(
                                applicationContext
                            )
                        )
                    }
                )
            }


            composable(
                route = NavItem.Settings.route,
            ) {
                SettingsScreen(
                    viewModel = viewModel {
                        SettingsScreenViewModel(SettingsRepositoryImp(applicationContext))
                    }
                )
            }

        }

    }

    if (showInfoBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showInfoBottomSheet = false }
        ) {

            val context = LocalContext.current
            val versionName = try {
                context.packageManager
                    .getPackageInfo(context.packageName, 0).versionName

            } catch (e: PackageManager.NameNotFoundException) {
                "Unknown"
            }

            AppInfo(
                modifier = Modifier.fillMaxWidth(),
                versionName = versionName ?: "Unknown",
                onEmailClick = { email ->
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.setData(Uri.parse("mailto:")) // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, email)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                    context.startActivity(intent)
                },
                onSourceCodeClick = { sourceCodeLink ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    if(intent.resolveActivity(context.packageManager) != null){
                        intent.data = Uri.parse(sourceCodeLink)
                        context.startActivity(Intent.createChooser(intent,"Select Browser"))
                    } else {
                        Toast.makeText(context,"No browser found",Toast.LENGTH_SHORT).show()
                    }

                }
            )
        }
    }


}




