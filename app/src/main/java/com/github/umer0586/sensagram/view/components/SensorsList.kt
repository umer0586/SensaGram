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

package com.github.umer0586.sensagram.view.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.umer0586.sensagram.model.DeviceSensor
import com.github.umer0586.sensagram.model.fakeSensors
import com.github.umer0586.sensagram.view.components.theme.SensaGramTheme


@Composable
fun SensorsList(
    sensors: List<DeviceSensor>,
    modifier: Modifier = Modifier,
    selectedSensors: List<DeviceSensor>,
    onItemCheckedChange : ((DeviceSensor, Boolean) -> Unit)? = null,
    onSensorItemTap: ((DeviceSensor) -> Unit)? = null
) {

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {

        sensors.forEach { sensorItem ->
            var checkState by remember(key1 = selectedSensors.contains(sensorItem)) {
                mutableStateOf(selectedSensors.contains(sensorItem))
            }
            SensorItem(
                sensor = sensorItem,
                checked = checkState,
                onCheckedChange = {
                    checkState = it
                    onItemCheckedChange?.invoke(sensorItem,it)

                },
                onTap = {
                    onSensorItemTap?.invoke(it)
                }
            )
        }
    }


}



@Composable
private fun SensorItem(
    sensor: DeviceSensor,
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTap: ((DeviceSensor) -> Unit)? = null
) {


    ListItem(
        modifier = modifier
            .shadow(elevation = 7.dp, shape = RoundedCornerShape(10.dp), clip = true)
            .clickable {
                onTap?.invoke(sensor)
            },
        headlineContent = {
            AutoResizeText(
                text = sensor.name,
                maxLines = 1,
                fontSizeRange = FontSizeRange(
                    min = 13.sp,
                    max = 16.sp
                )
            )
        },
        supportingContent = {

            AutoResizeText(
                text = "type = ${sensor.stringType}",
                fontWeight = if (checked) FontWeight.Bold else null,
                maxLines = 1,
                fontSizeRange = FontSizeRange(
                    min = 10.sp,
                    max = 12.sp
                )
            )


        },
        trailingContent = {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        },
        tonalElevation = 5.dp,
        shadowElevation = 5.dp
    )


}
@Preview(showBackground = true)
@Composable
fun SensorsListPreview(){
    
    SensaGramTheme {
        SensorsList(
            sensors = fakeSensors,
            selectedSensors = listOf(fakeSensors[0], fakeSensors[2]),
            onItemCheckedChange = { _, _ -> },
            onSensorItemTap = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SensorItemPreview(){

    SensaGramTheme(dynamicColor = false) {
        SensorItem(
            sensor = fakeSensors[0],
            checked = true,
            onCheckedChange = {},
            onTap = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SensorItemPreviewNightMode(){

    SensaGramTheme(dynamicColor = false) {
        SensorItem(
            sensor = fakeSensors[0],
            checked = true,
            onCheckedChange = {},
            onTap = {}
        )
    }
}
