package plus.adaptive.sdk

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.MainThread
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APSDKManager
import plus.adaptive.sdk.core.providers.provideAPAnalyticsRepository
import plus.adaptive.sdk.core.providers.provideAPSplashScreenViewController
import plus.adaptive.sdk.data.*
import plus.adaptive.sdk.data.LOCALE
import plus.adaptive.sdk.data.OS_NAME
import plus.adaptive.sdk.data.QA_API_URL
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.listeners.APSplashScreenListener
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
import plus.adaptive.sdk.utils.isQAApp
import java.util.*


internal class AdaptivePlusSDKImpl(
    private val context: Context,
    private val sdkManager: APSDKManager,
    private val userRepository: APUserRepository
) : AdaptivePlusSDK {

    companion object {
        private var externalUserId: String? = null
        private var userProperties: Map<String, String>? = null
        private var userLocation: APLocation? = null
    }

    private var splashScreenListener: APSplashScreenListener? = null


    private fun init() {
        userRepository.apply {
            setExternalUserId(externalUserId)
            setUserProperties(userProperties)
            setUserLocation(userLocation)
            setUserDevice(
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
        }

        APAnalytics.init(
            userRepository,
            provideAPAnalyticsRepository(context)
        )
    }

    @MainThread
    override fun start() : AdaptivePlusSDK {
        init()

        if (sdkManager.isStartedLiveData().value != true) {
            APAnalytics.logEvent(
                APAnalyticsEvent(name = "launch-sdk")
            )

            sdkManager.start()
        }

        sdkManager.authorize(true)
        setBaseSdkUrl()
        return this
    }

    private fun setBaseSdkUrl(){
        if(context.packageName == "plus.adaptive.qaapp"){
            QA_API_URL?.let {
                SDK_API_URL = it
            } ?: setBaseSdkUrlFromManifest()
        } else {
            setBaseSdkUrlFromManifest()
        }
    }

    private fun setBaseSdkUrlFromManifest(){
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        SDK_API_URL = appInfo.metaData?.getString("adaptiveBaseApiUrl")?.let { url ->
            url
        } ?: ""
    }

    @MainThread
    override fun stop() : AdaptivePlusSDK {
        sdkManager.stop()
        return this
    }

    override fun setUserId(userId: String?) : AdaptivePlusSDK {
        externalUserId = userId
        return this
    }

    override fun setUserProperties(userProperties: Map<String, String>?) : AdaptivePlusSDK {
        AdaptivePlusSDKImpl.userProperties = userProperties
        return this
    }

    override fun setLocation(location: APLocation?) : AdaptivePlusSDK {
        userLocation = location
        return this
    }

    override fun setLocale(locale: Locale?) : AdaptivePlusSDK {
        AdaptivePlusSDK.setLocale(locale)
        return this
    }

    override fun setIsDebuggable(isDebuggable: Boolean) : AdaptivePlusSDK {
        AdaptivePlusSDK.setIsDebuggable(isDebuggable)
        return this
    }

    @MainThread
    override fun showSplashScreen(): AdaptivePlusSDK {
        return showSplashScreen(hasDrafts = false)
    }

    @MainThread
    override fun showSplashScreen(hasDrafts: Boolean): AdaptivePlusSDK {
        start()

        provideAPSplashScreenViewController(context).apply {
            setSplashScreenListener(splashScreenListener)
            show(hasDrafts)
        }

        return this
    }

    @MainThread
    override fun showMockSplashScreen(): AdaptivePlusSDK {
        if (isQAApp(context)) {
            provideAPSplashScreenViewController(context).apply {
                setSplashScreenListener(splashScreenListener)
                showMock()
            }
        }

        return this
    }

    override fun setSplashScreenListener(listener: APSplashScreenListener?): AdaptivePlusSDK {
        this.splashScreenListener = listener
        return this
    }

    override fun setQaBaseUrl(url: String?) : AdaptivePlusSDK{
        QA_API_URL = url
        return this
    }

    override fun setEnvName(url: String?) : AdaptivePlusSDK{
        url?.let {
            ENV_NAME = it
        }
        return this
    }

}