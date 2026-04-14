package com.lastweek.sharing.temporary

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.lastweek.sharing.common.getLog
import com.lastweek.sharing.common.module.StreamingModule
import com.lastweek.sharing.common.module.StreamingModuleManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.koin.android.ext.android.inject
import kotlin.also
import kotlin.jvm.java

public class SingleActivity : AppUpdateActivity() {
    internal companion object {
        internal fun getIntent(context: Context): Intent = Intent(context, SingleActivity::class.java)
    }

    private val streamingModulesManager: StreamingModuleManager by inject(mode = LazyThreadSafetyMode.NONE)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        XLog.d(this@SingleActivity.getLog("onCreate", "Bug workaround: ${window.decorView}"))
        super.onCreate(savedInstanceState)

        setContent {
            ScreenStreamContent()
        }
        AppReview.startTracking(activity = this, streamingModulesManager = streamingModulesManager)

        streamingModulesManager.selectedModuleIdFlow
            .onStart {}
            .onEach { moduleId ->
                if (streamingModulesManager.isActive(moduleId)) return@onEach
                startModuleWithCheck(moduleId)
            }
            .flowWithLifecycle(lifecycle, minActiveState = Lifecycle.State.RESUMED)
            .launchIn(lifecycleScope)
    }

    private suspend fun startModuleWithCheck(moduleId: StreamingModule.Id, attempt: Int = 0) {
        val importance = ActivityManager.RunningAppProcessInfo().also { ActivityManager.getMyMemoryState(it) }.importance

        if (attempt >= 5 || importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE) {
            streamingModulesManager.startModule(moduleId, this)
        } else {
            delay(75)
            startModuleWithCheck(moduleId, attempt + 1)
        }
    }
}
