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
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.umer0586.sensagram.data.service.SensorStreamingService

// A worker to start the SensorStreamingService when the device is connected to Wi-Fi
class StreamStarterWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val TAG = StreamStarterWorker::class.java.name

    override fun doWork(): Result {

            Log.i(TAG, "Starting Sensor Streaming service")
            val intent = Intent(applicationContext, SensorStreamingService::class.java)
            ContextCompat.startForegroundService(applicationContext, intent)

            return Result.success()

    }

}
