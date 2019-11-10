package net.feherenfekete.mapsnav.ui.map

import com.google.android.gms.maps.model.LatLng
import net.feherenfekete.mapsnav.model.LatLongData

fun LatLongData.asLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun LatLng.asLatLongData(): LatLongData {
    return LatLongData(this.latitude, this.longitude)
}
