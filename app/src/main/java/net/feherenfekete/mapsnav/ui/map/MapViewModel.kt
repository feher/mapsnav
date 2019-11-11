package net.feherenfekete.mapsnav.ui.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import net.feherenfekete.mapsnav.model.*
import net.feherenfekete.mapsnav.repository.directions.DirectionsRepository
import net.feherenfekete.mapsnav.repository.location.LocationRepository
import net.feherenfekete.mapsnav.repository.poi.PoiRepository
import net.feherenfekete.mapsnav.rx.RxSchedulers
import timber.log.Timber
import javax.inject.Inject


class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val poiRepository: PoiRepository,
    private val directionsRepository: DirectionsRepository,
    private val rxSchedulers: RxSchedulers
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    var currentNearbyPois = listOf<PoiData>()
        private set

    var currentDirections = DirectionData()
        private set

    var selectedPoi = PoiData()
        private set

    var currentPoiInfo = PoiInfoData()
        private set

    // These are for optimization. We don't want to re-fetch the directions if we
    // already have them in cachedDirections. It can be costly (i.e. Google Maps API
    // costs money).
    private var cachedDirections = DirectionData()
    private var cachedDirectionOrigin = LatLongData()
    private var cachedDirectionDestination = LatLongData()

    open class Event
    class NearbyPoisLoadedEvent : Event()
    class DirectionsLoadedEvent : Event()
    class DirectionsClearedEvent : Event()
    class PoiSelectedEvent : Event()
    class PoiUnselectedEvent : Event()
    class PoiInfoLoadedEvent : Event()

    private val modelChangedEvent = PublishProcessor.create<Event>()

    init {
        compositeDisposable.add(
            poiRepository.nearbyPois(NEARBY_POI_LIMIT)
                .observeOn(rxSchedulers.main())
                .subscribe({
                    currentNearbyPois = it
                    modelChangedEvent.offer(NearbyPoisLoadedEvent())
                }, {
                    Timber.e(it)
                })
        )
    }

    fun modelChanged(): Flowable<Event> = modelChangedEvent.onBackpressureLatest()

    fun userLocation() = locationRepository.location()

    fun setViewLocation(location: LatLongData, radiusMeters: Int) {
        poiRepository.setLocation(location, radiusMeters)
    }

    fun selectPoi(location: LatLongData) {
        val poi = getPoi(location) ?: return
        selectedPoi = poi
        compositeDisposable.add(
            poiRepository.poiInfo(poi.id)
                .observeOn(rxSchedulers.main())
                .subscribe({
                    currentPoiInfo = it
                    modelChangedEvent.offer(PoiInfoLoadedEvent())
                }, {
                    Timber.e(it)
                })
        )
        modelChangedEvent.offer(PoiSelectedEvent())
    }

    fun isSelectedPoi(poi: PoiData) = (poi.id == selectedPoi.id)

    fun hasSelectedPoi() = selectedPoi.isValid()

    fun clearSelectedPoi() {
        selectedPoi = PoiData()
        modelChangedEvent.offer(PoiUnselectedEvent())
    }

    fun poiInfo(poiId: Long) = poiRepository.poiInfo(poiId)

    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    fun fetchDirections() {
        compositeDisposable.add(
            userLocation()
                .observeOn(rxSchedulers.main())
                .flatMap {
                    if (hasCachedDirections(it, selectedPoi.location)) {
                        Single.just(cachedDirections)
                    } else {
                        cachedDirectionOrigin = it
                        cachedDirectionDestination = selectedPoi.location
                        directionsRepository.directions(
                            it,
                            selectedPoi.location,
                            TravelMode.Walking
                        )
                    }
                }
                .subscribe({
                    currentDirections = it
                    cachedDirections = it
                    modelChangedEvent.offer(DirectionsLoadedEvent())
                }, {
                    Timber.e(it)
                })
        )
    }

    fun hasDirections() = currentDirections.path.isNotEmpty()

    fun clearDirections() {
        currentDirections = DirectionData()
        modelChangedEvent.offer(DirectionsClearedEvent())
    }

    private fun getPoi(location: LatLongData) = currentNearbyPois.find {
        it.location == location
    }

    private fun hasCachedDirections(from: LatLongData, to: LatLongData): Boolean {
        return from.isEqualWithTolerance(cachedDirectionOrigin) &&
                to.isEqualWithTolerance(cachedDirectionDestination)
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    companion object {
        private const val NEARBY_POI_LIMIT = 50
    }

}
