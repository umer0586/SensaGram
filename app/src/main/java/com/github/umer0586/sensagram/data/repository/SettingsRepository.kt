package com.github.umer0586.sensagram.data.repository

import com.github.umer0586.sensagram.data.model.Setting
import kotlinx.coroutines.flow.Flow


interface SettingsRepository {
    suspend fun saveSetting(setting: Setting)
    val setting : Flow<Setting>
}

