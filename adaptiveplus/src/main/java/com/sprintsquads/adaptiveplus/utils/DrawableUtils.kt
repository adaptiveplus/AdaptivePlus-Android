package com.sprintsquads.adaptiveplus.utils

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable


internal fun createDrawableFromColor(color: Int?): Drawable? {
    if (color == null) return null

    return ColorDrawable(color)
}

internal fun createDrawableFromVerticalGradient(
    top: Int?,
    bottom: Int?
): Drawable? {
    if (top == null || bottom == null) return null

    return GradientDrawable(
        GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(top, bottom)
    )
}

internal fun createCircleDrawable(color: Int): Drawable {
    return GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
    }
}