package com.github.umer0586.sensagram.viewmodeltests

import com.github.umer0586.sensagram.model.data.Setting
import com.github.umer0586.sensagram.model.repository.SettingsRepository
import com.github.umer0586.sensagram.testrule.MainDispatcherRule
import com.github.umer0586.sensagram.viewmodel.SettingScreenEvent
import com.github.umer0586.sensagram.viewmodel.SettingsScreenViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsScreenViewModelTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var settingsRepository: SettingsRepository


    private val initialSettings = Setting(
        ipAddress = "192.168.1.1",
        portNo = 8080,
        selectedSensors = emptyList(),
        samplingRate = 1000,
        streamOnBoot = false,
        gpsStreaming = false
    )

    @Before
    fun setup() {
        settingsRepository = mock()
        whenever(settingsRepository.setting).thenReturn(flowOf(initialSettings))
    }


    @Test
    fun `initialization collects settings from repository`() = runTest{

        val viewModel = SettingsScreenViewModel(settingsRepository)

        // Ensure all pending coroutines are executed
        testScheduler.advanceUntilIdle()

        viewModel.uiState.value.let { uiState ->
            assertEquals(initialSettings.ipAddress, uiState.savedIpAddress)
            assertEquals(initialSettings.portNo, uiState.savedPortNo)
            assertEquals(initialSettings.samplingRate, uiState.savedSamplingRate)
            assertEquals(initialSettings.streamOnBoot, uiState.streamOnBoot)
        }
    }

    @Test
    fun `valid IP address updates state correctly`() = runTest {

        val viewModel = SettingsScreenViewModel(settingsRepository)
        viewModel.onUiEvent(SettingScreenEvent.OnIpAddressChange("192.168.1.1"))

        // No need to call testScheduler.advanceUntilIdle() as the
        // above uiEvent doesn't launches any coroutine

        viewModel.uiState.value.let { uiState ->
            assertEquals("192.168.1.1", uiState.ipAddress)
            assertTrue(uiState.isIpAddressValid)
        }

        // When
        viewModel.onUiEvent(SettingScreenEvent.OnIpAddressChange("256.256.256.256"))

        // Then
        viewModel.uiState.value.let { uiState ->
            assertEquals("256.256.256.256", uiState.ipAddress)
            assertFalse(uiState.isIpAddressValid)
        }
    }



    @Test
    fun `valid port number updates state correctly`() = runTest{

        val viewModel = SettingsScreenViewModel(settingsRepository)
        // When
        viewModel.onUiEvent(SettingScreenEvent.OnPortNoChange(8080))

        // Then
        viewModel.uiState.value.let { uiState ->
            assertEquals(8080, uiState.portNo)
            assertTrue(uiState.isPortNoValid)
        }


        // When
        viewModel.onUiEvent(SettingScreenEvent.OnPortNoChange(70000))

        // Then
        viewModel.uiState.value.let { uiState ->
            assertEquals(70000, uiState.portNo)
            assertFalse(uiState.isPortNoValid)
        }
    }



    @Test
    fun `valid sampling rate updates state correctly`() = runTest{

        val viewModel = SettingsScreenViewModel(settingsRepository)
        // When
        viewModel.onUiEvent(SettingScreenEvent.OnSamplingRateChange(1000))

        // Then
        viewModel.uiState.value.let { uiState ->
            assertEquals(1000, uiState.samplingRate)
            assertTrue(uiState.isSamplingRateValid)
        }

        // When
        viewModel.onUiEvent(SettingScreenEvent.OnSamplingRateChange(300000))

        // Then
        viewModel.uiState.value.let { uiState ->
            assertEquals(300000, uiState.samplingRate)
            assertFalse(uiState.isSamplingRateValid)
        }
    }


    @Test
    fun `saving IP address updates repository`() = runTest {
        val viewModel = SettingsScreenViewModel(settingsRepository)

        // When
        viewModel.onUiEvent(SettingScreenEvent.OnSaveIpAddress("192.168.1.1"))

        // Advance time and ensure all pending coroutines are executed
        // Its important to call testScheduler.advanceUntilIdle() here
        // as onSaveIpAddress() launches a coroutine to update the repository
        testScheduler.advanceUntilIdle()


        // verify that saveSettings is called with specified arguments
        verify(settingsRepository).saveSetting(initialSettings.copy(ipAddress = "192.168.1.1"))


    }

    @Test
    fun `saving port number updates repository`() = runTest{

        val viewModel = SettingsScreenViewModel(settingsRepository)
        viewModel.onUiEvent(SettingScreenEvent.OnSavePortNo(8080))

        // Advance time and ensure collection happens
        testScheduler.advanceUntilIdle()

        // Then
        verify(settingsRepository).saveSetting(initialSettings.copy(portNo = 8080))
    }

    @Test
    fun `saving sampling rate updates repository`() = runTest {

        val viewModel = SettingsScreenViewModel(settingsRepository)
        // When
        viewModel.onUiEvent(SettingScreenEvent.OnSaveSamplingRate(1000))

        // Advance time and ensure collection happens
        testScheduler.advanceUntilIdle()

        // Then
        verify(settingsRepository).saveSetting(initialSettings.copy(samplingRate = 1000))
    }

    @Test
    fun `saving stream on boot updates repository`() = runTest{

        val viewModel = SettingsScreenViewModel(settingsRepository)
        // When
        viewModel.onUiEvent(SettingScreenEvent.OnSaveStreamOnBoot(true))

        // Advance time and ensure collection happens
        testScheduler.advanceUntilIdle()

        // Then
        verify(settingsRepository).saveSetting(initialSettings.copy(streamOnBoot = true))
    }
}

