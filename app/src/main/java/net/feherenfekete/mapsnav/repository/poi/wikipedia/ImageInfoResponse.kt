package net.feherenfekete.mapsnav.repository.poi.wikipedia

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

object ImageInfoResponse {

    @JsonClass(generateAdapter = true)
    data class Response(val query: QueryResponse)

    @JsonClass(generateAdapter = true)
    data class QueryResponse(val pages: Map<Long, PageResponse>)

    @JsonClass(generateAdapter = true)
    data class PageResponse(
        @Json(name = "imageinfo") val imageInfo: List<InnerImageInfoResponse> = emptyList()
    )

    @JsonClass(generateAdapter = true)
    data class InnerImageInfoResponse(
        @Json(name = "thumburl") val url: String = ""
    )

}
