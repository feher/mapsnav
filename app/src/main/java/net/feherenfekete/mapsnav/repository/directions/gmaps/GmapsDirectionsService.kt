package net.feherenfekete.mapsnav.repository.directions.gmaps

import io.reactivex.Single
import net.feherenfekete.mapsnav.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface GmapsDirectionsService {

    // https://maps.googleapis.com/maps/api/directions/json?parameters
    // origin=41.43206,-81.38992
    @GET("json?&key=${BuildConfig.MAPS_API_KEY}")
    fun directions(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") travelMode: String
    ) : Single<DirectionsResponse.Response>

}
