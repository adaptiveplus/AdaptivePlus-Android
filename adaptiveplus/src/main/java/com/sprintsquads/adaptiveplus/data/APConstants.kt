package com.sprintsquads.adaptiveplus.data

import android.webkit.URLUtil


internal var IS_DEBUGGABLE: Boolean = false
internal var LOCALE: String = "ru"
    set(value) {
        field = if (value == "kk") {
            "kz"
        } else {
            value
        }
    }
internal var CUSTOM_IP_ADDRESS: String? = null

internal var BASE_API_URL: String? = null
    set(value) {
        if (URLUtil.isValidUrl(value)) {
            field = value
            SDK_API_URL = "$field/v1"
        }
    }
internal var SDK_API_URL: String = "$BASE_API_URL/v1"

internal const val DELAY_BETWEEN_CLICKS = 1000 // milliseconds
internal const val GLIDE_TIMEOUT = 60000 // milliseconds

internal const val META_KEY_BASE_API_URL = "apBaseApiUrl"
internal const val META_KEY_CHANNEL_SECRET = "apChannelSecret"