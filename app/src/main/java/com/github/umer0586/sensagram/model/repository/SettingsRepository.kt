package com.github.umer0586.sensagram.model.repository

import com.github.umer0586.sensagram.model.data.Setting
import kotlinx.coroutines.flow.Flow


interface SettingsRepository {
    suspend fun saveSetting(setting: Setting)
    val setting : Flow<Setting>
}

