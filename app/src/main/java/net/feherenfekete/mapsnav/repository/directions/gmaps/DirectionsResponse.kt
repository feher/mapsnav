package net.feherenfekete.mapsnav.repository.directions.gmaps

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

object DirectionsResponse {

    @JsonClass(generateAdapter = true)
    data class Response(
        @Json(name = "status") val status: String = "",
        @Json(name = "routes") val routes: List<RouteResponse> = emptyList()
    )

    @JsonClass(generateAdapter = true)
    data class RouteResponse(
        @Json(name = "legs") val legs: List<LegResponse> = emptyList(),
        @Json(name = "overview_polyline") val polyline: PolylineResponse = PolylineResponse(),
        @Json(name = "bounds") val bounds: BoundsResponse = BoundsResponse()
    )

    @JsonClass(generateAdapter = true)
    data class LegResponse(
        @Json(name = "duration") val duration: DurationResponse = DurationResponse(),
        @Json(name = "distance") val distance: DistanceResponse = DistanceResponse(),
        @Json(name = "start_address") val startAddress: String = "",
        @Json(name = "end_address") val endAddress: String = ""
    )

    @JsonClass(generateAdapter = true)
    data class DurationResponse(
        @Json(name = "value") val value: Long = 0,
        @Json(name = "text") val text: String = ""
    )

    @JsonClass(generateAdapter = true)
    data class DistanceResponse(
        @Json(name = "value") val value: Long = 0,
        @Json(name = "text") val text: String = ""
    )

    @JsonClass(generateAdapter = true)
    data class PolylineResponse(
        @Json(name = "points") val points: String = ""
    )

    @JsonClass(generateAdapter = true)
    data class BoundsResponse(
        @Json(name = "southwest") val southWest: LatLongResponse = LatLongResponse(),
        @Json(name = "northeast") val northEast: LatLongResponse = LatLongResponse()
    )

    @JsonClass(generateAdapter = true)
    data class LatLongResponse(
        @Json(name = "lat") val latitude: Double = 0.0,
        @Json(name = "lng") val longitude: Double = 0.0
    )

}
