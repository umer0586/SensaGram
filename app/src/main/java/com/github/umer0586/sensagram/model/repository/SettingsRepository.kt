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

package com.github.umer0586.sensagram.model.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.umer0586.sensagram.model.DeviceSensor
import com.github.umer0586.sensagram.model.toDeviceSensor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//The delegate will ensure that we have a single instance of DataStore with that name in our application.
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

class SettingsRepository(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    companion object {

        private val KEY_IP_ADDRESS = stringPreferencesKey("IP_ADDRESS")
        private val KEY_PORT_NO = intPreferencesKey("PORT_NO")
        private val KEY_SENSOR_LIST = stringPreferencesKey("SENSOR_LIST")
        private val KEY_SAMPLING_RATE = intPreferencesKey("SAMPLING_RATE")
        private val KEY_STREAM_ON_BOOT = booleanPreferencesKey("STREAM_ON_BOOT")
        private val KEY_GPS_STREAMING = booleanPreferencesKey("GPS_STREAMING")


        const val DEFAULT_IP = "127.0.0.1"
        const val DEFAULT_PORT = 8080
        const val DEFAULT_SAMPLING_RATE = 20000
        const val DEFAULT_STREAM_ON_BOOT = false
        const val DEFAULT_GPS_STREAMING = false


    }



    suspend fun saveIpAddress(ipAddress: String) {

        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_IP_ADDRESS] = ipAddress
        }

    }

    suspend fun savePortNo(portNo: Int) {

        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_PORT_NO] = portNo
        }

    }

    suspend fun saveSensors(sensors: List<DeviceSensor>) {

        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_SENSOR_LIST] = sensors.map { it.stringType }.joinToString(separator = ",")
        }
    }

    suspend fun saveSamplingRate(samplingRate: Int) {

        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_SAMPLING_RATE] = samplingRate
        }
    }

    suspend fun saveStreamOnBoot(streamOnBoot : Boolean){
        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_STREAM_ON_BOOT] = streamOnBoot
        }
    }

    suspend fun saveGPSStreaming(gpsStreaming : Boolean){
        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_GPS_STREAMING] = gpsStreaming
        }
    }

    val ipAddress: Flow<String>
        get() = context.userPreferencesDataStore.data.map { pref ->
            pref[KEY_IP_ADDRESS] ?: DEFAULT_IP
        }

    val portNo: Flow<Int>
        get() = context.userPreferencesDataStore.data.map { pref ->
            pref[KEY_PORT_NO] ?: DEFAULT_PORT
        }

    val selectedSensors: Flow<List<DeviceSensor>>
        get() = context.userPreferencesDataStore.data.map { pref ->
            pref[KEY_SENSOR_LIST]?.split(",")?.mapNotNull { sensorType ->
                sensorManager.getSensorFromStringType(sensorType)?.toDeviceSensor()
            } ?: emptyList()
        }

    val samplingRate: Flow<Int>
        get() = context.userPreferencesDataStore.data.map { pref ->
             pref[KEY_SAMPLING_RATE] ?: DEFAULT_SAMPLING_RATE
        }

    val streamOnBoot : Flow<Boolean>
        get() = context.userPreferencesDataStore.data.map{ pref ->
            pref[KEY_STREAM_ON_BOOT] ?: DEFAULT_STREAM_ON_BOOT
        }

    val gpsStreaming : Flow<Boolean>
        get() = context.userPreferencesDataStore.data.map{ pref ->
            pref[KEY_GPS_STREAMING] ?: DEFAULT_GPS_STREAMING
        }

    private fun SensorManager.getSensorFromStringType(sensorStringType: String): Sensor? {
        return getSensorList(Sensor.TYPE_ALL).firstOrNull {
            it.stringType.equals(
                sensorStringType,
                ignoreCase = true
            )
        }

    }


}