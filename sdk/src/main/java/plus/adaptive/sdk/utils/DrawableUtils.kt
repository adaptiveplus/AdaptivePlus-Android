package plus.adaptive.sdk.utils

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable


internal fun createDrawableFromColor(
    color: Int?,
    cornerRadius: Int? = null,
    strokeWidth: Int? = null,
    strokeColor: Int? = null
): Drawable? {
    return color?.let {
        GradientDrawable().apply {
            setColor(it)
            cornerRadius?.let { radius ->
                setCornerRadius(radius.toFloat())
            }
            if (strokeWidth != null && strokeColor != null) {
                setStroke(strokeWidth, strokeColor)
            }
        }
    }
}

internal fun createCircleDrawableFromColor(
    color: Int?,
    strokeWidth: Int? = null,
    strokeColor: Int? = null
): Drawable? {
    return color?.let {
        GradientDrawable().apply {
            setColor(it)
            shape = GradientDrawable.OVAL
            cornerRadii = floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f)
            if (strokeWidth != null && strokeColor != null) {
                setStroke(strokeWidth, strokeColor)
            }
        }
    }
}