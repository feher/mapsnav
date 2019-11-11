package net.feherenfekete.mapsnav

import com.squareup.moshi.Moshi
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.repository.poi.wikipedia.NearbyArticlesResponse
import net.feherenfekete.mapsnav.repository.poi.wikipedia.WikiPoiDatasource
import net.feherenfekete.mapsnav.repository.poi.wikipedia.WikiPoiService
import net.feherenfekete.mapsnav.rx.RxSchedulers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class WikiPoiDatasourceTest {

    private val moshi = Moshi.Builder().build()
    private val jsonAdapter =
        moshi.adapter<NearbyArticlesResponse.Response>(NearbyArticlesResponse.Response::class.java)

    private val service: WikiPoiService = mockk()
    private val schedulers: RxSchedulers = mockk()

    @BeforeEach
    fun setup() {
        clearAllMocks()
        every { schedulers.io() } returns Schedulers.trampoline()
    }

    @Test
    fun nearbyPois_canParseCompleteInput() {

        every {
            service.nearbyArticles(any(), any(), any())
        } answers {
            Single.just(jsonAdapter.fromJson(completeInput))
        }

        val datasource = WikiPoiDatasource(service, schedulers)
        val pois = datasource.nearbyPois(LatLongData(), 0, 0).blockingGet()
        assertEquals(3, pois.size)
        assertEquals(18806750, pois[0].id)
        assertEquals("T\u00f6\u00f6l\u00f6 Sports Hall", pois[0].title)
        assertEquals(60.18333333333333, pois[0].location.latitude)
        assertEquals(24.925555555555558, pois[0].location.longitude)
        assertEquals(166.0f, pois[0].distanceMeters)
    }

    @Test
    fun nearbyPois_canParseMissingInput() {
        every {
            service.nearbyArticles(any(), any(), any())
        } answers {
            Single.just(jsonAdapter.fromJson(incompleteInput))
        }

        val datasource = WikiPoiDatasource(service, schedulers)
        val pois = datasource.nearbyPois(LatLongData(), 0, 0).blockingGet()

        assertEquals(3, pois.size)

        assertEquals(18806750, pois[0].id)
        assertEquals("T\u00f6\u00f6l\u00f6 Sports Hall", pois[0].title)
        assertEquals(0.0, pois[0].location.latitude)
        assertEquals(0.0, pois[0].location.longitude)
        assertEquals(166.0f, pois[0].distanceMeters)

        assertEquals(-1, pois[1].id)
        assertEquals("", pois[1].title)
        assertEquals(0.0, pois[1].location.latitude)
        assertEquals(0.0, pois[1].location.longitude)
        assertEquals(0.0f, pois[1].distanceMeters)

        assertEquals(-1, pois[2].id)
        assertEquals("T\u00f6\u00f6l\u00f6n Pallokentt\u00e4", pois[2].title)
        assertEquals(60.18583333333333, pois[2].location.latitude)
        assertEquals(24.924722222222222, pois[2].location.longitude)
        assertEquals(361.9f, pois[2].distanceMeters)
    }

    val completeInput = """
    { 
       "batchcomplete":"",
       "query":{ 
          "geosearch":[ 
             { 
                "pageid":18806750,
                "ns":0,
                "title":"T\u00f6\u00f6l\u00f6 Sports Hall",
                "lat":60.18333333333333,
                "lon":24.925555555555558,
                "dist":166,
                "primary":""
             },
             { 
                "pageid":537137,
                "ns":0,
                "title":"Finnish National Opera and Ballet",
                "lat":60.181666666666665,
                "lon":24.929722222222225,
                "dist":181.5,
                "primary":""
             },
             { 
                "pageid":4852943,
                "ns":0,
                "title":"T\u00f6\u00f6l\u00f6n Pallokentt\u00e4",
                "lat":60.18583333333333,
                "lon":24.924722222222222,
                "dist":361.9,
                "primary":""
             }
          ]
       }
    }
    """.trimIndent()

    val incompleteInput = """
    { 
       "batchcomplete":"",
       "query":{ 
          "geosearch":[ 
             { 
                "pageid":18806750,
                "ns":0,
                "title":"T\u00f6\u00f6l\u00f6 Sports Hall",
                "dist":166,
                "primary":""
             },
             { 
             },
             { 
                "title":"T\u00f6\u00f6l\u00f6n Pallokentt\u00e4",
                "lat":60.18583333333333,
                "lon":24.924722222222222,
                "dist":361.9,
                "primary":""
             }
          ]
       }
    }
    """.trimIndent()

}
