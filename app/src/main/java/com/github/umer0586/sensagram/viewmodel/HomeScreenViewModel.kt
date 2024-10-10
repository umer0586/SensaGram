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

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import com.github.umer0586.sensagram.model.service.SensorStreamingService
import com.github.umer0586.sensagram.model.service.StreamingServiceBindHelper
import com.github.umer0586.sensagram.model.streamer.StreamingInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class HomeScreenUiState(
    val isStreaming: Boolean = false,
    val streamingInfo: StreamingInfo? = null,
    val selectedSensorsCount : Int = 0
)

sealed interface HomeScreenEvent {
    data object OnStartSubmit : HomeScreenEvent
    data object OnStopSubmit : HomeScreenEvent
}


class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()


    private val appContext: Context
        get() = getApplication<Application>().applicationContext


    private val streamingServiceBindHelper = StreamingServiceBindHelper(appContext)
    private val settingsRepository = SettingsRepository(appContext)

    @SuppressLint("StaticFieldLeak")
    private lateinit var sensorStreamingService: SensorStreamingService


    init {


        viewModelScope.launch {
            settingsRepository.selectedSensors.collect{ sensors ->

                _uiState.update {
                    it.copy(
                        selectedSensorsCount = sensors.count()
                    )
                }
            }
        }

        streamingServiceBindHelper.onStreamingServiceConnected { service : SensorStreamingService ->
            Log.d(TAG, "onServiceConnected()")
            sensorStreamingService = service

            _uiState.update {
                it.copy(
                    isStreaming = sensorStreamingService.isStreaming,
                    streamingInfo = sensorStreamingService.streamingInfo
                )
            }

            sensorStreamingService.streamingStateListener(
                onStart = { info ->
                    Log.d(TAG, "onStreamingStarted()")

                    _uiState.update {
                        it.copy(
                            isStreaming = true,
                            streamingInfo = info
                        )
                    }

                },
                onStop = {
                    Log.d(TAG, "onStreamingStopped()")
                    _uiState.update {
                        it.copy(
                            isStreaming = false,
                            streamingInfo = null
                        )
                    }
                },

                onError = {
                    Log.d(TAG, "onStreamingError()")

                    _uiState.update {
                        it.copy(
                            isStreaming = false,
                            streamingInfo = null
                        )
                    }

                    onError?.invoke(it)
                }

            )

        }



        streamingServiceBindHelper.bindToService()

    }

    fun onUiEvent(event : HomeScreenEvent){
        when(event){
            is HomeScreenEvent.OnStartSubmit -> {
                Log.d(TAG, "starting foreground service")
                val intent = Intent(appContext, SensorStreamingService::class.java)
                ContextCompat.startForegroundService(appContext, intent)
            }
            HomeScreenEvent.OnStopSubmit -> {
                Log.d(TAG, "stopping foreground service")
                sensorStreamingService.stopStreaming()
                //applicationContext.stopService(Intent(applicationContext, StreamingService::class.java))
            }
        }
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared()")
        sensorStreamingService.streamingStateListener(onStart = null, onStop = null, onError = null)
        streamingServiceBindHelper.unBindFromService()
    }

    private var onError : ((Exception) -> Unit)? = null
    fun onError(callBack: ((Exception) -> Unit)?){
        onError = callBack
    }

    companion object {
        private val TAG = HomeScreenViewModel::class.java.simpleName
    }
}

