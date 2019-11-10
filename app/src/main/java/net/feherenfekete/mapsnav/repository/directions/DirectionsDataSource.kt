package net.feherenfekete.mapsnav.repository.directions

import io.reactivex.Single
import net.feherenfekete.mapsnav.model.DirectionData
import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.model.TravelMode

interface DirectionsDataSource {

    fun directions(
        origin: LatLongData,
        destination: LatLongData,
        mode: TravelMode
    ): Single<DirectionData>

}