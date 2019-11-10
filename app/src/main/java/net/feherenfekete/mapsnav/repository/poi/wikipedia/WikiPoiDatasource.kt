package net.feherenfekete.mapsnav.repository.poi.wikipedia

import io.reactivex.Single
import net.feherenfekete.mapsnav.model.ImageData
import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.model.PoiData
import net.feherenfekete.mapsnav.repository.poi.PoiDataSource
import net.feherenfekete.mapsnav.model.PoiInfoData
import net.feherenfekete.mapsnav.rx.RxSchedulers
import javax.inject.Inject

class WikiPoiDatasource @Inject constructor(
    private val wikiPoiService: WikiPoiService,
    private val rxSchedulers: RxSchedulers
) : PoiDataSource {

    override fun nearbyPois(
        location: LatLongData,
        radiusMeters: Int,
        resultLimit: Int
    ): Single<List<PoiData>> {
        // Wikipedia does not support larger radius than 10000 meters
        val r = Math.min(radiusMeters, 10000)
        return wikiPoiService.nearbyArticles("${location.latitude}|${location.longitude}", r, resultLimit)
            .subscribeOn(rxSchedulers.io())
            .flatMap {
                Single.just(extractPois(it.query.geosearch))
            }
    }

    override fun poiInfo(poiId: Long): Single<PoiInfoData> {
        return wikiPoiService.articleInfo(poiId)
            .subscribeOn(rxSchedulers.io())
            .flatMap {
                val firstPage = extractFirstPageResponse(it.query.pages)
                if (firstPage != null) {
                    val imageTitles = extractImageTitles(firstPage.images)
                    Single.just(
                        PoiInfoData(
                            firstPage.pageId,
                            firstPage.title,
                            firstPage.description,
                            firstPage.url,
                            if (imageTitles.isNotEmpty()) {
                                extractImageData(
                                    wikiPoiService.imageInfo(imageTitles).blockingGet().query.pages
                                )
                            } else {
                                emptyList()
                            }
                        )
                    )
                } else {
                    Single.just(PoiInfoData())
                }
            }
    }

    private fun extractPois(articles: List<NearbyArticlesResponse.ArticleResponse>): List<PoiData> {
        return articles.map {
            PoiData(
                it.pageId,
                it.title,
                LatLongData(it.latitude, it.longitude),
                it.distanceMeters
            )
        }
    }

    private fun extractImageData(
        pageMap: Map<Long, ImageInfoResponse.PageResponse>
    ): List<ImageData> {
        val nonSvg = pageMap.values.filter {
            !it.imageInfo[0].url.endsWith(".svg")
        }
        return nonSvg.map {
            ImageData(it.imageInfo[0].url)
        }
    }

    private fun extractImageTitles(
        imageResponses: List<ArticleInfoResponse.ImageResponse>
    ): String {
        return if (imageResponses.isNotEmpty()) {
            val imageTitles = imageResponses.fold("") { sum, item ->
                "$sum|${item.title}"
            }
            imageTitles.substring(1)
        } else {
            ""
        }
    }

    private fun extractFirstPageResponse(
        pagesMap: Map<Long, ArticleInfoResponse.PageResponse>
    ): ArticleInfoResponse.PageResponse? {
        return if (pagesMap.isNotEmpty()) {
            val firstKey = pagesMap.keys.iterator().next()
            pagesMap[firstKey]
        } else {
            null
        }
    }

}
