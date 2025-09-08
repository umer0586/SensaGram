package com.github.umer0586.sensagram.viewmodeltests

import com.github.umer0586.sensagram.data.model.accelerometer
import com.github.umer0586.sensagram.data.model.gyroscope
import com.github.umer0586.sensagram.data.model.light
import com.github.umer0586.sensagram.data.model.magneticField
import com.github.umer0586.sensagram.data.model.proximity
import com.github.umer0586.sensagram.data.repository.SensorsRepository
import com.github.umer0586.sensagram.data.model.Setting
import com.github.umer0586.sensagram.data.repository.SettingsRepository
import com.github.umer0586.sensagram.data.util.LocationPermissionChecker
import com.github.umer0586.sensagram.testrule.MainDispatcherRule
import com.github.umer0586.sensagram.ui.screens.sensors.SensorScreenEvent
import com.github.umer0586.sensagram.ui.screens.sensors.SensorsScreenViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class SensorsScreenViewModelTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sensorsRepository: SensorsRepository = mock()
    private val settingsRepository: SettingsRepository = mock()
    private val locationPermissionChecker: LocationPermissionChecker = mock()

    @Before
    fun setup() {
        whenever(sensorsRepository.getAllSensors()).thenReturn(
            listOf(accelerometer, gyroscope, magneticField , light, proximity)
        )
    }

    @Test
    fun `initialization collects selected sensors from repository and sync with uiState`() = runTest {

        val initialSettings = Setting(
            ipAddress = "192.168.1.1",
            portNo = 8080,
            selectedSensors = emptyList(),
            samplingRate = 1000,
            streamOnBoot = false,
            gpsStreaming = false
        )
        whenever(settingsRepository.setting).thenReturn(flowOf(initialSettings))

        val viewModel = SensorsScreenViewModel(
            sensorsRepository = sensorsRepository,
            settingsRepository = settingsRepository,
            locationPermissionChecker = locationPermissionChecker
        )
        testScheduler.advanceUntilIdle()


        viewModel.uiState.value.let { uiState ->
            assertTrue(uiState.selectedSensors.isEmpty())
            assertEquals(false, uiState.gpsChecked)
        }

    }

    @Test
    fun `When no location permission then gps sensor should not be checked`() = runTest {

        whenever(locationPermissionChecker.isLocationPermissionGranted()).thenReturn(false)

        val initialSettings = Setting(
            ipAddress = "192.168.1.1",
            portNo = 8080,
            selectedSensors = emptyList(),
            samplingRate = 1000,
            streamOnBoot = false,
            gpsStreaming = false
        )
        whenever(settingsRepository.setting).thenReturn(flowOf(initialSettings))

        val viewModel = SensorsScreenViewModel(
            sensorsRepository = sensorsRepository,
            settingsRepository = settingsRepository,
            locationPermissionChecker = locationPermissionChecker
        )
        testScheduler.advanceUntilIdle()


        viewModel.uiState.value.let { uiState ->
            assertFalse(uiState.gpsChecked)
        }

    }

    @Test
    fun `selecting and deselecting sensors should update the state`() = runTest{

        val lastSelectedSensor = listOf(accelerometer, gyroscope, light)
        val initialSettings = Setting(
            ipAddress = "192.168.1.1",
            portNo = 8080,
            selectedSensors = lastSelectedSensor,
            samplingRate = 1000,
            streamOnBoot = false,
            gpsStreaming = false
        )
        whenever(settingsRepository.setting).thenReturn(flowOf(initialSettings))

        val viewModel = SensorsScreenViewModel(
            sensorsRepository = sensorsRepository,
            settingsRepository = settingsRepository,
            locationPermissionChecker = locationPermissionChecker
        )
        testScheduler.advanceUntilIdle()

        viewModel.uiState.value.let { uiState ->
            assertEquals(lastSelectedSensor, uiState.selectedSensors)
        }

        viewModel.onUiEvent(SensorScreenEvent.OnSensorSelected(magneticField))

        testScheduler.advanceUntilIdle()

        viewModel.uiState.value.let { uiState ->
            assertEquals(4, uiState.selectedSensors.count())
            assertEquals(listOf(accelerometer, gyroscope, light, magneticField).toSet(), uiState.selectedSensors.toSet())
        }

        viewModel.onUiEvent(SensorScreenEvent.OnSensorDeselected(accelerometer))
        viewModel.onUiEvent(SensorScreenEvent.OnSensorDeselected(light))

        testScheduler.advanceUntilIdle()

        viewModel.uiState.value.let { uiState ->
            assertEquals(2, uiState.selectedSensors.count())
            assertEquals(listOf(gyroscope, magneticField).toSet(), uiState.selectedSensors.toSet())
        }

    }

    fun `GPS switch change should update the state`() = runTest {

        val initialSettings = Setting(
            ipAddress = "192.168.1.1",
            portNo = 8080,
            selectedSensors = emptyList(),
            samplingRate = 1000,
            streamOnBoot = false,
            gpsStreaming = false
        )
        whenever(settingsRepository.setting).thenReturn(flowOf(initialSettings))

        val viewModel = SensorsScreenViewModel(
            sensorsRepository = sensorsRepository,
            settingsRepository = settingsRepository,
            locationPermissionChecker = locationPermissionChecker
        )
        testScheduler.advanceUntilIdle()

        viewModel.onUiEvent(SensorScreenEvent.OnGPSCheckedChange(true))

        testScheduler.advanceUntilIdle()

        verify(settingsRepository).saveSetting(initialSettings.copy(gpsStreaming = true))

        viewModel.uiState.value.let { uiState ->
            assertTrue(uiState.gpsChecked)
        }

        viewModel.onUiEvent(SensorScreenEvent.OnGPSCheckedChange(false))

        testScheduler.advanceUntilIdle()

        verify(settingsRepository).saveSetting(initialSettings.copy(gpsStreaming = false))

        viewModel.uiState.value.let { uiState ->
            assertFalse(uiState.gpsChecked)
        }
    }
}
