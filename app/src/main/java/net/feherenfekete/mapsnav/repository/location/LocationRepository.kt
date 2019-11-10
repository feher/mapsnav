package net.feherenfekete.mapsnav.repository.location

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(private val locationDataSource: LocationDataSource) {

    fun location() = locationDataSource.location()

}
