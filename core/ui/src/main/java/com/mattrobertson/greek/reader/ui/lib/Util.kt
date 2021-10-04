package com.mattrobertson.greek.reader.ui.lib

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}