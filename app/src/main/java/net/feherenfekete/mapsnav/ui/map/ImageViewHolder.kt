package net.feherenfekete.mapsnav.ui.map

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_item.view.*
import net.feherenfekete.mapsnav.R
import net.feherenfekete.mapsnav.ui.glide.GlideApp

class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(imageUrl: String) {
        val radius = itemView.resources.getDimensionPixelSize(R.dimen.image_corner_radius)
        val requestOptions = RequestOptions().apply {
            transform(CenterCrop(), RoundedCorners(radius))
        }
        GlideApp.with(itemView)
            .load(Uri.parse(imageUrl))
            .apply(requestOptions)
            .placeholder(R.drawable.ic_image)
            .into(itemView.image)
    }

}
