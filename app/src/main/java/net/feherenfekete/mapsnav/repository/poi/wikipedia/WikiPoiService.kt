package net.feherenfekete.mapsnav.repository.poi.wikipedia

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiPoiService {

    // https://en.wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&gscoord=60.1831906%7C24.9285439&gslimit=50&format=json
    @GET("api.php?action=query&list=geosearch&format=json")
    fun nearbyArticles(
        @Query("gscoord") latLong: String,
        @Query("gsradius") radiusMeters: Int,
        @Query("gslimit") resultLimit: Int = 10
    ): Single<NearbyArticlesResponse.Response>

    // https://en.wikipedia.org/w/api.php?action=query&prop=info%7Cdescription%7Cimages&pageids=18806750&format=json
    @GET("api.php?action=query&prop=info%7Cdescription%7Cimages&inprop=url&format=json")
    fun articleInfo(
        @Query("pageids") pageId: Long
    ): Single<ArticleInfoResponse.Response>

    // http://en.wikipedia.org/w/api.php?action=query&titles=File:Albert_Einstein_(Nobel).png&prop=imageinfo&iiprop=url&format=json
    @GET("api.php?action=query&prop=imageinfo&iiprop=url&iiurlwidth=512&format=json")
    fun imageInfo(
        @Query("titles") imageTitles: String
    ): Single<ImageInfoResponse.Response>

}
