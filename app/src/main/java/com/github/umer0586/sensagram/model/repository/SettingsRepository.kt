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
import android.util.Log
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

//The delegate will ensure that we have a single instance of DataStore with that name in our application.
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

class SettingsRepository(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    companion object {

        private val TAG = SettingsRepository::class.java.simpleName

        private val KEY_IP_ADDRESS = stringPreferencesKey("IP_ADDRESS")
        private val KEY_PORT_NO = intPreferencesKey("PORT_NO")
        private val KEY_SENSOR_LIST = stringPreferencesKey("SENSOR_LIST")
        private val KEY_SAMPLING_RATE = intPreferencesKey("SAMPLING_RATE")
        private val KEY_STREAM_ON_BOOT = booleanPreferencesKey("STREAM_ON_BOOT")


        const val DEFAULT_IP = "127.0.0.1"
        const val DEFAULT_PORT = 8080
        const val DEFAULT_SAMPLING_RATE = 20000
        const val DEFAULT_STREAM_ON_BOOT = true

        var ipAddress = DEFAULT_IP
            private set

        var portNo = DEFAULT_PORT
            private set

        var sensors = emptyList<DeviceSensor>()
            private set

        var samplingRate = DEFAULT_SAMPLING_RATE
            private set

        var streamOnBoot = DEFAULT_STREAM_ON_BOOT
            private set

    }

    suspend fun collect() {
        ipAddress = ipAddressFlow.first()
        portNo = portNoFlow.first()
        sensors = sensorsFlow.first()
        samplingRate = samplingRateFlow.first()
        streamOnBoot = streamOnBootFlow.first()

        Log.d(TAG, "Setting loaded")
    }


    suspend fun saveIpAddress(ipAddress: String) {
        Companion.ipAddress = ipAddress
        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_IP_ADDRESS] = ipAddress
        }

    }

    suspend fun savePortNo(portNo: Int) {
        Companion.portNo = portNo
        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_PORT_NO] = portNo
        }

    }

    suspend fun saveSensors(sensors: List<DeviceSensor>) {
        Companion.sensors = sensors
        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_SENSOR_LIST] = sensors.map { it.stringType }.joinToString(separator = ",")
        }
    }

    suspend fun saveSamplingRate(samplingRate: Int) {
        Companion.samplingRate = samplingRate
        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_SAMPLING_RATE] = samplingRate
        }
    }

    suspend fun saveStreamOnBoot(streamOnBoot: Boolean) {
        Companion.streamOnBoot = streamOnBoot
        context.userPreferencesDataStore.edit { pref ->
            pref[KEY_STREAM_ON_BOOT] = streamOnBoot
        }
    }

    val ipAddressFlow: Flow<String>
        get() = context.userPreferencesDataStore.data.map { pref ->
            pref[KEY_IP_ADDRESS] ?: DEFAULT_IP
        }

    val portNoFlow: Flow<Int>
        get() = context.userPreferencesDataStore.data.map { pref ->
            pref[KEY_PORT_NO] ?: DEFAULT_PORT
        }

    val sensorsFlow: Flow<List<DeviceSensor>>
        get() = context.userPreferencesDataStore.data.map { pref ->
            pref[KEY_SENSOR_LIST]?.split(",")?.mapNotNull { sensorType ->
                sensorManager.getSensorFromStringType(sensorType)?.toDeviceSensor()
            } ?: emptyList()
        }

    val samplingRateFlow: Flow<Int>
        get() = context.userPreferencesDataStore.data.map { pref ->
             pref[KEY_SAMPLING_RATE] ?: DEFAULT_SAMPLING_RATE
        }

    val streamOnBootFlow: Flow<Boolean>
        get() = context.userPreferencesDataStore.data.map { pref ->
            pref[KEY_STREAM_ON_BOOT] ?: DEFAULT_STREAM_ON_BOOT
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