package plus.adaptive.sdk.utils

import android.graphics.Color


internal fun getColorFromHex(colorHex: String?): Int? {
    if (colorHex == null) return null

    return try {
        val formattedColorHex = when (colorHex.length) {
            9 -> colorHex.substring(0, 1) + colorHex.substring(7) + colorHex.substring(1, 7)
            5 -> colorHex.substring(0, 1) + colorHex.substring(5) + colorHex.substring(1, 5)
            else -> colorHex
        }
        Color.parseColor(formattedColorHex)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}