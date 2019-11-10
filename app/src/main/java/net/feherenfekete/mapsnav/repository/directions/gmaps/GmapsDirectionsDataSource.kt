package net.feherenfekete.mapsnav.repository.directions.gmaps

import com.google.maps.android.PolyUtil
import io.reactivex.Single
import net.feherenfekete.mapsnav.model.DirectionData
import net.feherenfekete.mapsnav.repository.directions.DirectionsDataSource
import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.model.TravelMode
import net.feherenfekete.mapsnav.rx.RxSchedulers
import javax.inject.Inject

class GmapsDirectionsDataSource @Inject constructor(
    private val gmapsDirectionsService: GmapsDirectionsService,
    private val rxSchedulers: RxSchedulers
) : DirectionsDataSource {

    /**
     * @param mode Ignored. Always TravelMode.Walking is used.
     */
    override fun directions(
        origin: LatLongData,
        destination: LatLongData,
        mode: TravelMode
    ): Single<DirectionData> {
        return gmapsDirectionsService.directions(
            "${origin.latitude},${origin.longitude}",
            "${destination.latitude},${destination.longitude}",
            "walking"
        ).flatMap {
            Single.just(extractDirectionData(TravelMode.Walking, it))
        }.subscribeOn(rxSchedulers.io())
    }

    private fun extractDirectionData(travelMode: TravelMode, response: DirectionsResponse.Response): DirectionData {
        if (response.routes.isNotEmpty()) {
            val route = response.routes[0]
            val leg = if (route.legs.isNotEmpty()) {
                route.legs[0]
            } else {
                DirectionsResponse.LegResponse()
            }
            val encodedPolyline = route.polyline.points
            val polyline = PolyUtil.decode(encodedPolyline).map {
                LatLongData(it.latitude, it.longitude)
            }
            return DirectionData(
                leg.startAddress,
                leg.endAddress,
                leg.duration.text,
                leg.distance.text,
                travelMode,
                polyline
            )
        } else {
            return DirectionData()
        }
    }

}
