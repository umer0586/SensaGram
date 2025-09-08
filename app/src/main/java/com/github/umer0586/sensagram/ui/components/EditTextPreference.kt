/*
 *     This file is a part of SensaGram (https://github.com/umer0586/SensaGram)
 *     Copyright (C) 2024 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     SensaGram is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     SensaGram is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with SensaGram.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.github.umer0586.sensagram.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.github.umer0586.sensagram.ui.theme.SensaGramTheme


@Composable
fun EditTextPreference(
    title: String,
    summary: String,
    label: String,
    modifier: Modifier = Modifier,
    editMode: Boolean = false,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    value: String,
    onValueChange: (String) -> Unit,
    onSavedPressed: ((String) -> Unit)? = null,
    onCancelledPressed: (() -> Unit)? = null,
    onEditPressed: (() -> Unit)? = null,


    ) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            if (editMode) {
                OutlinedTextField(
                    modifier = Modifier.testTag("TextField"),
                    value = value,
                    onValueChange = { onValueChange.invoke(it) },
                    isError = isError,
                    label = { Text(label) },
                    trailingIcon = {
                        if(isError)
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                    },
                    supportingText = { if (isError && errorText != null) Text(errorText) },
                    keyboardOptions = keyboardOptions
                )

            } else {
                Text(title)
            }

        },
        supportingContent = {
            if (!editMode)
                Text(summary)

            AnimatedVisibility(editMode, enter = slideInVertically()) {
                Row {
                    TextButton(
                        onClick = {
                            onSavedPressed?.invoke(value)
                        },
                        enabled = !isError,
                        content = {
                            Text("Save")
                        }
                    )

                    TextButton(
                        onClick = {
                            onCancelledPressed?.invoke()
                        },
                        content = {
                            Text("cancel")
                        }
                    )
                }
            }

        },
        trailingContent = {
            if (!editMode)
                Icon(
                    modifier = Modifier.clickable { onEditPressed?.invoke() },
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "EditIcon"
                )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditTextPreferencePreview(){
    var text by remember { mutableStateOf("") }
    var editMode by remember { mutableStateOf(false) }
    SensaGramTheme {

        Column(modifier = Modifier.fillMaxSize()) {
            EditTextPreference(
                title = "Title",
                summary = "Summary",
                label = "Enter",
                isError = text == "error",
                value = text,
                onValueChange = { text = it },
                editMode = false,

            )

            EditTextPreference(
                title = "Title",
                summary = "Summary",
                label = "Enter",
                value = text,
                onValueChange = { text = it },
                editMode = true,

                )

            EditTextPreference(
                title = "Title",
                summary = "Summary",
                label = "Enter",
                isError = true,
                errorText = "Invalid Input",
                value = text,
                onValueChange = { text = it },
                editMode = true,

                )
        }

    }
}