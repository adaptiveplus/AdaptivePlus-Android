package com.sprintsquads.adaptiveplus.extensions

import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.sprintsquads.adaptiveplus.data.models.APFont
import com.sprintsquads.adaptiveplus.utils.getColorFromHex


internal fun TextView.applyAPFont(apFont: APFont) {
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

    // TODO: implement typeface loading
}