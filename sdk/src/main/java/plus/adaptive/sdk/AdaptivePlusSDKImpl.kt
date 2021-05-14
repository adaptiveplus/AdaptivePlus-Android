package plus.adaptive.sdk

import android.content.Context
import android.os.Build
import androidx.annotation.MainThread
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APSDKManager
import plus.adaptive.sdk.core.providers.provideAPAnalyticsRepository
import plus.adaptive.sdk.data.IS_DEBUGGABLE
import plus.adaptive.sdk.data.LOCALE
import plus.adaptive.sdk.data.OS_NAME
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.APLocation
import plus.adaptive.sdk.data.models.APUser
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.utils.getAppVersion
import plus.adaptive.sdk.utils.getDeviceId
import plus.adaptive.sdk.utils.getDeviceType
import plus.adaptive.sdk.utils.getMobileCountryCode
import plus.adaptive.sdk.utils.getMobileNetworkCode
import plus.adaptive.sdk.utils.getMobileOperatorName
import java.util.*


internal class AdaptivePlusSDKImpl(
    private val context: Context,
    private val sdkManager: APSDKManager,
    private val userRepository: APUserRepository
) : AdaptivePlusSDK {

    @MainThread
    override fun start() : AdaptivePlusSDK {
        stop()

        APAnalytics.init(
            userRepository,
            provideAPAnalyticsRepository(context)
        )

        userRepository.setUserDevice(
            APUser.Device(
                id = getDeviceId(context),
                manufacturer = Build.MANUFACTURER,
                model = Build.MODEL,
                type = getDeviceType(context),
                locale = LOCALE.language,
                osName = OS_NAME,
                osVersion = Build.VERSION.RELEASE,
                storeAppId = context.packageName,
                appPackageName = context.packageName,
                appVersionName = getAppVersion(context),
                apSdkVersion = BuildConfig.AP_VERSION_NAME,
                operatorName = getMobileOperatorName(context),
                mcc = getMobileCountryCode(context),
                mnc = getMobileNetworkCode(context)
            )
        )

        sdkManager.start()

        APAnalytics.logEvent(
            APAnalyticsEvent(name = "launch-sdk")
        )

        sdkManager.authorize(true)

        return this
    }

    @MainThread
    override fun stop() : AdaptivePlusSDK {
        sdkManager.stop()
        return this
    }

    override fun setUserId(userId: String?) : AdaptivePlusSDK {
        userRepository.setExternalUserId(userId)
        return this
    }

    override fun setUserProperties(userProperties: Map<String, String>?) : AdaptivePlusSDK {
        userRepository.setUserProperties(userProperties)
        return this
    }

    override fun setLocation(location: APLocation?) : AdaptivePlusSDK {
        userRepository.setUserLocation(location)
        return this
    }

    override fun setLocale(locale: Locale?) : AdaptivePlusSDK {
        LOCALE = locale ?:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0)
            } else {
                context.resources.configuration.locale
            }
        return this
    }

    override fun setIsDebuggable(isDebuggable: Boolean) : AdaptivePlusSDK {
        IS_DEBUGGABLE = isDebuggable
        return this
    }

}