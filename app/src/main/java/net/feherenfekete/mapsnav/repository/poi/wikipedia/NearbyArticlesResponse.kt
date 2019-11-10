package net.feherenfekete.mapsnav.repository.poi.wikipedia

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

object NearbyArticlesResponse {

    @JsonClass(generateAdapter = true)
    data class Response(val query: QueryResponse)

    @JsonClass(generateAdapter = true)
    data class QueryResponse(val geosearch: List<ArticleResponse>)

    @JsonClass(generateAdapter = true)
    data class ArticleResponse(
        @Json(name = "pageid") val pageId: Long,
        @Json(name = "title") val title: String = "",
        @Json(name = "lat") val latitude: Double = 0.0,
        @Json(name = "lon") val longitude: Double = 0.0,
        @Json(name = "dist") val distanceMeters: Float = 0.0f
    )

}
