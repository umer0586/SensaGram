package com.github.umer0586.sensagram.model.repository

import com.github.umer0586.sensagram.model.data.DeviceSensor

interface SensorsRepository {
    fun getAllSensors(): List<DeviceSensor>
}