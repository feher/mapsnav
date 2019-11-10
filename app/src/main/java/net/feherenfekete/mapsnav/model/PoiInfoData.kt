package net.feherenfekete.mapsnav.model

import net.feherenfekete.mapsnav.model.ImageData

data class PoiInfoData(
    val pageId: Long = -1,
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val images: List<ImageData> = emptyList()
) {

    fun isValid() = (pageId != -1L)

}
