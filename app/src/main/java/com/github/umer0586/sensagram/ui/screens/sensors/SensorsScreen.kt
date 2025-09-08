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

package com.github.umer0586.sensagram.ui.screens.sensors


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.sensagram.data.model.DeviceSensor
import com.github.umer0586.sensagram.data.model.fakeSensors
import com.github.umer0586.sensagram.ui.components.SensorsList
import com.github.umer0586.sensagram.ui.theme.SensaGramTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SensorsScreen(viewModel: SensorsScreenViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    SensorScreenContent(
        sensors = viewModel.availableSensors,
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun SensorScreenContent(
    sensors : List<DeviceSensor>,
    uiState: SensorScreenUiState,
    onUiEvent: (SensorScreenEvent) -> Unit,
    // Don't declare PermissionState as local variable as we can't use it in preview
    locationPermissionState: PermissionState? = null
){

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    // Reference to sensor that was tapped by user
    var tappedSensor by remember { mutableStateOf(Any()) }
    var showPermissionAlert by remember { mutableStateOf(false) }



    SensorsList(
        modifier = Modifier.padding(10.dp),
        sensors = sensors,
        selectedSensors = uiState.selectedSensors,
        onItemCheckedChange = { sensor, checkState ->
            if(checkState)
                onUiEvent(SensorScreenEvent.OnSensorSelected(sensor))
            else
                onUiEvent(SensorScreenEvent.OnSensorDeselected(sensor))
        },
        onSensorItemTap = {
            tappedSensor = it

            scope.launch {
                showBottomSheet = true
                sheetState.show()
            }
        },
        gpsChecked = uiState.gpsChecked,
        onGPSCheckedChange = { checked ->
            locationPermissionState?.let { permissionState ->
                if(checked){

                    if(permissionState.status.isGranted)
                        onUiEvent(SensorScreenEvent.OnGPSCheckedChange(checked))
                    // If the user has denied the permission previously
                    // but did not select "Don't ask again,"
                    // we can request the permission again
                    else if (permissionState.status.shouldShowRationale)
                        permissionState.launchPermissionRequest()
                    // Permission has been denied permanently
                    else if (!permissionState.status.isGranted)
                        showPermissionAlert = true

                    permissionState.launchPermissionRequest()

                }else{
                    onUiEvent(SensorScreenEvent.OnGPSCheckedChange(checked))
                }

            }

        }
    )


    if (showPermissionAlert) {
        val context = LocalContext.current
        AlertDialog(
            title = {Text("Permission Required")},
            text = {Text("Please grant location permission to enable GPS streaming")},
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        context.startActivity(intent)
                    }
                ) { Text("Open Settings")}
            },
            onDismissRequest = {
                showPermissionAlert = false
            },
        )
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            }
        ) {
            val sensor = tappedSensor as DeviceSensor

            LazyColumn(modifier = Modifier.padding(20.dp)) {
                items(items = sensor.detail().toList()) {
                    Text("${it.first} : ${it.second}")
                }
            }
        }
    }

}



private fun DeviceSensor.detail() = mapOf(
    "Name" to name,
    "Maximum Range" to maximumRange,
    "Reporting Mode" to when (reportingMode) {
        0 -> "Continuous"
        1 -> "On Change"
        3 -> "Special Trigger"
        else -> "Unknown"
    },
    "Maximum Delay" to "${maxDelay}µs",
    "Minimum Delay" to "${minDelay}µs",
    "Vendor" to vendor,
    "Power" to "${power}mA",
    "Wake Up Sensor" to isWakeUpSensor
)

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SensorScreenContentPreview(){
    SensaGramTheme {
        SensorScreenContent(
            sensors = fakeSensors,
            uiState = SensorScreenUiState(
                selectedSensors = mutableStateListOf(fakeSensors[3])
            ),
            onUiEvent = {}
        )
    }
}


