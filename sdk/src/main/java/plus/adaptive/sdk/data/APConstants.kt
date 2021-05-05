package plus.adaptive.sdk.data

import plus.adaptive.sdk.BuildConfig


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

internal const val SDK_API_URL: String = BuildConfig.SDK_API_URL

internal var REQUEST_TIMEOUT: Long = 30L // seconds
internal var GLIDE_TIMEOUT: Int = 60000 // milliseconds

internal const val DELAY_BETWEEN_CLICKS = 1000L // milliseconds

internal const val BASE_SIZE_MULTIPLIER = 4 // to increase quality of view drawing