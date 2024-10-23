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

package com.github.umer0586.sensagram.view.components.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.umer0586.sensagram.model.streamer.StreamingInfo
import com.github.umer0586.sensagram.view.components.StreamControllerButton
import com.github.umer0586.sensagram.view.components.theme.SensaGramTheme
import com.github.umer0586.sensagram.viewmodel.HomeScreenEvent
import com.github.umer0586.sensagram.viewmodel.HomeScreenUiState
import com.github.umer0586.sensagram.viewmodel.HomeScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(),
    onStreamingError: (() -> Unit)? = null
) {

    viewModel.onError {
        onStreamingError?.invoke()
    }

    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        landscapeMode = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE,
        onUiEvent = viewModel::onUiEvent,
        postNotificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        ) else null
    )


}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeScreenUiState,
    landscapeMode: Boolean = false,
    onUiEvent: (HomeScreenEvent) -> Unit,
    // PermissionState cannot be used within a Composable function that is being rendered in @Preview mode.
    postNotificationPermissionState: PermissionState? = null
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){

        AnimatedVisibility(
            visible = uiState.isStreaming,
            modifier = Modifier
                .then(
                    if (landscapeMode) Modifier.align(Alignment.CenterStart).padding(start = 50.dp)
                    else Modifier.align(Alignment.TopCenter).padding(top = 30.dp)
                ),
            enter = slideInVertically(
                initialOffsetY = { it / 2 }
            )
        ) {
            uiState.streamingInfo?.let {
                val count = uiState.selectedSensorsCount
                InfoCard(
                    modifier = Modifier.testTag("InfoCard"),
                    text = "sending data to\n${it.address}:${it.portNo}",
                    warningText = if (count == 0) "No Sensor Selected" else null,
                    successText = if (count !=0 ) "$count sensor${if(count > 1) "s" else ""} selected" else null
                )
            }
        }


            StreamControllerButton(
                modifier = Modifier
                    .then(
                        if (landscapeMode && uiState.isStreaming)
                            Modifier.align(Alignment.CenterEnd).padding(end = 50.dp)
                        else Modifier.align(Alignment.Center)
                    ),
                isStreaming = uiState.isStreaming,
                onStartSubmit = {

                    // Android 13 introduced a runtime permission for posting notifications,
                    // requiring that apps ask for this permission and users have to explicitly grant it, otherwise notifications will not be visible.
                    //
                    // Whether user grant this permission or not we will start service anyway
                    // If permission is not granted foreground notification will not be shown
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        postNotificationPermissionState?.launchPermissionRequest()
                    }

                onUiEvent(HomeScreenEvent.OnStartSubmit)
            },
            onStopSubmit = { onUiEvent(HomeScreenEvent.OnStopSubmit) }

        )

    }


}

@Composable
private fun WarningText(
    modifier: Modifier = Modifier,
    text: String
){
    Row(
        modifier = modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.errorContainer)
        ,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val contentPadding = 7.dp
        Icon(
            modifier = Modifier
                .padding(contentPadding)
                .size(20.dp),
            imageVector = Icons.Filled.Warning,
            contentDescription = "Warning",

            )
        Text(
            modifier = Modifier.padding(contentPadding),
            text = text,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 13.sp
        )
    }
}


@Composable
private fun SuccessText(
    modifier: Modifier = Modifier,
    text: String
){
    Row(
        modifier = modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
        ,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val contentPadding = 7.dp
        Icon(
            modifier = Modifier
                .padding(contentPadding)
                .size(20.dp),
            imageVector = Icons.Filled.Check,
            contentDescription = "Warning",

            )
        Text(
            modifier = Modifier.padding(contentPadding),
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 13.sp
        )
    }
}


@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    text : String,
    warningText : String? = null,
    successText : String? = null
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {

            warningText?.let {
                WarningText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = it
                )
            }

            successText?.let{
                SuccessText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = it
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 5.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
                    .align(Alignment.CenterHorizontally),
                text = text,
                textAlign = TextAlign.Center
            )


    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, showSystemUi = true, name = "Home screen streaming")
@Composable
fun HomeScreenContentPreview2(
    uiState: HomeScreenUiState = HomeScreenUiState(
        isStreaming = true,
        selectedSensorsCount = 3,
        streamingInfo = StreamingInfo(
            address = "192.168.1.1",
            portNo = 5000,
            samplingRate = 1000,
            sensors = emptyList()
        )
    )
) {

    SensaGramTheme {
        HomeScreenContent(
            uiState = uiState,
            onUiEvent = {}
        )
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    name = "Landscape mode while streaming"
)
@Composable
fun HomeScreenContentLandScapePreview(
    uiState: HomeScreenUiState = HomeScreenUiState(
        isStreaming = true,
        streamingInfo = StreamingInfo(
            address = "192.168.1.1",
            portNo = 5000,
            samplingRate = 1000,
            sensors = emptyList()
        )
    )
) {

    SensaGramTheme {
        HomeScreenContent(
            uiState = uiState,
            landscapeMode = true,
            onUiEvent = {}
        )
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenContentPreview(
    uiState: HomeScreenUiState = HomeScreenUiState(
        isStreaming = false,
        streamingInfo = null
    )
) {

    SensaGramTheme {
        HomeScreenContent(
            uiState = uiState,
            onUiEvent = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun InfoCardPreview(){
    SensaGramTheme {
        Column (
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            InfoCard(
                text = "sending data to \n 192.168.1.1:5000",
                warningText = "No Sensor Selected"
            )

            InfoCard(
                text = "sending data to \n 192.168.1.1:5000",
                successText = "3 sensors selected"
            )

        }

    }
}


