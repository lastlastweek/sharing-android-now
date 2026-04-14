package com.lastweek.sharing.mjpeg.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lastweek.sharing.common.ModuleSettings
import com.lastweek.sharing.mjpeg.R
import com.lastweek.sharing.mjpeg.ui.settings.security.AutoChangePin
import com.lastweek.sharing.mjpeg.ui.settings.security.BlockAddress
import com.lastweek.sharing.mjpeg.ui.settings.security.EnablePin
import com.lastweek.sharing.mjpeg.ui.settings.security.HidePinOnStart
import com.lastweek.sharing.mjpeg.ui.settings.security.NewPinOnAppStart
import com.lastweek.sharing.mjpeg.ui.settings.security.Pin

public data object SecurityGroup : ModuleSettings.Group {
    override val id: String = "SECURITY"
    override val position: Int = 2
    override val items: List<ModuleSettings.Item> =
        listOf(EnablePin, HidePinOnStart, NewPinOnAppStart, AutoChangePin, Pin, BlockAddress)
            .filter { it.available }.sortedBy { it.position }

    @Composable
    override fun TitleUI(modifier: Modifier) {
        Text(
            text = stringResource(id = R.string.mjpeg_pref_settings_security),
            modifier = modifier,
            style = MaterialTheme.typography.titleMedium
        )
    }
}