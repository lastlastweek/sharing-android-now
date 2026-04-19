package com.lastweek.sharing.temporary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lastweek.sharing.common.module.StreamingModule
import com.lastweek.sharing.common.module.StreamingModuleManager
import com.lastweek.sharing.common.settings.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun StreamTabContent(
    boundsInWindow: Rect,
    modifier: Modifier = Modifier,
    streamingModulesManager: StreamingModuleManager = koinInject()
) {
    val activeModule = streamingModulesManager.activeModuleStateFlow.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        val with = with(LocalDensity.current) { boundsInWindow.width.toDp() }
        if (with >= 800.dp) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1F), verticalArrangement = Arrangement.Center) {
                    StreamingModuleSelector(
                        streamingModulesManager = streamingModulesManager,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 16.dp, end = 8.dp, bottom = 8.dp)
                            .fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1F)) {
                    AdaptiveBanner(modifier = Modifier.fillMaxWidth())
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                StreamingModuleSelector(
                    streamingModulesManager = streamingModulesManager,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                        .fillMaxWidth()
                )
                AdaptiveBanner(modifier = Modifier.fillMaxWidth())
            }
        }
        activeModule.value?.StreamUIContent(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun StreamingModuleSelector(
    streamingModulesManager: StreamingModuleManager,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    val selectedModuleId = streamingModulesManager.selectedModuleIdFlow
        .collectAsStateWithLifecycle(initialValue = AppSettings.Default.STREAMING_MODULE)

    streamingModulesManager.modules.forEach { module ->
        ModuleSelectorRow(
            module = module,
            selectedModuleId = selectedModuleId.value,
            onModuleSelect = { moduleId -> scope.launch { streamingModulesManager.selectStreamingModule(moduleId) } },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ModuleSelectorRow(
    module: StreamingModule,
    selectedModuleId: StreamingModule.Id,
    onModuleSelect: (StreamingModule.Id) -> Unit,
    modifier: Modifier = Modifier
) {
   modifier.selectable(
        selected = module.id == selectedModuleId,
        onClick = { onModuleSelect.invoke(module.id) },
        role = Role.RadioButton)
}