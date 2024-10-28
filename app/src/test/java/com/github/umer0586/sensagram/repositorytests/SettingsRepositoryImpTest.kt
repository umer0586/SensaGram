package com.github.umer0586.sensagram.repositorytests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.umer0586.sensagram.model.data.Setting
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import com.github.umer0586.sensagram.model.repository.SettingsRepositoryImp
import com.github.umer0586.sensagram.testrule.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryImpTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()  // Robolectric context
    }

    /**
     * Test coverage note:
     * This test exclude the 'selectedSensors' field validation since it relies on
     * SensorManager from the Android framework. In the Robolectric test environment,
     * device sensors are not available, resulting in this field always being empty.
     */
    @Test
    fun saveAndRetrieveSettings_correctly() = runTest { // uses Main scheduler

        val testDispatcher = StandardTestDispatcher(testScheduler)
        settingsRepository = SettingsRepositoryImp(context, testDispatcher)

        // Arrange: Create a sample Setting object
        var sampleSetting = Setting(
            ipAddress = "192.168.1.30",
            portNo = 9090,
            selectedSensors = emptyList(),  // No sensors selected
            samplingRate = 10000,
            streamOnBoot = true,
            gpsStreaming = false
        )

        // Act: Save the setting to DataStore
        settingsRepository.saveSetting(sampleSetting)

        // Assert: Retrieve the settings and verify the values
        val expectedSettings = settingsRepository.setting.first()

        Assert.assertEquals(expectedSettings,sampleSetting)

        sampleSetting = sampleSetting.copy(
            ipAddress = "192.168.1.100",
            portNo = 8085,
            samplingRate = 20000,
            streamOnBoot = true,
            gpsStreaming = true
        )

        settingsRepository.saveSetting(sampleSetting)

        val expectedSettings2 = settingsRepository.setting.first()

        Assert.assertEquals(expectedSettings2,sampleSetting)

    }


}
