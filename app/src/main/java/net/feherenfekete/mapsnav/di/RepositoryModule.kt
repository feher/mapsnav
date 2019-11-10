package net.feherenfekete.mapsnav.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.feherenfekete.mapsnav.repository.directions.DirectionsDataSource
import net.feherenfekete.mapsnav.repository.directions.gmaps.GmapsDirectionsDataSource
import net.feherenfekete.mapsnav.repository.location.LocationDataSource
import net.feherenfekete.mapsnav.repository.location.fusedlocation.FusedLocationDataSource
import net.feherenfekete.mapsnav.repository.poi.PoiDataSource
import net.feherenfekete.mapsnav.repository.poi.wikipedia.WikiPoiDatasource

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindPoiDataSource(dataSource: WikiPoiDatasource): PoiDataSource

    @Binds
    abstract fun bindDirectionsDataSource(dataSource: GmapsDirectionsDataSource): DirectionsDataSource

    @Binds
    abstract fun bindLocationDataSource(dataSource: FusedLocationDataSource): LocationDataSource

    // Why do we need this companion object?
    // See https://stackoverflow.com/questions/48386794/provides-and-binds-methods-in-same-class-kotlin
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun fusedLocationProvider(context: Context) =
            LocationServices.getFusedLocationProviderClient(context)
    }

}
