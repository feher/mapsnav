package net.feherenfekete.mapsnav.ui.map

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.SphericalUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.directions_info.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.poi_info.*
import net.feherenfekete.mapsnav.App
import net.feherenfekete.mapsnav.R
import net.feherenfekete.mapsnav.rx.RxSchedulers
import timber.log.Timber
import javax.inject.Inject


class MapFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var locationPermissionHelper: LocationPermissionHelper

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var rxSchedulers: RxSchedulers

    private lateinit var viewModel: MapViewModel
    private lateinit var poiImageAdapter: ImageAdapter

    private var googleMap: GoogleMap? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[MapViewModel::class.java]
        poiImageAdapter = ImageAdapter()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        poiImagesRecyclerView.adapter = poiImageAdapter
        poiImagesRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        fab.setOnClickListener {
            onFabClicked()
        }

        hideBottomSheet()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googMap: GoogleMap) {
        googleMap = googMap

        googMap.getUiSettings().setMapToolbarEnabled(false);

        googMap.setOnMapClickListener {
            toggleBottomSheet()

            // HACK: This is needed because GoogleMaps removes marker windows when tapping.
            updateUi(MapViewModel.Event())
        }

        googMap.setOnCameraIdleListener {
            setViewLocation()
        }

        googMap.setOnMarkerClickListener {
            viewModel.selectPoi(it.position.asLatLongData())
            true
        }

        compositeDisposable.add(
            viewModel.modelChanged()
                .observeOn(rxSchedulers.main())
                .subscribe({
                    updateUi(it)
                }, {
                    Timber.e(it)
                })
        )

        locationPermissionHelper.ensurePermissions(
            this,
            ::onLocationPermissionGranted,
            ::onLocationPermissionDenied
        )

        val berlin = LatLng(52.52, 13.405)
        googMap.moveCamera(CameraUpdateFactory.newLatLngZoom(berlin, DEFAULT_ZOOM))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    private fun onLocationPermissionDenied() {
        hideFab()
    }

    private fun onLocationPermissionGranted() {
        googleMap?.setMyLocationEnabled(true)
        compositeDisposable.add(
            viewModel.userLocation()
                .observeOn(rxSchedulers.main())
                .subscribe({ loc ->
                    val latlng = LatLng(loc.latitude, loc.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM))
                }, {
                    Timber.e(it)
                })
        )
    }

    private fun setViewLocation() {
        val map = googleMap ?: return
        val viewPort = map.getProjection().getVisibleRegion()
        val height = SphericalUtil.computeDistanceBetween(viewPort.nearLeft, viewPort.farLeft)
        val width = SphericalUtil.computeDistanceBetween(viewPort.nearLeft, viewPort.nearRight)
        val radiusMeters = (Math.max(height, width) / 2).toInt()
        viewModel.setViewLocation(
            map.cameraPosition.target.asLatLongData(),
            radiusMeters
        )
    }

    private fun updateUi(event: MapViewModel.Event) {
        val map = googleMap ?: return
        map.clear()
        drawPois(event)
        drawDirections()
        updateFab()
        updatePoiInfo(event)
        updateDirectionInfo(event)
    }

    private fun drawPois(event: MapViewModel.Event) {
        val map = googleMap ?: return

        if (viewModel.hasDirections()) {
            return
        }

        viewModel.currentNearbyPois.forEach {
            val marker = map.addMarker(
                MarkerOptions()
                    .position(it.location.asLatLng())
                    .title(it.title)
            )
            if (viewModel.isSelectedPoi(it)) {
                marker.showInfoWindow()
                if (event is MapViewModel.PoiSelectedEvent) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
                }
            }
        }
    }

    private fun drawDirections() {
        val map = googleMap ?: return

        if (!viewModel.hasDirections()) {
            return
        }

        val polylineOptions = PolylineOptions()
        viewModel.currentDirections.path.forEach {
            polylineOptions.add(LatLng(it.latitude, it.longitude))
        }
        map.addPolyline(polylineOptions)

        map.addMarker(
            MarkerOptions()
                .position(viewModel.selectedPoi.location.asLatLng())
                .title(viewModel.currentPoiInfo.title)
        ).showInfoWindow()
    }

    private fun updateFab() {
        val c = context ?: return
        if (locationPermissionHelper.hasLocationPermission(c)) {
            if (viewModel.hasDirections()) {
                fab.setImageResource(R.drawable.ic_close)
                showFab()
            } else if (viewModel.hasSelectedPoi()) {
                fab.setImageResource(R.drawable.ic_directions)
                showFab()
            } else {
                hideFab()
            }
        } else {
            hideFab()
        }
    }

    private fun updatePoiInfo(event: MapViewModel.Event) {
        if (viewModel.hasDirections()) {
            return
        }

        poiInfoLayout.visibility = View.VISIBLE
        directionsInfoLayout.visibility = View.GONE

        val poiInfo = viewModel.currentPoiInfo
        poiTitle.text = poiInfo.title
        poiDescription.text = poiInfo.description
        poiWikiLink.setOnClickListener {
            context?.let {
                viewModel.openUrl(it, poiInfo.url)
            }
        }
        poiImageAdapter.setItems(poiInfo.images.map { it.url })

        if (event is MapViewModel.PoiSelectedEvent) {
            showBottomSheet()
        }
    }

    private fun updateDirectionInfo(event: MapViewModel.Event) {
        if (!viewModel.hasDirections()) {
            return
        }

        directionsInfoLayout.visibility = View.VISIBLE
        poiInfoLayout.visibility = View.GONE

        val directions = viewModel.currentDirections

        directionTitle.text = viewModel.currentPoiInfo.title
        directionTravelModeValue.text = directions.travelMode.name
        directionStartAddressValue.text = directions.startAddress
        directionEndAddressValue.text = directions.endAddress
        directionDistanceValue.text = directions.distance
        directionDurationValue.text = directions.duration

        if (event is MapViewModel.DirectionsRequestedEvent) {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideBottomSheet() {
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun toggleBottomSheet() {
        val sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (sheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            if (viewModel.hasSelectedPoi() || viewModel.hasDirections()) {
                showBottomSheet()
            }
        } else {
            hideBottomSheet()
        }
    }

    private fun showFab() {
        fab.visibility = View.VISIBLE
        fab.animate()
            .scaleX(1.0f).scaleY(1.0f).setDuration(300)
            .setListener(null)
            .start()
    }

    private fun hideFab() {
        fab.animate()
            .scaleX(0.0f).scaleY(0.0f).setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fab.visibility = View.GONE
                }
            })
            .start()
    }

    private fun onFabClicked() {
        if (viewModel.hasDirections()) {
            viewModel.clearDirections()
        } else {
            viewModel.fetchDirections()
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 12.0f
    }

}
