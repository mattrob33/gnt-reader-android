package com.mattrobertson.greek.reader.ui.lib

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mattrobertson.greek.reader.ui.theme.AppTheme

@Composable
fun DialogTopBar(
    title: String,
    onDismiss: () -> Unit
) {
    MaxWidthColumn {
        MaxWidthBox(
            modifier = Modifier.height(60.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colors.onBackground
                )
            }

            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun DialogTopBar_Preview_Light() {
    AppTheme {
        DialogTopBar(
            title = "Vocabulary",
            onDismiss = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF222222
)
@Composable
fun DialogTopBar_Preview_Dark() {
    AppTheme(darkTheme = true) {
        DialogTopBar(
            title = "Vocabulary",
            onDismiss = {}
        )
    }
}