package net.feherenfekete.mapsnav.repository.location

import io.reactivex.Single
import net.feherenfekete.mapsnav.model.LatLongData

interface LocationDataSource {

    fun location(): Single<LatLongData>

}