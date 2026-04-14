package com.lastweek.sharing.mjpeg.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lastweek.sharing.common.ModuleSettings
import com.lastweek.sharing.mjpeg.R
import com.lastweek.sharing.mjpeg.ui.settings.image.CropImage
import com.lastweek.sharing.mjpeg.ui.settings.image.Flip
import com.lastweek.sharing.mjpeg.ui.settings.image.Grayscale
import com.lastweek.sharing.mjpeg.ui.settings.image.JpegQuality
import com.lastweek.sharing.mjpeg.ui.settings.image.MaxFPS
import com.lastweek.sharing.mjpeg.ui.settings.image.ResizeImage
import com.lastweek.sharing.mjpeg.ui.settings.image.Rotation
import com.lastweek.sharing.mjpeg.ui.settings.image.VrMode

public object ImageGroup : ModuleSettings.Group {
    override val id: String = "IMAGE"
    override val position: Int = 1
    override val items: List<ModuleSettings.Item> =
        listOf(VrMode, CropImage, Grayscale, ResizeImage, Rotation, Flip, MaxFPS, JpegQuality)
            .filter { it.available }.sortedBy { it.position }

    @Composable
    override fun TitleUI(modifier: Modifier) {
        Text(
            text = stringResource(id = R.string.mjpeg_pref_settings_image),
            modifier = modifier,
            style = MaterialTheme.typography.titleMedium
        )
    }
}