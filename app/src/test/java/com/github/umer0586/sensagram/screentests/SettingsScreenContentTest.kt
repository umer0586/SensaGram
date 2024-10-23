package com.github.umer0586.sensagram.screentests


import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.umer0586.sensagram.view.components.screens.SettingsScreenContent
import com.github.umer0586.sensagram.viewmodel.SettingsScreenUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsScreenContentTest {


    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialState_displaysPreferences() {
        composeTestRule.setContent {
            SettingsScreenContent(
                uiState = SettingsScreenUiState(),
                onUIEvent = {}
            )
        }

        composeTestRule.onNodeWithText("Remote Address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remote Port No").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sampling Rate (Microseconds)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stream On Boot").assertIsDisplayed()
    }

    @Test
    fun savedValuesDisplay_test() {

        val uiState = SettingsScreenUiState(
            ipAddress = "192.168.1.18",
            isIpAddressValid = true,
            savedIpAddress = "192.168.1.1",
            portNo = 8080,
            isPortNoValid = true,
            savedPortNo = 8085,
            samplingRate = 200000,
            savedSamplingRate = 100000,
            isSamplingRateValid = true,
            streamOnBoot = false
        )

        composeTestRule.setContent {
            SettingsScreenContent(
                uiState = uiState,
                onUIEvent = {}
            )
        }

        // check if saved ipAddress is displayed
        composeTestRule.onNodeWithText("192.168.1.1").assertIsDisplayed()
        composeTestRule.onNodeWithText("8085").assertIsDisplayed()
        composeTestRule.onNodeWithText("100000").assertIsDisplayed()


    }


    @Test
    fun streamOnBootChecked_displaysTargetAddress() {
        val uiState =
            SettingsScreenUiState(
                streamOnBoot = true,
                savedIpAddress = "192.168.1.10",
                savedPortNo = 8080
            )


        composeTestRule.setContent {

            SettingsScreenContent(
                uiState = uiState,
                onUIEvent = {}
            )

        }
        // Assert that the "Target Address: ..." text is displayed
        composeTestRule.onNodeWithText("Target Address : 192.168.1.10:8080").assertIsDisplayed()
    }

    @Test
    fun streamOnBootNotChecked_displaysNothing() {
        val uiState =
            SettingsScreenUiState(
                streamOnBoot = false, // Set streamOnBoot to true
                savedIpAddress = "192.168.1.10",
                savedPortNo = 8080
            )


        composeTestRule.setContent {

            SettingsScreenContent(
                uiState = uiState,
                onUIEvent = {}
            )

        }
        // Assert that the "Target Address: ..." text is displayed
        composeTestRule.onNodeWithText("Target Address : 192.168.1.10:8080").assertIsNotDisplayed()
    }

    @Test
    fun samplingRateDetailShown_WhenEditMode() {

        composeTestRule.setContent {
            SettingsScreenContent(
                uiState = SettingsScreenUiState(),
                onUIEvent = {}
            )
        }

        composeTestRule.onNodeWithTag("SamplingRate")
            .onChildren()
            .filter(hasContentDescription("EditIcon"))
            .onFirst()
            .performClick()

        composeTestRule.onNodeWithTag("SamplingRateDetail")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("SamplingRate")
            .onChildren()
            .filter(hasText("cancel") and hasClickAction())
            .onFirst()
            .performClick()

        composeTestRule.onNodeWithTag("SamplingRateDetail")
            .assertIsNotDisplayed()

    }

}