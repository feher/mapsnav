package net.feherenfekete.mapsnav.di

import dagger.Component
import net.feherenfekete.mapsnav.ui.map.MapFragment
import javax.inject.Singleton

@Component(
    modules = [
        AppContextModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(fragment: MapFragment)
}
