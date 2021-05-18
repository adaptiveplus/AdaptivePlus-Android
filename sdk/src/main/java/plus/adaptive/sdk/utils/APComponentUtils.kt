package plus.adaptive.sdk.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import plus.adaptive.sdk.data.GLIDE_TIMEOUT
import plus.adaptive.sdk.data.models.APFont


internal fun preloadImage(context: Context, url: String) {
    Glide.with(context)
        .load(url)
        .timeout(GLIDE_TIMEOUT)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .preload()
}

internal fun preloadGIF(context: Context, url: String) {
    preloadImage(context, url)
}

internal fun preloadAPFont(context: Context, apFont: APFont) {
    requestFontDownload(
        context = context,
        familyName = apFont.family,
        fontStyle = apFont.style,
        onSuccess = {},
        onError = {}
    )
}