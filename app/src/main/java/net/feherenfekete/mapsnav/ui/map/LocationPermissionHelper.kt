package net.feherenfekete.mapsnav.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import javax.inject.Inject

class LocationPermissionHelper @Inject constructor() {

    var onLocationPermissionGranted: (() -> Unit)? = null
    var onLocationPermissionDenied: (() -> Unit)? = null

    fun hasLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun ensurePermissions(
        fragment: Fragment,
        onLocationPermissionGranted: () -> Unit,
        onLocationPermissionDenied: () -> Unit
    ): Boolean {
        val c = fragment.context ?: return false
        if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            onLocationPermissionGranted()
            return true
        } else {
            this.onLocationPermissionGranted = onLocationPermissionGranted
            this.onLocationPermissionDenied = onLocationPermissionDenied
            fragment.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLocationPermissionGranted?.invoke()
            } else {
                onLocationPermissionDenied?.invoke()
            }
            onLocationPermissionGranted = null
            onLocationPermissionDenied = null
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1234
    }

}