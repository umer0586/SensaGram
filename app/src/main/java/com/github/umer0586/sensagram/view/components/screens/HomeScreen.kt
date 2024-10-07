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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.umer0586.sensagram.model.streamer.StreamingInfo
import com.github.umer0586.sensagram.view.components.theme.SensaGramTheme
import com.github.umer0586.sensagram.view.components.StreamControllerButton
import com.github.umer0586.sensagram.viewmodel.HomeScreenEvent
import com.github.umer0586.sensagram.viewmodel.HomeScreenUiState
import com.github.umer0586.sensagram.viewmodel.HomeScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


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
    )


}


@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeScreenUiState,
    landscapeMode: Boolean = false,
    onUiEvent: (HomeScreenEvent) -> Unit
) {

    val postNotificationPermissionState = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

    if (!landscapeMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            AnimatedVisibility(
                visible = uiState.isStreaming,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 30.dp),
                enter = slideInVertically(
                    initialOffsetY = { it / 2 }
                )
            ) {

                uiState.streamingInfo?.let {
                    InfoCard(
                        address = it.address,
                        portNo = it.portNo
                    )
                }


            }


            StreamControllerButton(
                modifier = Modifier
                    .align(Alignment.Center),
                isStreaming = uiState.isStreaming,
                onStartSubmit = {

                    // Android 13 introduced a runtime permission for posting notifications,
                    // requiring that apps ask for this permission and users have to explicitly grant it, otherwise notifications will not be visible.
                    //
                    // Whether user grant this permission or not we will start service anyway
                    // If permission is not granted foreground notification will not be shown
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        postNotificationPermissionState.launchPermissionRequest()
                    }

                    onUiEvent(HomeScreenEvent.OnStartSubmit)
                },
                onStopSubmit = { onUiEvent(HomeScreenEvent.OnStopSubmit) }

            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            AnimatedVisibility(
                visible = uiState.isStreaming,
                enter = slideInHorizontally(
                    initialOffsetX = { it / 2 }
                )
            ) {

                uiState.streamingInfo?.let {
                    InfoCard(
                        address = it.address,
                        portNo = it.portNo
                    )
                }


            }

            if (uiState.isStreaming)
                Spacer(Modifier.width(100.dp))

            StreamControllerButton(
                modifier = Modifier,
                isStreaming = uiState.isStreaming,
                onStartSubmit = {

                    // Android 13 introduced a runtime permission for posting notifications,
                    // requiring that apps ask for this permission and users have to explicitly grant it, otherwise notifications will not be visible.
                    //
                    // Whether user grant this permission or not we will start service anyway
                    // If permission is not granted foreground notification will not be shown
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        postNotificationPermissionState.launchPermissionRequest()
                    }
                    onUiEvent(HomeScreenEvent.OnStartSubmit)
                },
                onStopSubmit = { onUiEvent(HomeScreenEvent.OnStopSubmit) }

            )

        }
    }


}

@Composable
private fun InfoCard(modifier: Modifier = Modifier, address: String, portNo: Int) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {

        Text(
            modifier = Modifier.padding(20.dp),
            text = "sending to\n$address:$portNo",
            textAlign = TextAlign.Center
        )

    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Home screen streaming")
@Composable
fun HomeScreenContentPreview2(
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
            onUiEvent = {}
        )
    }

}

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


