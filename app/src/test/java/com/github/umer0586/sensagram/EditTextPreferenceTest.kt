package com.github.umer0586.sensagram

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.umer0586.sensagram.ui.components.EditTextPreference
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditTextPreferenceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialState_displaysTitleAndSummary() {
        val errorMessage = "Test Error Message"
        val title = "Test Title"
        val summary = "Test Summary"

        composeTestRule.setContent {
            EditTextPreference(
                title = title,
                summary = summary,
                label = "Label",
                value = "Initial Value",
                errorText = errorMessage,
                onValueChange = {}
            )

        }

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(summary).assertIsDisplayed()
        // should only displayed with isError = true
        composeTestRule.onNodeWithText(errorMessage).assertIsNotDisplayed()
    }

    @Test
    fun editMode_displaysTextFieldAndButtons() {
        composeTestRule.setContent {
            EditTextPreference(
                title = "My Title",
                summary = "My Summary",
                label = "Label",
                value = "Initial Value",
                onValueChange = {},
                editMode = true
            )
        }

        composeTestRule.onNodeWithTag("TextField").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("cancel").assertIsDisplayed()
    }


    @Test
    fun inputChanges_updatesValue() {
        val inputText = mutableStateOf("")
        composeTestRule.setContent {
            EditTextPreference(
                title = "My Title",
                summary = "My Summary",
                label = "Label",
                value = inputText.value,
                onValueChange = { inputText.value = it },
                editMode = true
            )
        }

        val newValue = "New Value"
        composeTestRule.onNodeWithTag("TextField").performTextInput(newValue)
        assert(inputText.value == newValue)

    }

    @Test
    fun saveButtonClicked_callsOnSavedPressed_withValue() {
        var savedValue: String? = null
        composeTestRule.setContent {

            EditTextPreference(
                title = "My Title",
                summary = "My Summary",
                label = "Label",
                value = "Initial Value",
                onValueChange = {},
                editMode = true,
                onSavedPressed = { savedValue = it }
            )

        }
        composeTestRule.onNodeWithText("Save").performClick()

        assert(savedValue == "Initial Value")
    }

    @Test
    fun cancelButtonClicked_callsOnCancelledPressed() {
        var onCancelledPressedCalled = false
        composeTestRule.setContent {

            EditTextPreference(
                title = "My Title",
                summary = "My Summary",
                label = "Label",
                value = "Initial Value",
                onValueChange = {},
                editMode = true,
                onCancelledPressed = { onCancelledPressedCalled = true }
            )

        }
        composeTestRule.onNodeWithText("cancel").performClick()

        assert(onCancelledPressedCalled)
    }

    @Test
    fun editButtonClicked_callsOnEditPressed() {
        var onEditPressedCalled = false
        composeTestRule.setContent {

            EditTextPreference(
                title = "My Title",
                summary = "My Summary",
                label = "Label",
                value = "Initial Value",
                onValueChange = {},
                onEditPressed = { onEditPressedCalled = true }
            )

        }
        composeTestRule.onNodeWithContentDescription("EditIcon").performClick()
        assert(onEditPressedCalled)

    }

    @Test
    fun errorState_displaysError() {
        composeTestRule.setContent {

            EditTextPreference(
                title = "My Title",
                summary = "My Summary",
                label = "Label",
                value = "Initial Value",
                onValueChange = {},
                editMode = true,
                isError = true,
                errorText = "Error message"
            )

        }
        composeTestRule.onNodeWithText("Error message").assertIsDisplayed()

    }

    @Test
    fun isErrorTrue_saveButtonDisabled() {
        composeTestRule.setContent {

            EditTextPreference(
                title = "My Title",
                summary = "My Summary",
                label = "Label",
                value = "Initial Value",
                onValueChange = {},
                editMode = true, // Set editMode to true to show the Save button
                isError = true    // Set isError to true
            )

        }

        composeTestRule.onNodeWithText("Save")
            .assertIsNotEnabled() // Assert that the Save button is not enabled

    }


}