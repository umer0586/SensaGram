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

package com.github.umer0586.sensagram.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import com.github.umer0586.sensagram.model.workers.StreamStarterWorker
import com.github.umer0586.sensagram.model.workers.WifiEnablerWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class BootCompleteReceiver : BroadcastReceiver() {

    private val TAG = BootCompleteReceiver::class.java.simpleName
    private val scope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(TAG, "onReceive() : Intent : ${intent?.action}")


        val job = scope.launch {

            val streamOnBoot = SettingsRepository(context).streamOnBoot.first()

            val wifiConstraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // Ensures Wi-Fi connection
                .build()

            val streamStartWorkRequest = OneTimeWorkRequestBuilder<StreamStarterWorker>()
                .setConstraints(wifiConstraint)
                .build()

            val wifiEnablerWorkRequest = OneTimeWorkRequestBuilder<WifiEnablerWorker>()
                .build()


            // Check if the intent action is BOOT_COMPLETED and if the user has enabled streaming on boot
            if (intent != null && intent.action == Intent.ACTION_BOOT_COMPLETED && streamOnBoot) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                    // Chain the work requests together : wifiEnablerWorkRequest -> streamStartWorkRequest
                    WorkManager.getInstance(context.applicationContext)
                        .beginWith(wifiEnablerWorkRequest) // Enable Wi-Fi first
                        .then(streamStartWorkRequest) // Then start the stream
                        .enqueue()

                } else {


                    // On Android 10 (API level 29) and higher, apps have no control over enabling Wi-Fi.
                    // Therefore, we skip the Wi-Fi enabling step and directly enqueue the stream starting work request.
                    // To ensure automatic Wi-Fi connection on boot (for Android 10 and higher), the user must manually enable Wi-Fi before shutting down or restarting the device.
                    // This allows the system to remember the Wi-Fi state and enable it automatically during the next boot.

                    WorkManager.getInstance(context.applicationContext)
                        .enqueue(streamStartWorkRequest)

                }


            }

        }

        job.invokeOnCompletion {
            Log.d(TAG, "Job Completed : cancelling scope")
            scope.cancel()
        }

    }

}