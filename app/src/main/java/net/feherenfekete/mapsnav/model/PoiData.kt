package net.feherenfekete.mapsnav.model

data class PoiData(
    val id: Long = -1,
    val title: String = "",
    val location: LatLongData = LatLongData(),
    val distanceMeters: Float = 0.0f
) {

    fun isValid() = (id != -1L)

}
