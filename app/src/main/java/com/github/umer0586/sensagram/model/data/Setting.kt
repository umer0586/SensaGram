package com.github.umer0586.sensagram.model.data

const val DEFAULT_IP = "127.0.0.1"
const val DEFAULT_PORT = 8080
const val DEFAULT_SAMPLING_RATE = 20000
const val DEFAULT_STREAM_ON_BOOT = false
const val DEFAULT_GPS_STREAMING = false

data class Setting(
    val ipAddress : String = DEFAULT_IP,
    val portNo : Int = DEFAULT_PORT,
    val selectedSensors : List<DeviceSensor> = emptyList(),
    val samplingRate : Int = DEFAULT_SAMPLING_RATE,
    val streamOnBoot : Boolean = DEFAULT_STREAM_ON_BOOT,
    val gpsStreaming : Boolean = DEFAULT_GPS_STREAMING,
)