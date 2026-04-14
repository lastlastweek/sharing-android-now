package com.lastweek.sharing.temporary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.unit.toRect

@Composable
internal fun ScreenStreamContent(
    modifier: Modifier = Modifier,
    isLoggingOn: Boolean = AppLogger.isLoggingOn
) {
    if (isLoggingOn) {
        Column(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxSize()
        ) {
            CollectingLogsUi(modifier = Modifier.fillMaxWidth())
            MainContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            )
        }
    } else {
        MainContent(
            modifier = modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxSize()
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier
) {
    val windowSize = currentWindowSize()
    val contentBoundsInWindow = remember(windowSize) { mutableStateOf(windowSize.toIntRect().toRect()) }
    StreamTabContent(contentBoundsInWindow.value, modifier = Modifier.fillMaxSize())
}
