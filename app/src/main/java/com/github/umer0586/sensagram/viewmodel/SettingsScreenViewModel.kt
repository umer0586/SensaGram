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
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SettingsScreenUiState(
    val ipAddress: String = SettingsRepository.DEFAULT_IP,
    val isIpAddressValid: Boolean = true,
    val savedIpAddress: String = SettingsRepository.DEFAULT_IP,
    val portNo: Int = SettingsRepository.DEFAULT_PORT,
    val savedPortNo: Int = SettingsRepository.DEFAULT_PORT,
    val isPortNoValid : Boolean = true,
    val samplingRate: Int = SettingsRepository.DEFAULT_SAMPLING_RATE,
    val savedSamplingRate: Int = SettingsRepository.DEFAULT_SAMPLING_RATE,
    val isSamplingRateValid: Boolean = true
)

sealed class SettingScreenEvent {
    data class OnPortNoChange(val portNo: Int) : SettingScreenEvent()
    data class OnIpAddressChange(val ipAddress: String) : SettingScreenEvent()
    data class OnSamplingRateChange(val samplingRate: Int) : SettingScreenEvent()
    data class OnSaveIpAddress(val ipAddress: String) : SettingScreenEvent()
    data class OnSavePortNo(val portNo: Int) : SettingScreenEvent()
    data class OnSaveSamplingRate(val samplingRate: Int) : SettingScreenEvent()
}

class SettingsScreenViewModel(application: Application) : AndroidViewModel(application) {


    private val _uiState = MutableStateFlow(SettingsScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val TAG: String = SettingsScreenViewModel::class.java.getSimpleName()

    companion object {
        private val IPV4_REGEX =
            "^(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))\$".toRegex()
    }

    private val appContext: Context
        get() = getApplication<Application>().applicationContext

    private val settingsRepository = SettingsRepository(appContext)

    init {

        viewModelScope.launch {

            // User should be presented with the last saved ipAddress in the EditTextPreference
            _uiState.update {
                it.copy(ipAddress = settingsRepository.ipAddress.first())
            }

            settingsRepository.ipAddress.collect { address ->
                Log.d(TAG,"collected ipAddress : $address")
                _uiState.update {
                    it.copy(
                        savedIpAddress = address
                    )
                }
            }

        }


        viewModelScope.launch {

            // User should be presented with the last saved portNo in the EditTextPreference
            _uiState.update {
                it.copy(portNo = settingsRepository.portNo.first())
            }

            settingsRepository.portNo.collect { portNo ->
                Log.d(TAG,"collected portNo : $portNo")
                _uiState.update {
                    it.copy(
                        savedPortNo = portNo
                    )
                }
            }

        }


        viewModelScope.launch {

            // User should be presented with the last saved samplingRate in the EditTextPreference
            _uiState.update {
                it.copy(samplingRate = settingsRepository.samplingRate.first())
            }
            settingsRepository.samplingRate.collect { rate ->
                Log.d(TAG,"collected samplingRate : $rate")
                _uiState.update {
                    it.copy(
                        savedSamplingRate = rate
                    )
                }
            }

        }

    }

    fun onUiEvent(event : SettingScreenEvent){
        when(event){
            is SettingScreenEvent.OnIpAddressChange -> onIpAddressChange(event.ipAddress)
            is SettingScreenEvent.OnPortNoChange -> onPortNoChange(event.portNo)
            is SettingScreenEvent.OnSamplingRateChange -> onSamplingRateChange(event.samplingRate)
            is SettingScreenEvent.OnSaveIpAddress -> saveIpAddress(event.ipAddress)
            is SettingScreenEvent.OnSavePortNo -> savePortNo(event.portNo)
            is SettingScreenEvent.OnSaveSamplingRate -> saveSamplingRate(event.samplingRate)
        }
    }


    private fun onIpAddressChange(ipAddress: String) {
        _uiState.update {
            it.copy(
                ipAddress = ipAddress,
                isIpAddressValid = IPV4_REGEX.matches(ipAddress)
            )
        }
    }

    private fun onPortNoChange(portNo: Int) {
        _uiState.update {
            it.copy(
                portNo = portNo,
                isPortNoValid = portNo in 0..65534
            )
        }
    }

    private fun onSamplingRateChange(samplingRate: Int) {
        _uiState.update {
            it.copy(
                samplingRate = samplingRate,
                isSamplingRateValid = samplingRate in 0..200000
            )
        }
    }

    private fun saveIpAddress(ipAddress: String) {
        viewModelScope.launch {
            settingsRepository.saveIpAddress(ipAddress)
        }
    }


    private fun savePortNo(portNo: Int) {
        viewModelScope.launch {
            settingsRepository.savePortNo(portNo)
        }
    }

    private fun saveSamplingRate(samplingRate: Int) {
        viewModelScope.launch {
            settingsRepository.saveSamplingRate(samplingRate)
        }
    }


}

