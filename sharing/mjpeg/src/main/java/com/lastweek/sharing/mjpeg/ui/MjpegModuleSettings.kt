package com.lastweek.sharing.mjpeg.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lastweek.sharing.common.ModuleSettings
import com.lastweek.sharing.mjpeg.MjpegStreamingModule

internal object MjpegModuleSettings : ModuleSettings {
    override val id: String = MjpegStreamingModule.Id.value
    override val groups: List<ModuleSettings.Group> = emptyList()

    @Composable
    override fun TitleUI(modifier: Modifier) {
    }
}
