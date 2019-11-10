package net.feherenfekete.mapsnav.repository.poi.wikipedia

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

object ArticleInfoResponse {

    @JsonClass(generateAdapter = true)
    data class Response(val query: QueryResponse)

    @JsonClass(generateAdapter = true)
    data class QueryResponse(val pages: Map<Long, PageResponse>)

    @JsonClass(generateAdapter = true)
    data class PageResponse(
        @Json(name = "pageid") val pageId: Long,
        @Json(name = "title") val title: String = "",
        @Json(name = "description") val description: String = "",
        @Json(name = "fullurl") val url: String = "",
        @Json(name = "images") val images: List<ImageResponse> = emptyList()
    )

    @JsonClass(generateAdapter = true)
    data class ImageResponse(
        @Json(name = "title") val title: String = ""
    )

}
