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

package com.github.umer0586.sensagram.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.umer0586.sensagram.ui.components.EditTextPreference
import com.github.umer0586.sensagram.ui.components.SwitchPreference
import com.github.umer0586.sensagram.ui.theme.SensaGramTheme


@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        uiState = uiState,
        onUIEvent = viewModel::onUiEvent
    )

}

@Composable
fun SettingsScreenContent(
    uiState: SettingsScreenUiState,
    onUIEvent : (SettingScreenEvent) -> Unit,
 ){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 10.dp, end = 10.dp),
    ) {

       var ipAddressEditMode by remember { mutableStateOf(false) }


        EditTextPreference(
            modifier = Modifier.testTag("RemoteAddress"),
            title = "Remote Address",
            label = "Remote Address",
            summary = uiState.savedIpAddress,
            value = uiState.ipAddress,
            onValueChange = {
                onUIEvent(SettingScreenEvent.OnIpAddressChange(it))
            },
            isError = !uiState.isIpAddressValid,
            editMode = ipAddressEditMode,
            onEditPressed = { ipAddressEditMode = true },
            onCancelledPressed = { ipAddressEditMode = false },
            onSavedPressed = {
                // Important : Never pass readable state directly to viewModel's event handler
                // i-e onUIEvent(SettingScreenEvent.OnSaveIpAddress(uiState.ipAddress))
                // it caused unnecessary recomposition for all other EditTextPreference in SettingsScreenContent
                onUIEvent(SettingScreenEvent.OnSaveIpAddress(it))
                ipAddressEditMode = false
            }

        )

        var portNoEditMode by remember { mutableStateOf(false) }

        EditTextPreference(
            modifier = Modifier.testTag("PortNo"),
            title = "Remote Port No",
            label = "Remote Port No",
            summary = uiState.savedPortNo.toString(),
            value = uiState.portNo.toString(),
            onValueChange = {
                try {
                    onUIEvent(SettingScreenEvent.OnPortNoChange(it.toInt()))
                } catch (e: NumberFormatException) {

                }
            },
            isError = !uiState.isPortNoValid,
            editMode = portNoEditMode,
            onEditPressed = { portNoEditMode = true },
            onCancelledPressed = { portNoEditMode = false },
            onSavedPressed = {
                onUIEvent(SettingScreenEvent.OnSavePortNo(it.toInt()))
                portNoEditMode = false
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )

        )


        var samplingRateEditMode by remember { mutableStateOf(false) }

        // sampling rate preference

        AnimatedVisibility(visible = samplingRateEditMode) {
            SamplingRateDetailText(
                modifier = Modifier
                    .padding(20.dp)
                    .testTag("SamplingRateDetail")
            )
        }
        EditTextPreference(
            modifier = Modifier.testTag("SamplingRate"),
            title = "Sampling Rate (Microseconds)",
            label = "Sampling Rate",
            summary = uiState.savedSamplingRate.toString(),
            value = uiState.samplingRate.toString(),
            onValueChange = {
                try {
                    onUIEvent(SettingScreenEvent.OnSamplingRateChange(it.toInt()))
                } catch (e: NumberFormatException) {

                }
            },
            isError = !uiState.isSamplingRateValid,
            editMode = samplingRateEditMode,
            onEditPressed = { samplingRateEditMode = true },
            onCancelledPressed = { samplingRateEditMode = false },
            onSavedPressed = {
                onUIEvent(SettingScreenEvent.OnSaveSamplingRate(it.toInt()))
                samplingRateEditMode = false
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )

        )

        SwitchPreference(
            title = "Stream On Boot",
            subtitle = if(uiState.streamOnBoot) "Target Address : ${uiState.savedIpAddress}:${uiState.savedPortNo}" else null,
            checked = uiState.streamOnBoot,
            onCheckedChange = {
                onUIEvent(SettingScreenEvent.OnStreamOnBootChange(it))
                onUIEvent(SettingScreenEvent.OnSaveStreamOnBoot(it))
            }
        )


    }

}


@Composable
private fun SamplingRateDetailText(modifier: Modifier = Modifier) {
    val detail = buildAnnotatedString {
        append("The data delay (or sampling rate) controls the interval at which sensor events are sent to application. The delay that you specify is only a suggested delay. The Android system and other applications can alter this delay.\n\n")
        append("Normal Rate : ")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("200000μs\n")
        }
        append("Fastest : ")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append("0μs")
        }

    }
    Text(
        modifier = modifier,
        text = detail
    )
}


@Preview
@Composable
fun SettingsScreenContentPreview(){
    SensaGramTheme {
        SettingsScreenContent(
            uiState = SettingsScreenUiState(
                ipAddress = "192.168.1.1",
                isIpAddressValid = true,
                savedIpAddress = "192.168.1.1",
                portNo = 8080,
                isPortNoValid = true,
                savedPortNo = 8080,
                samplingRate = 200000,
                savedSamplingRate = 200000,
                isSamplingRateValid = true
            ),
            onUIEvent = {}
        )
    }
}










