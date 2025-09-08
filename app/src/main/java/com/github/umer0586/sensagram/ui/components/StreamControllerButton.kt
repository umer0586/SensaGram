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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StreamControllerButton(
    isStreaming: Boolean,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 100.dp,
    onStartSubmit: () -> Unit,
    onStopSubmit: () -> Unit
) {

    Box(modifier = modifier) {

        Button(
            modifier = Modifier
                .size(buttonSize)
                .align(alignment = Alignment.Center),
            shape = CircleShape,
            onClick = {

                if (!isStreaming)
                    onStartSubmit()
                else
                    onStopSubmit()
            }

        ) {

            if (isStreaming) {
                Text("Stop")
            } else {
                Text("Stream")
            }

        }

        if (isStreaming)
            CircularProgressIndicator(
                modifier = Modifier
                    .size(buttonSize - 5.dp)
                    .align(alignment = Alignment.Center)
                    .testTag("CircularProgressIndicator"),
                color = MaterialTheme.colorScheme.onPrimary,
            )

    }

}

@Composable
@Preview
fun StreamControllerButtonPreview() {

    var isStreaming by remember {
        mutableStateOf(false)
    }

    StreamControllerButton(
        isStreaming = isStreaming,
        onStartSubmit = {
            isStreaming = true
        },
        onStopSubmit = {
            isStreaming = false
        }
    )


}
