/*
 *     This file is a part of SensaGram (https://www.github.com/umer0586/SensaGram)
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

package com.github.umer0586.sensagram.data.worker

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

/*
 * This worker enables Wi-Fi on devices running Android 9 (API level 28) or lower.
 * On Android 10 (API level 29) and higher, this worker is ineffective and will always fail.
 * It is scheduled to run at boot time.
 */

class WifiEnablerWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val TAG = WifiEnablerWorker::class.java.name

    @Suppress("DEPRECATION")
    override suspend fun doWork(): Result {
        Log.i(TAG,"worker started")
        // Get the WifiManager system service
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager


        // Fail the worker if the Android version is Q or higher since enabling Wi-Fi programmatically
        // is restricted starting from Android Q (API level 29).
        // We never enqueue this worker on Android Q or higher at boot time.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            return Result.failure()

        // Enable Wi-Fi if it is not already enabled
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!wifiManager.isWifiEnabled) {
                Log.i(TAG, "enabling wifi...")
                delay(2000L)
                wifiManager.setWifiEnabled(true)
                Log.i(TAG, "wifi enabled : ${wifiManager.isWifiEnabled}")
            }
        }

        return if (wifiManager.isWifiEnabled) Result.success() else Result.failure()
    }
}