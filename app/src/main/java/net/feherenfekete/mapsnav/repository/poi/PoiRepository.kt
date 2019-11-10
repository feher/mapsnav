package net.feherenfekete.mapsnav.repository.poi

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.processors.BehaviorProcessor
import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.model.PoiData
import net.feherenfekete.mapsnav.model.PoiInfoData
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PoiRepository @Inject constructor(private val poiDataSource: PoiDataSource) {

    private data class LocationInfo(
        val location: LatLongData,
        val radiusMeters: Int
    )

    private val locationBehavior = BehaviorProcessor.create<LocationInfo>()

    fun setLocation(location: LatLongData, radiusMeters: Int) {
        locationBehavior.offer(LocationInfo(location, radiusMeters))
    }

    fun nearbyPois(resultLimit: Int): Flowable<List<PoiData>> =
        locationBehavior
            .sample(1000, TimeUnit.MILLISECONDS)
            .map {
                poiDataSource.nearbyPois(
                    it.location, it.radiusMeters, resultLimit
                ).blockingGet()
            }

    fun poiInfo(poiId: Long): Single<PoiInfoData> = poiDataSource.poiInfo(poiId)

}
