package plus.adaptive.sdk.data

import plus.adaptive.sdk.BuildConfig
import java.util.*


internal var IS_DEBUGGABLE: Boolean = false
internal var LOCALE: Locale = Locale.ENGLISH

internal var CUSTOM_IP_ADDRESS: String? = null

internal var REQUEST_TIMEOUT: Long = 30L // seconds
internal var GLIDE_TIMEOUT: Int = 60000 // milliseconds

internal const val SDK_API_URL: String = BuildConfig.SDK_API_URL

internal const val DELAY_BETWEEN_CLICKS = 1000L // milliseconds

internal const val BASE_SIZE_MULTIPLIER = 1 // to increase quality of view drawing

internal const val OS_NAME = "android"