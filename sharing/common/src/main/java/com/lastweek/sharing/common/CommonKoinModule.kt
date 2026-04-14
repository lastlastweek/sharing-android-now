package com.lastweek.sharing.common

import com.lastweek.sharing.common.module.StreamingModuleManager
import com.lastweek.sharing.common.settings.AppSettings
import com.lastweek.sharing.common.settings.AppSettingsImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public val CommonKoinModule: Module = module {
    single(createdAtStart = true) { AppSettingsImpl(get()) } bind (AppSettings::class)
    single { StreamingModuleManager(getAll(), get()) }
}