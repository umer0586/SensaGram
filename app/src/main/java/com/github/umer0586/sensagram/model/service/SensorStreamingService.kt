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

package com.github.umer0586.sensagram.model.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.umer0586.sensagram.MainActivity
import com.github.umer0586.sensagram.R
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import com.github.umer0586.sensagram.model.streamer.SensorStreamer
import com.github.umer0586.sensagram.model.streamer.StreamingInfo
import com.github.umer0586.sensagram.model.toSensors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class SensorStreamingService : Service() {


    private var sensorStreamer: SensorStreamer? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var streamingStartedCallBack: ((StreamingInfo) -> Unit)? = null
    private var streamingStoppedCallBack: (() -> Unit)? = null
    private var streamingErrorCallBack : ((Exception) -> Unit)? = null

    var isStreaming: Boolean = false
        get() =  sensorStreamer?.isStreaming ?: false
        private set

    val streamingInfo : StreamingInfo?
        get() = sensorStreamer?.streamingInfo

    companion object {


        private val TAG: String = SensorStreamingService::class.java.getSimpleName()
        const val CHANNEL_ID = "SensGram-Notification-Channel"

        // cannot be zero
        const val ON_GOING_NOTIFICATION_ID = 332


        // Broadcast intent action (published by other app's component) to stop server thread
        // TODO : Add broadcast listener
        // val ACTION_STOP_SERVER = "ACTION_STOP_SERVER_" + StreamingService::class.java.getName()
    }

    fun streamingStateListener(onStart: ((StreamingInfo) -> Unit)? = null, onStop: (() -> Unit)? = null, onError : ((Exception) -> Unit)? = null) {
        streamingStartedCallBack = onStart
        streamingStoppedCallBack = onStop
        streamingErrorCallBack = onError

    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        handleAndroid8andAbove()

        scope.launch {
            startStreaming()
        }

        return START_NOT_STICKY
    }

    private suspend fun startStreaming() {

        sensorStreamer?.let {
            if(it.isStreaming) {
                Log.d(TAG, "startStreaming() : already streaming, returning without starting again")
                return
            }
        }

        val settingsRepository = SettingsRepository(applicationContext)

        sensorStreamer = SensorStreamer(
            context = applicationContext,
            address = settingsRepository.ipAddress.first(),
            portNo = settingsRepository.portNo.first(),
            samplingRate = settingsRepository.samplingRate.first(),
            sensors = settingsRepository.selectedSensors.first().toSensors(applicationContext)
        )

        sensorStreamer?.onStreamingStarted { info ->
            streamingStartedCallBack?.invoke(info)
            val notificationIntent = Intent(applicationContext, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

            val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .apply {
                    setSmallIcon(R.drawable.ic_launcher_foreground)
                    setContentTitle("Sending Sensor Data")
                    setContentText("Sending to ${info.address}:${info.portNo}")
                    setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the intent that will fire when the user taps the notification
                    setContentIntent(pendingIntent) // don't cancel notification when user taps it
                    setAutoCancel(false)
                }


            val notification = notificationBuilder.build()
            startForeground(ON_GOING_NOTIFICATION_ID, notification)
        }

        sensorStreamer?.onStreamingStopped {

            streamingStoppedCallBack?.invoke()
            stopForeground()

        }
        sensorStreamer?.onError {
            streamingErrorCallBack?.invoke(it)
            stopForeground()
        }

        sensorStreamer?.startStreaming()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        sensorStreamer?.stopStreaming()
        stopForeground()
        scope.cancel()

    }

    // Binder given to clients
    private val binder: IBinder = LocalBinder()

    /*
    * For Android 8 and above there is a framework restriction which required service.startForeground()
    * method to be called within five seconds after call to Context.startForegroundService()
    * so make sure we call this method even if we are returning from service.onStartCommand() without calling
    * service.startForeground()
    *
    * */
    private fun handleAndroid8andAbove() {
        val TEMP_NOTIFICATION_ID = 421

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val tempNotification = NotificationCompat.Builder(
                applicationContext, CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(TEMP_NOTIFICATION_ID, tempNotification)
            //stopForeground(true)
            stopForeground()
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createNotificationChannel() called")
            val name: CharSequence = "Sensor-Streamer"
            val description = "Notifications from SensorStreamer"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("DEPRECATION")
    private fun stopForeground() {
        /*
        If the device is running an older version of Android,
        we fallback to stopForeground(true) to remove the service from the foreground and dismiss the ongoing notification.
        Although it shows as deprecated, it should still work as expected on API level 21 (Android 5).
         */

        // for Android 7 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            stopForeground(STOP_FOREGROUND_REMOVE)
        else
        // This method was deprecated in API level 33.
        // Ignore deprecation message as there is no other alternative method for Android 6 and lower
            stopForeground(true)
    }

    fun stopStreaming() {
        sensorStreamer?.stopStreaming()
        // We stop the service's foreground status but keep it running in the background.
        // In other words, we call stopForeground() but not stopSelf().
        stopForeground()

    }

    override fun onBind(intent: Intent) = binder

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {


        // Return this instance of LocalService so clients can call public methods
        val service: SensorStreamingService
            get() = this@SensorStreamingService // Return this instance of LocalService so clients can call public methods

    }
}