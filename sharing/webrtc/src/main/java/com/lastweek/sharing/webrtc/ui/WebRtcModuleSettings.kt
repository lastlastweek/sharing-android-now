package com.lastweek.sharing.webrtc.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lastweek.sharing.common.ModuleSettings
import com.lastweek.sharing.webrtc.WebRtcStreamingModule

internal object WebRtcModuleSettings : ModuleSettings {
    override val id: String = WebRtcStreamingModule.Id.value
    override val groups: List<ModuleSettings.Group> = emptyList()

    @Composable
    override fun TitleUI(modifier: Modifier) {
    }
}