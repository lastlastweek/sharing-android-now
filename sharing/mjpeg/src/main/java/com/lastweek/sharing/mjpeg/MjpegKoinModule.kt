package com.lastweek.sharing.mjpeg

import com.lastweek.sharing.common.module.StreamingModule
import com.lastweek.sharing.mjpeg.internal.MjpegStreamingService
import com.lastweek.sharing.mjpeg.internal.NetworkHelper
import com.lastweek.sharing.mjpeg.settings.MjpegSettings
import com.lastweek.sharing.mjpeg.settings.MjpegSettingsImpl
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.StringQualifier
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

public class MjpegKoinScope : KoinScopeComponent {
    override val scope: Scope by lazy(LazyThreadSafetyMode.NONE) { createScope(this) }
}

internal val MjpegKoinQualifier: Qualifier = StringQualifier("MjpegStreamingModule")

public val MjpegKoinModule: org.koin.core.module.Module = module {
    single(MjpegKoinQualifier) { MjpegStreamingModule() } bind (StreamingModule::class)
    single { MjpegSettingsImpl(context = get()) } bind (MjpegSettings::class)
    scope<MjpegKoinScope> {
        scoped { NetworkHelper(get()) }
        scoped { params ->
            MjpegStreamingService(
                service = params.get(),
                mutableMjpegStateFlow = params.get(),
                networkHelper = get(),
                mjpegSettings = get(),
                streamingAnalytics = get()
            )
        } bind (MjpegStreamingService::class)
    }
}
