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

package com.github.umer0586.sensagram.model.streamer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import com.github.umer0586.sensagram.model.DeviceSensor
import com.github.umer0586.sensagram.model.toDeviceSensors
import com.github.umer0586.sensagram.model.util.JsonUtil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

data class StreamingInfo(
    val address: String,
    val portNo: Int,
    val samplingRate: Int,
    val sensors: List<DeviceSensor>
)

class SensorStreamer(
    val context: Context,
    val address: String,
    val portNo: Int,
    val samplingRate : Int,
    private var sensors: List<Sensor>
) : SensorEventListener {


    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var handlerThread: HandlerThread = HandlerThread("Handler Thread")
    private lateinit var handler: Handler

    var isStreaming = false
        private set

    val streamingInfo: StreamingInfo?
        get() = when (isStreaming) {
            true -> StreamingInfo(
                address = address,
                portNo = portNo,
                samplingRate = samplingRate,
                sensors = sensors.toDeviceSensors()
            )
            false -> null
        }

    private val datagramSocket: DatagramSocket = DatagramSocket()


    private var onStart: ((StreamingInfo) -> Unit)? = null
    private var onStop: (() -> Unit)? = null
    private var onError :((Exception) -> Unit)? = null

    fun startStreaming() {

         if (isStreaming)
            return

        handlerThread.start()
        handler = Handler(handlerThread.looper)

        sensors.forEach {
            sensorManager.registerListener(this, it, samplingRate, handler)
        }
        onStart?.invoke(
            StreamingInfo(
                address = address,
                portNo = portNo,
                samplingRate = samplingRate,
                sensors = sensors.toDeviceSensors()
            )
        )
        isStreaming = true
    }

    fun stopStreaming() {
        sensors.forEach {
            sensorManager.unregisterListener(this, it)
        }

        onStop?.invoke()

        if (!datagramSocket.isClosed)
            datagramSocket.close()

        isStreaming = false
    }

    fun onStreamingStarted(callBack: ((StreamingInfo) -> Unit)?) {
        onStart = callBack
    }

    fun onStreamingStopped(callBack: (() -> Unit)?) {
        onStop = callBack
    }

    fun onError(callBack: ((Exception) -> Unit)?){
        onError = callBack
    }

    fun changeSensors(newSensors : List<Sensor>){

        if(!isStreaming)
            return

        sensors.forEach {
            sensorManager.unregisterListener(this, it)
        }

        newSensors.forEach{
            sensorManager.registerListener(this, it, samplingRate, handler)
        }

        // Avoid declaring 'sensors' property as a mutable list and then doing 'sensors.clear()' followed by 'sensors.addAll(newSensors)'.
        // This can throw a ConcurrentModificationException, leading to app crashes.
        // The crash occurs because the list is being modified while it's being iterated over,
        // which happens when the user frequently selects and deselects sensors from the list during streaming.
        //
        // One solution is to use an iterator, as explained here: https://stackoverflow.com/questions/50032000/how-to-avoid-concurrentmodificationexception-kotlin
        // Alternatively, declaring 'sensors' as a 'var' and reassigning it (tested and works without crashes) is a simpler and effective fix.

        sensors = newSensors

    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {

        val sensorData = mutableMapOf<String,Any>()
        sensorData["timestamp"] = sensorEvent.timestamp
        sensorData["values"] = sensorEvent.values.toList()
        sensorData["type"] = sensorEvent.sensor.stringType
        val jsonString = JsonUtil.toJSON(sensorData)


        val packet = DatagramPacket(
            jsonString.toByteArray(),
            jsonString.length,
            InetAddress.getByName(address),
            portNo
        )

        try{
            datagramSocket.send(packet)
        }catch (e: Exception){
            onError?.invoke(e)
            stopStreaming()
        }
    }

    override fun onAccuracyChanged(p0: Sensor, p1: Int) {
        //TODO("Not yet implemented")
    }

}