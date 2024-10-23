package com.github.umer0586.sensagram.screentests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.umer0586.sensagram.model.streamer.StreamingInfo
import com.github.umer0586.sensagram.view.components.screens.HomeScreenContent
import com.github.umer0586.sensagram.viewmodel.HomeScreenUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@OptIn(ExperimentalPermissionsApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeScreenContentTest {

    private val streamingInfo = StreamingInfo(
        address = "192.168.1.1",
        portNo = 8080,
        samplingRate = 1000,
        sensors = emptyList()
    )
    private var uiState = HomeScreenUiState(
        isStreaming = false,
        streamingInfo = streamingInfo,
        selectedSensorsCount = 0
    )


    private val infoCardMessageSendingDataTo
        get() = "sending data to\n${streamingInfo.address}:${streamingInfo.portNo}"
    private val infoCardWarningNoSensorSelected = "No Sensor Selected"
    private val count get() = uiState.selectedSensorsCount
    private val infoCardSuccessCountSensorSelected get() = "$count sensor${if (count > 1) "s" else ""} selected"


    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun initialState_displaysStreamControllerButton() {

        composeTestRule.setContent {
            HomeScreenContent(
                uiState = uiState,
                onUiEvent = {}
            )
        }
        composeTestRule.onNodeWithText("Stream")
            .assertIsDisplayed() // Stream button should be visible
        composeTestRule.onNodeWithTag("InfoCard").assertIsNotDisplayed()
    }


    @Test
    fun streamingState_displaysInfoCard() {

        this.uiState = uiState.copy(isStreaming = true)

        composeTestRule.setContent {
            HomeScreenContent(
                uiState = uiState,
                onUiEvent = {}
            )
        }
        composeTestRule.onNodeWithTag("InfoCard").assertIsDisplayed() // InfoCard should be visible
        composeTestRule.onNodeWithText(infoCardMessageSendingDataTo).assertIsDisplayed()
    }

    @Test
    fun streamingState_displaysWarningWhenNoSensorsSelected() {

        this.uiState = uiState.copy(isStreaming = true, selectedSensorsCount = 0)

        composeTestRule.setContent {
            HomeScreenContent(
                uiState = uiState,
                onUiEvent = {}
            )
        }
        composeTestRule.onNodeWithText(infoCardWarningNoSensorSelected)
            .assertIsDisplayed() // Warning should be visible
        composeTestRule.onNodeWithText("Stop")
            .assertIsDisplayed() // stream controller button should show text "Stop"
    }


    @Test
    fun streamingState_displaysSuccessMessageWithSensorCount() {
        this.uiState = uiState.copy(isStreaming = true, selectedSensorsCount = 2)

        composeTestRule.setContent {
            HomeScreenContent(
                uiState = uiState,
                onUiEvent = {}
            )
        }

        composeTestRule.onNodeWithText(infoCardSuccessCountSensorSelected)
            .assertIsDisplayed() // Success message with sensor count
        composeTestRule.onNodeWithText(infoCardMessageSendingDataTo).assertIsDisplayed()
        composeTestRule.onNodeWithText("Stop").assertIsDisplayed()

    }


}