package plus.adaptive.sdk.data

import java.util.*


internal var IS_DEBUGGABLE: Boolean = false
internal var LOCALE: Locale = Locale.ENGLISH

internal var CUSTOM_IP_ADDRESS: String? = null

internal var REQUEST_TIMEOUT: Long = 30L // seconds
internal var GLIDE_TIMEOUT: Int = 60000 // milliseconds

internal const val DELAY_BETWEEN_CLICKS = 1000L // milliseconds

internal const val BASE_SIZE_MULTIPLIER_NEW = 4 // to increase quality of view drawing

internal const val BASE_SIZE_MULTIPLIER = 4 // to increase quality of view drawing

internal const val OS_NAME = "android"

internal var SDK_API_URL = "https://test-adaptive-api2.homebank.kz/v1"

internal var QA_API_URL: String? = ""

internal var ENV_NAME = ""

internal var AUTHORIZATION_TOKEN = ""