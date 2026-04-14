package com.lastweek.sharing

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.WorkManager
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogItem
import com.elvishew.xlog.interceptor.AbstractFilterInterceptor
import com.jakewharton.processphoenix.ProcessPhoenix
import com.tencent.mmkv.MMKV
import com.lastweek.sharing.AppConfig.ANG_PACKAGE
import com.lastweek.sharing.common.CommonKoinModule
import com.lastweek.sharing.common.analytics.StreamingAnalytics
import com.lastweek.sharing.common.notification.NotificationHelper
import com.lastweek.sharing.handler.SettingsManager
import com.lastweek.sharing.temporary.AdMob
import com.lastweek.sharing.temporary.AppLogger
import com.lastweek.sharing.temporary.AppStreamingAnalytics
import com.lastweek.sharing.temporary.NotificationHelperImpl
import com.lastweek.sharing.webrtc.WebRtcKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

class AngApplication : MultiDexApplication() {
    companion object {
        lateinit var application: AngApplication
    }

    /**
     * Attaches the base context to the application.
     * @param base The base context.
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }

    private val workManagerConfiguration: Configuration = Configuration.Builder()
        .setDefaultProcessName("${ANG_PACKAGE}:bg")
        .build()

    fun configureLogger(builder: LogConfiguration.Builder) {
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) return

        builder.addInterceptor(object : AbstractFilterInterceptor() {
            override fun reject(log: LogItem): Boolean = AppLogger.isLoggingOn
        })
    }

    val streamingModules: Array<Module> = arrayOf(CommonKoinModule, WebRtcKoinModule)

    /**
     * Initializes the application.
     */
    override fun onCreate() {
        super.onCreate()

        MMKV.initialize(this)

        SettingsManager.setNightMode()
        // Initialize WorkManager with the custom configuration
        WorkManager.initialize(this, workManagerConfiguration)

        SettingsManager.initRoutingRulesets(this)

        es.dmoral.toasty.Toasty.Config.getInstance()
            .setGravity(android.view.Gravity.BOTTOM, 0, 200)
            .apply()

        if (ProcessPhoenix.isPhoenixProcess(this)) return

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .permitDiskReads()
                    .permitDiskWrites()
                    .penaltyLog()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .detectFileUriExposure()
                    .detectCleartextNetwork()
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) detectContentUriWithoutPermission()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) detectCredentialProtectedWhileLocked()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) detectUnsafeIntentLaunch()
                    }
                    .penaltyLog()
                    .build()
            )
        }

        AppLogger.init(this, ::configureLogger)

        val defaultModule = module {
            single(createdAtStart = true) { AdMob(get()) }
            single(createdAtStart = true) { AppStreamingAnalytics(get()) } bind (StreamingAnalytics::class)
            single { NotificationHelperImpl(get()) } bind (NotificationHelper::class)
        }

        startKoin {
            allowOverride(false)
            androidContext(this@AngApplication)
            modules(defaultModule, *streamingModules)
        }
    }
}
