package net.feherenfekete.mapsnav.repository.poi

import io.reactivex.Single
import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.model.PoiData
import net.feherenfekete.mapsnav.model.PoiInfoData

interface PoiDataSource {

    fun nearbyPois(
        location: LatLongData,
        radiusMeters: Int,
        resultLimit: Int = 10
    ): Single<List<PoiData>>

    fun poiInfo(poiId: Long): Single<PoiInfoData>

}
