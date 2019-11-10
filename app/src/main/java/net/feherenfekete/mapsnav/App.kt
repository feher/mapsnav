package net.feherenfekete.mapsnav

import android.app.Application
import net.feherenfekete.mapsnav.di.AppComponent
import net.feherenfekete.mapsnav.di.AppContextModule
import net.feherenfekete.mapsnav.di.DaggerAppComponent
import timber.log.Timber
import timber.log.Timber.DebugTree


class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        appComponent = DaggerAppComponent.builder()
            .appContextModule(AppContextModule(this))
            .build()

        super.onCreate()

        Timber.plant(DebugTree())
    }

}
