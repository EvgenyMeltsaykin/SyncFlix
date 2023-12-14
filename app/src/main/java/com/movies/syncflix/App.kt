package com.movies.syncflix

import android.app.Application
import com.movies.syncflix.common.core.Environment
import com.movies.syncflix.di.platformModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin() {
            androidContext(this@App)
            modules(platformModule)
        }
        initLogger()
    }

    private fun initLogger() {
        if (Environment.isRelease) {
            //Napier.base(ReleaseAntilog())
        } else {
            Napier.base(DebugAntilog())
        }
    }
}