package plus.adaptive.sdk.ext

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import plus.adaptive.sdk.data.models.APFont
import plus.adaptive.sdk.utils.getColorFromHex
import plus.adaptive.sdk.utils.requestFontDownload


private val apFontMap = mutableMapOf<String, Typeface>()


internal fun TextView.applyAPFont(
    apFont: APFont,
    dimensionUnit: Int = TypedValue.COMPLEX_UNIT_PX,
    onSuccess: (() -> Unit)? = null,
    onError: (() -> Unit)? = null
) {
    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
        this,
        1, apFont.size.toInt(),
        1, dimensionUnit)
    getColorFromHex(apFont.color)?.let { setTextColor(it) }

    gravity = when (apFont.align) {
        APFont.Align.LEFT -> Gravity.START
        APFont.Align.CENTER -> Gravity.CENTER
        APFont.Align.RIGHT -> Gravity.END
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        letterSpacing = apFont.letterSpacing.toFloat()
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        apFont.lineHeight?.let {
            lineHeight = TypedValue.applyDimension(
                dimensionUnit, it.toFloat(), resources.displayMetrics).toInt()
        }
    }

    val fontMapKey = "${apFont.family}:${apFont.style}"

    if (fontMapKey !in apFontMap) {
        requestFontDownload(
            context = context,
            familyName = apFont.family,
            fontStyle = apFont.style,
            onSuccess = {
                typeface = it
                apFontMap[fontMapKey] = it
                onSuccess?.invoke()
            },
            onError = {
                onError?.invoke()
            }
        )
    } else {
        typeface = apFontMap[fontMapKey]
        onSuccess?.invoke()
    }
}