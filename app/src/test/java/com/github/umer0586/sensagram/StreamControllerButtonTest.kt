package com.github.umer0586.sensagram


import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.umer0586.sensagram.view.components.StreamControllerButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StreamControllerButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun streamControllerButton_displaysStream_whenNotStreaming() {
        val isStreaming = mutableStateOf(false)
        var startClicked = false

        composeTestRule.setContent {
            StreamControllerButton(
                isStreaming = isStreaming.value,
                onStartSubmit = { startClicked = true },
                onStopSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithText("Stream")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Stream")
            .assert(hasClickAction())
            .performClick()

        assert(startClicked) // Verify that onStartSubmit was called
    }

    @Test
    fun streamControllerButton_displaysStop_whenStreaming() {
        val isStreaming = mutableStateOf(true)
        var stopClicked = false

        composeTestRule.setContent {
            StreamControllerButton(
                isStreaming = isStreaming.value,
                onStartSubmit = {},
                onStopSubmit = { stopClicked = true }
            )
        }

        composeTestRule
            .onNodeWithText("Stop")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Stop")
            .assert(hasClickAction())
            .performClick()

        assert(stopClicked) // Verify that onStopSubmit was called
    }

    @Test
    fun streamControllerButton_showsCircularProgress_whenStreaming() {
        val isStreaming = mutableStateOf(true)
        val buttonSize: Dp = 100.dp

        composeTestRule.setContent {
            StreamControllerButton(
                isStreaming = isStreaming.value,
                buttonSize = buttonSize,
                onStartSubmit = {},
                onStopSubmit = {}
            )
        }

        composeTestRule
            .onNode(hasTestTag("CircularProgressIndicator"))
            .assertIsDisplayed()

        // Verify the size of CircularProgressIndicator is buttonSize - 5.dp
        composeTestRule
            .onNode(hasTestTag("CircularProgressIndicator"))
            .assertWidthIsEqualTo(buttonSize - 5.dp)
            .assertHeightIsEqualTo(buttonSize - 5.dp)
    }
}
