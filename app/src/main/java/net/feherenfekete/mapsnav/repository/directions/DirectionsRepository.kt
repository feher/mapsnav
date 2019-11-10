package net.feherenfekete.mapsnav.repository.directions

import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.model.TravelMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectionsRepository @Inject constructor(private val directionsDataSource: DirectionsDataSource) {

    fun directions(origin: LatLongData, destination: LatLongData, mode: TravelMode) =
        directionsDataSource.directions(origin, destination, mode)

}
