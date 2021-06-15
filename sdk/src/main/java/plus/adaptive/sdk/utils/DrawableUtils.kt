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