package com.mattrobertson.greek.reader.ui.lib

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mattrobertson.greek.reader.ui.theme.AppTheme

@Composable
fun DialogTopBar(
    title: String,
    onDismiss: () -> Unit
) {
    DialogTopBarCloseRight(title, onDismiss)
}

@Composable
fun DialogTopBarCloseRight(
    title: String,
    onDismiss: () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            IconButton(
                onClick = {
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Rounded.Close, "Close", tint = MaterialTheme.colors.onSurface)
            }

            Text(
                text = title,
                style = MaterialTheme.typography.h2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Preview
@Composable
fun DialogTopBarCloseRightPreview() {
    AppTheme {
        DialogTopBarCloseRight(
            title = "Vocabulary",
            onDismiss = {}
        )
    }
}

@Composable
fun DialogTopBarCloseLeft(
    title: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface),
    ) {
        IconButton(
            onClick = {
                onDismiss()
            },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Rounded.ArrowBack, "Close", tint = MaterialTheme.colors.onSurface)
        }

        Text(
            text = title,
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 8.dp),
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Preview
@Composable
fun DialogTopBarCloseLeftPreview() {
    AppTheme {
        DialogTopBarCloseLeft(
            title = "Vocabulary",
            onDismiss = {}
        )
    }
}

@Composable
fun DialogTopBarAllLeft(
    title: String,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface),
    ) {
        IconButton(
            onClick = {
                onDismiss()
            }
        ) {
            Icon(Icons.Rounded.ArrowBack, "Close", tint = MaterialTheme.colors.onSurface)
        }

        Text(
            text = title,
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp),
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Preview
@Composable
fun DialogTopBarAllLeftPreview() {
    AppTheme {
        DialogTopBarAllLeft(
            title = "Vocabulary",
            onDismiss = {}
        )
    }
}