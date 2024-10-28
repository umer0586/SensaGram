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

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.sensagram.model.data.DeviceSensor
import com.github.umer0586.sensagram.model.repository.SensorsRepository
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import com.github.umer0586.sensagram.model.util.LocationPermissionChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SensorScreenUiState(
    val selectedSensors: SnapshotStateList<DeviceSensor>,
    val gpsChecked : Boolean = false
)

sealed class SensorScreenEvent {
    data class OnSensorSelected(val sensor: DeviceSensor) : SensorScreenEvent()
    data class OnSensorDeselected(val sensor: DeviceSensor) : SensorScreenEvent()
    data class OnGPSCheckedChange(val checked : Boolean) : SensorScreenEvent()
}

class SensorsScreenViewModel(
    private val settingsRepository: SettingsRepository,
    private val sensorsRepository: SensorsRepository,
    private val locationPermissionChecker: LocationPermissionChecker
) : ViewModel() {

    private val TAG = SensorsScreenViewModel::class.java.simpleName


    private val _uiState = MutableStateFlow(
        SensorScreenUiState(
            selectedSensors = mutableStateListOf()
        )
    )

    val uiState = _uiState.asStateFlow()

    val availableSensors: List<DeviceSensor>
        get() = sensorsRepository.getAllSensors()

    init {

        viewModelScope.launch {

            // Load saved selected sensors list from persistent storage
            // this is important as a user might have closed the app and re-opened it (with or without streaming)
            _uiState.value.selectedSensors.apply {
                clear()
                addAll(settingsRepository.setting.first().selectedSensors)
            }

            if(!locationPermissionChecker.isLocationPermissionGranted()){
                val oldSettings = settingsRepository.setting.first()
                settingsRepository.saveSetting( oldSettings.copy(gpsStreaming = false) )
            }

        }

        viewModelScope.launch {
            settingsRepository.setting.collect{ settings ->
                _uiState.update {
                    it.copy(gpsChecked = settings.gpsStreaming)
                }
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

            is SensorScreenEvent.OnGPSCheckedChange -> {
                _uiState.update {
                    it.copy(gpsChecked = event.checked)
                }
                viewModelScope.launch {
                    val oldSettings = settingsRepository.setting.first()
                    settingsRepository.saveSetting(
                        oldSettings.copy(gpsStreaming = event.checked)
                    )
                }
            }
        }
    }


    private fun saveSensors(){

        viewModelScope.launch {
            val oldSettings = settingsRepository.setting.first()
            settingsRepository.saveSetting(
                oldSettings.copy(selectedSensors = _uiState.value.selectedSensors)
            )
        }
    }



}