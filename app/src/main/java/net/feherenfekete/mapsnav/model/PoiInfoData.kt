package net.feherenfekete.mapsnav.model

data class PoiInfoData(
    val pageId: Long = -1,
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val images: List<ImageData> = emptyList()
) {

    fun isValid() = (pageId != -1L)

}
