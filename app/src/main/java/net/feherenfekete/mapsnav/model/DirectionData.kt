package net.feherenfekete.mapsnav.model

data class DirectionData(
    val startAddress: String = "",
    val endAddress: String = "",
    val duration: String = "",
    val distance: String = "",
    val travelMode: TravelMode = TravelMode.Walking,
    val path: List<LatLongData> = emptyList()
)
