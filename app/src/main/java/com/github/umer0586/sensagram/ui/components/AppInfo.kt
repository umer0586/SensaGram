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


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.umer0586.sensagram.R
import com.github.umer0586.sensagram.ui.theme.SensaGramTheme


@Composable
fun AppInfo(
    modifier: Modifier = Modifier,
    versionName: String,
    developer: String = "Umer Farooq",
    developerEmail: String = "umerfarooq2383@gmail.com",
    sourceCodeLink : String = "https://github.com/umer0586/SensaGram",
    license : String = "GPL-3.0",
    onEmailClick: ((String) -> Unit)? = null,
    onSourceCodeClick: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = "SensaGram",
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "v$versionName",
            color = MaterialTheme.colorScheme.secondary,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            text = buildAnnotatedString {
                append("Developed By : ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(developer)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                text = "Feedback: "
            )
            Text(
                modifier = Modifier.clickable {onEmailClick?.invoke(developerEmail)},
                text = developerEmail,
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.github),
                contentDescription = null
            )
            Text(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                text = "Source Code"
            )
        }
        Text(
            modifier = Modifier.clickable {
                onSourceCodeClick?.invoke(sourceCodeLink)
            },
            text = sourceCodeLink,
            color = MaterialTheme.colorScheme.primary,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            textDecoration = TextDecoration.Underline,
        )

        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.secondary)
                .padding(5.dp),
            text = "License : $license",
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            fontWeight = FontWeight.Bold
        )


    }
}

@Preview(showBackground = true)
@Composable
fun AppInfoPreview() {
    SensaGramTheme {
        AppInfo(versionName = "v1.0.0")

    }
}