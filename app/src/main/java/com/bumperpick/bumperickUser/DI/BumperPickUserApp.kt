package com.bumperpick.bumperickUser.DI

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class BumperPickUserApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BumperPickUserApp)
            modules(appModule)
        }
    }
}