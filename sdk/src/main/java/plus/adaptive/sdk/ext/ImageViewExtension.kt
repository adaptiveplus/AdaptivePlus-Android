package plus.adaptive.sdk.ext

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import plus.adaptive.sdk.core.glide.GlideProgressTarget
import plus.adaptive.sdk.data.GLIDE_TIMEOUT

/**
 * Extension function to load and display image in ImageView
 *
 * @param url - image url
 * @param defaultDrawable - default drawable (placeholder)
 * @param cornerRadius - corner radius to round the image
 * @param onResourceReady - on resource loaded callback
 * @param onLoadFailed - on resource load failed callback
 */
internal fun ImageView.loadImage(
    url: String,
    defaultDrawable: Drawable? = null,
    cornerRadius: Int? = null,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null
) {
    val requestOptions =
        if (cornerRadius != null && cornerRadius > 0) {
            RequestOptions
                .bitmapTransform(RoundedCorners(cornerRadius))
                .fitCenter()
        } else {
            RequestOptions()
        }

    Glide
        .with(context)
        .asBitmap()
        .load(url)
        .centerCrop()
        .timeout(GLIDE_TIMEOUT)
        .placeholder(defaultDrawable)
        .listener(object: RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFailed?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
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

/**
 * Extension function to load and display image in ImageView
 *
 * @param url - image url
 * @param defaultDrawable - default drawable (placeholder)
 * @param cornerRadius - corner radius to round the image
 * @param onResourceReady - on resource loaded callback
 * @param onLoadFailed - on resource load failed callback
 * @param onLoadProgressUpdate - on resource load progress update callback
 */
internal fun ImageView.loadImage(
    url: String,
    defaultDrawable: Drawable? = null,
    cornerRadius: Int? = null,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null,
    onLoadProgressUpdate: ((progress: Float) -> Unit)? = null
) {
    val imageViewTarget = object: ImageViewTarget<Bitmap>(this) {
        override fun setResource(resource: Bitmap?) {
            loadImage(
                url = url,
                defaultDrawable = defaultDrawable,
                cornerRadius = cornerRadius,
                onResourceReady = onResourceReady,
                onLoadFailed = onLoadFailed
            )
        }
    }
    val target = object: GlideProgressTarget<String, Bitmap>(url, imageViewTarget) {
        override fun onDownloading(bytesRead: Long, expectedLength: Long) {
            super.onDownloading(bytesRead, expectedLength)
            val progress = bytesRead.toFloat() / expectedLength
            onLoadProgressUpdate?.invoke(progress)
        }

        override fun getGranularityPercentage(): Float = 1f
    }

    Glide
        .with(context)
        .asBitmap()
        .load(url)
        .centerCrop()
        .timeout(GLIDE_TIMEOUT)
        .placeholder(defaultDrawable)
        .into(target)
}

/**
 * Extension function to load and display GIF in ImageView
 *
 * @param url - image url
 * @param defaultDrawable - default drawable (placeholder)
 * @param cornerRadius - corner radius to round the image
 * @param onResourceReady - on resource loaded callback
 * @param onLoadFailed - on resource load failed callback
 * @param onLoadProgressUpdate - on resource load progress update callback
 */
internal fun ImageView.loadGIF(
    url: String,
    defaultDrawable: Drawable? = null,
    cornerRadius: Int? = null,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null,
    onLoadProgressUpdate: ((progress: Float) -> Unit)? = null
) {
    val requestOptions =
        if (cornerRadius != null && cornerRadius > 0) {
            RequestOptions.bitmapTransform(RoundedCorners(cornerRadius))
        } else {
            RequestOptions()
        }

    val imageViewTarget = object: ImageViewTarget<GifDrawable>(this) {
        override fun setResource(resource: GifDrawable?) {
            setImageDrawable(resource)
        }
    }
    val target = object: GlideProgressTarget<String, GifDrawable>(url, imageViewTarget) {
        override fun onDownloading(bytesRead: Long, expectedLength: Long) {
            super.onDownloading(bytesRead, expectedLength)
            val progress = bytesRead.toFloat() / expectedLength
            onLoadProgressUpdate?.invoke(progress)
        }

        override fun getGranularityPercentage(): Float = 1f
    }

    Glide
        .with(context)
        .asGif()
        .load(url)
        .timeout(GLIDE_TIMEOUT)
        .placeholder(defaultDrawable)
        .listener(object: RequestListener<GifDrawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFailed?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable?,
                model: Any?,
                target: Target<GifDrawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onResourceReady?.invoke()
                return false
            }
        })
        .apply(requestOptions)
        .into(target)
}




internal fun ImageView.loadCircleImage(
    url: String,
    defaultDrawable: Drawable? = null,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null,
    onLoadProgressUpdate: ((progress: Float) -> Unit)? = null
) {
    val imageViewTarget = object: ImageViewTarget<Bitmap>(this) {
        override fun setResource(resource: Bitmap?) {
            loadCircleImg(
                url = url,
                defaultDrawable = defaultDrawable,
                onResourceReady = onResourceReady,
                onLoadFailed = onLoadFailed
            )
        }
    }
    val target = object: GlideProgressTarget<String, Bitmap>(url, imageViewTarget) {
        override fun onDownloading(bytesRead: Long, expectedLength: Long) {
            super.onDownloading(bytesRead, expectedLength)
            val progress = bytesRead.toFloat() / expectedLength
            onLoadProgressUpdate?.invoke(progress)
        }

        override fun getGranularityPercentage(): Float = 1f
    }

    Glide
        .with(context)
        .asBitmap()
        .load(url)
        .timeout(GLIDE_TIMEOUT)
        .centerCrop()
        .circleCrop()
        .placeholder(defaultDrawable)
        .into(target)
}

internal fun ImageView.loadCircleImg(
    url: String,
    defaultDrawable: Drawable? = null,
    onResourceReady: (() -> Unit)? = null,
    onLoadFailed: (() -> Unit)? = null
) {
    Glide
        .with(context)
        .asBitmap()
        .load(url)
        .timeout(GLIDE_TIMEOUT)
        .apply(
            RequestOptions()
                .fitCenter()
                .centerCrop()
                .circleCrop()
        )
        .placeholder(defaultDrawable)
        .listener(object: RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadFailed?.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onResourceReady?.invoke()
                return false
            }
        })
        .into(this)
}