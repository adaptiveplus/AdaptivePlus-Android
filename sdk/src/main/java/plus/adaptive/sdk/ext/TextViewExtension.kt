package plus.adaptive.sdk.ext

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.provider.FontRequest
import androidx.core.provider.FontsContractCompat
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APFont
import plus.adaptive.sdk.utils.getColorFromHex


internal fun TextView.applyAPFont(
    apFont: APFont,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, apFont.size.toFloat())
    setTextColor(getColorFromHex(apFont.color))

    gravity = when (apFont.align) {
        APFont.Align.LEFT -> Gravity.START
        APFont.Align.CENTER -> Gravity.CENTER
        APFont.Align.RIGHT -> Gravity.END
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        letterSpacing = apFont.letterSpacing.toFloat()
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        apFont.lineHeight?.let { lineHeight = it.toInt() }
    }

    requestFontDownload(
        context = context,
        familyName = apFont.family,
        fontStyle = apFont.style,
        onSuccess = {
            typeface = it
            onSuccess?.invoke()
        },
        onError = {
            onError?.invoke()
        }
    )
}

private fun requestFontDownload(
    context: Context,
    familyName: String,
    fontStyle: APFont.Style,
    onSuccess: (typeface: Typeface) -> Unit,
    onError: () -> Unit
) {
    val queryBuilder = QueryBuilder(familyName, fontStyle)
    val query = queryBuilder.build()

    val request = FontRequest(
        "com.google.android.gms.fonts",
        "com.google.android.gms",
        query,
        R.array.com_google_android_gms_fonts_certs)

    val callback = object : FontsContractCompat.FontRequestCallback() {

        override fun onTypefaceRetrieved(typeface: Typeface) {
            onSuccess.invoke(typeface)
        }

        override fun onTypefaceRequestFailed(reason: Int) {
            onError.invoke()
        }
    }

    val handlerThread = HandlerThread("fonts")
    handlerThread.start()
    val mHandler = Handler(handlerThread.looper)

    FontsContractCompat
        .requestFont(context, request, callback, mHandler)
}

private class QueryBuilder(
    val familyName: String,
    val fontStyle: APFont.Style
) {

    fun build(): String {
        if (fontStyle == APFont.Style.REGULAR) {
            return familyName
        }

        val builder = StringBuilder()
        builder.append("name=").append(familyName)

        when (fontStyle) {
            APFont.Style.THIN -> {
                builder.append("&weight=").append(100)
            }
            APFont.Style.THIN_ITALIC -> {
                builder.append("&weight=").append(100)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.EXTRA_LIGHT -> {
                builder.append("&weight=").append(200)
            }
            APFont.Style.EXTRA_LIGHT_ITALIC -> {
                builder.append("&weight=").append(200)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.LIGHT -> {
                builder.append("&weight=").append(300)
            }
            APFont.Style.LIGHT_ITALIC -> {
                builder.append("&weight=").append(300)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.REGULAR -> { }
            APFont.Style.REGULAR_ITALIC -> {
                builder.append("&italic=").append(1f)
            }
            APFont.Style.MEDIUM -> {
                builder.append("&weight=").append(500)
            }
            APFont.Style.MEDIUM_ITALIC -> {
                builder.append("&weight=").append(500)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.SEMIBOLD -> {
                builder.append("&weight=").append(600)
            }
            APFont.Style.SEMIBOLD_ITALIC -> {
                builder.append("&weight=").append(600)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.BOLD -> {
                builder.append("&weight=").append(700)
            }
            APFont.Style.BOLD_ITALIC -> {
                builder.append("&weight=").append(700)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.EXTRA_BOLD -> {
                builder.append("&weight=").append(800)
            }
            APFont.Style.EXTRA_BOLD_ITALIC -> {
                builder.append("&weight=").append(800)
                builder.append("&italic=").append(1f)
            }
            APFont.Style.BLACK -> {
                builder.append("&weight=").append(900)
            }
            APFont.Style.BLACK_ITALIC -> {
                builder.append("&weight=").append(900)
                builder.append("&italic=").append(1f)
            }
        }

        builder.append("&besteffort=").append(true)

        return builder.toString()
    }
}