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

package com.github.umer0586.sensagram.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.sensagram.model.DeviceSensor
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import com.github.umer0586.sensagram.model.toDeviceSensors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SensorScreenUiState(
    val selectedSensors: SnapshotStateList<DeviceSensor>,
)

sealed class SensorScreenEvent {
    data class OnSensorSelected(val sensor: DeviceSensor) : SensorScreenEvent()
    data class OnSensorDeselected(val sensor: DeviceSensor) : SensorScreenEvent()
}

class SensorsScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = SensorsScreenViewModel::class.java.simpleName

    private val appContext: Context
        get() = getApplication<Application>().applicationContext

    private val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val settingsRepository = SettingsRepository(appContext)

    private val _uiState = MutableStateFlow(
        SensorScreenUiState(
            selectedSensors = mutableStateListOf()
        )
    )

    val uiState = _uiState.asStateFlow()

    val availableSensors: List<DeviceSensor>
        get() = sensorManager.getSensorList(Sensor.TYPE_ALL).toDeviceSensors()

    init {

        viewModelScope.launch {

            // Load saved selected sensors list from persistent storage
            // this is important as a user might have closed the app and re-opened it (with or without streaming)
            _uiState.value.selectedSensors.apply {
                clear()
                addAll(settingsRepository.selectedSensors.first())
            }

        }

    }

    fun onUiEvent(event: SensorScreenEvent){
        when(event){
            is SensorScreenEvent.OnSensorSelected -> {
                _uiState.value.selectedSensors.add(event.sensor)
                saveSensors()
            }
            is SensorScreenEvent.OnSensorDeselected -> {
                _uiState.value.selectedSensors.apply {
                    if (contains(event.sensor))
                        remove(event.sensor)
                }
                saveSensors()
            }
        }
    }


    private fun saveSensors(){

        viewModelScope.launch {
            settingsRepository.saveSensors(_uiState.value.selectedSensors)
        }
    }

}