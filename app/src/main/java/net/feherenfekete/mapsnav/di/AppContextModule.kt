package net.feherenfekete.mapsnav.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppContextModule(val appContext: Context)
{

    @Provides
    fun appContext() = appContext

}
