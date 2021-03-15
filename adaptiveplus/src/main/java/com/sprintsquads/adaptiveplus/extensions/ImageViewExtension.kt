package com.sprintsquads.adaptiveplus.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.sprintsquads.adaptiveplus.data.GLIDE_TIMEOUT

/**
 * Extension function to load and display image in ImageView
 *
 * @param url - image url
 * @param defaultDrawable - default drawable (placeholder)
 * @param onResourceReady - on resource loaded callback
 * @param cornerRadius - corner radius to round the image
 */
internal fun ImageView.loadImage(
    url: String,
    defaultDrawable: Drawable? = null,
    onResourceReady: (() -> Unit)? = null,
    cornerRadius: Int? = null
) {
    val requestOptions =
        if (cornerRadius != null && cornerRadius > 0) {
            RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))
        } else {
            RequestOptions()
        }

    Glide
        .with(context)
        .load(url)
        .timeout(GLIDE_TIMEOUT)
        .placeholder(defaultDrawable)
        .listener(object: RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onResourceReady?.invoke()
                return false
            }
        })
        .apply(requestOptions)
        .into(this)
}