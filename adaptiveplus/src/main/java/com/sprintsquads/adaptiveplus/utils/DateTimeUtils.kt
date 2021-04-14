package com.sprintsquads.adaptiveplus.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


internal const val DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"


internal fun getCurrentTimeString(targetFmt: String = DATE_FORMAT_ISO) : String {
    val sdf = SimpleDateFormat(targetFmt, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    return sdf.format(Calendar.getInstance().time)
}

internal fun parseDateString(dateStr: String, srcFormat: String = DATE_FORMAT_ISO): Date? {
    val sdf = SimpleDateFormat(srcFormat, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    return try {
        sdf.parse(dateStr)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}