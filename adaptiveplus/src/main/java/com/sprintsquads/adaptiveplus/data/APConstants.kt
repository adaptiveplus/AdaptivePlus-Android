package com.sprintsquads.adaptiveplus.data

import android.webkit.URLUtil


// can be changed only upon sdk initialization
internal var IS_DEBUGGABLE: Boolean = false
internal var LOCALE: String = "ru"
    set(value) {
        field = if (value == "kk") {
            "kz"
        } else {
            value
        }
    }
internal var BASE_API_URL: String = "https://api-staging.adaptive.plus"
    set(value) {
        if (URLUtil.isValidUrl(value)) {
            field = value
            SDK_API_URL = "$field/api/sdk"
        }
    }
internal var SDK_API_URL: String = "$BASE_API_URL/api/sdk"

internal const val DELAY_BETWEEN_CLICKS = 1000 // time in milliseconds

internal var CUSTOM_IP_ADDRESS: String? = null

internal const val GLIDE_TIMEOUT = 60000 // milliseconds

internal const val META_KEY_BASE_API_URL = "adaptiveBaseApiUrl"
internal const val META_KEY_COMPANY_SECRET = "adaptiveCompanySecret"
internal const val META_KEY_APP_SECRET = "adaptiveAppSecret"