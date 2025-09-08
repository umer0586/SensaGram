package com.github.umer0586.sensagram.data.repository

import com.github.umer0586.sensagram.data.model.DeviceSensor

interface SensorsRepository {
    fun getAllSensors(): List<DeviceSensor>
}