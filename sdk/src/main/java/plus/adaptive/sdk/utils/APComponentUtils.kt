package plus.adaptive.sdk.utils

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import plus.adaptive.sdk.data.GLIDE_TIMEOUT
import plus.adaptive.sdk.data.models.APFont


internal fun preloadImage(
    context: Context,
    url: String,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null
) {
    Glide.with(context)
        .load(url)
        .timeout(GLIDE_TIMEOUT)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .listener(object: RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFailed?.invoke()
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
        .preload()
}

internal fun preloadGIF(
    context: Context,
    url: String,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null
) {
    preloadImage(context, url, onResourceReady, onLoadFailed)
}

internal fun preloadAPFont(
    context: Context,
    apFont: APFont,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null
) {
    requestFontDownload(
        context = context,
        familyName = apFont.family,
        fontStyle = apFont.style,
        onSuccess = { onResourceReady?.invoke() },
        onError = { onLoadFailed?.invoke() }
    )
}