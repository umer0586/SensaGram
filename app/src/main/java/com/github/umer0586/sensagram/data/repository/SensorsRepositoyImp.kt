package com.github.umer0586.sensagram.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.github.umer0586.sensagram.data.model.DeviceSensor
import com.github.umer0586.sensagram.data.model.toDeviceSensors

class SensorsRepositoryImp(private val context: Context) : SensorsRepository {

    override fun getAllSensors(): List<DeviceSensor> {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getSensorList(Sensor.TYPE_ALL).filter{ it.reportingMode != Sensor.REPORTING_MODE_ONE_SHOT}.toDeviceSensors()
    }

}