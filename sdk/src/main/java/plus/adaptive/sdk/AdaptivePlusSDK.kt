package plus.adaptive.sdk

import android.content.Context
import androidx.annotation.MainThread
import plus.adaptive.sdk.core.providers.provideAPAuthCredentialsManager
import plus.adaptive.sdk.core.providers.provideAPUserRepository
import plus.adaptive.sdk.core.providers.provideAdaptivePlusSDK
import plus.adaptive.sdk.core.providers.provideNetworkServiceManager
import plus.adaptive.sdk.data.CUSTOM_IP_ADDRESS
import plus.adaptive.sdk.data.exceptions.APInitializationException
import plus.adaptive.sdk.data.listeners.APSplashScreenListener
import plus.adaptive.sdk.data.models.APLocation
import plus.adaptive.sdk.utils.isQAApp
import java.util.*


interface AdaptivePlusSDK {

    companion object {
        @JvmStatic
        fun init(apiKey: String) {
            provideAPAuthCredentialsManager().setApiKey(apiKey)
            provideAPUserRepository(null).setAPUserId(null)
            provideNetworkServiceManager().updateToken(null, null)
        }

        @JvmStatic
        @Throws(APInitializationException::class)
        fun newInstance(context: Context) : AdaptivePlusSDK {
            provideAPAuthCredentialsManager().getAuthCredentials()
                ?: throw APInitializationException()

            return provideAdaptivePlusSDK(context)
        }

        @Deprecated(
            message = "Only for testing purposes.",
            level = DeprecationLevel.WARNING
        )
        @JvmStatic
        fun setCustomIP(
            context: Context,
            customIP: String?
        ) {
            if (!isQAApp(context)) {
                return
            }

            CUSTOM_IP_ADDRESS = customIP
        }
    }

    @MainThread
    fun start() : AdaptivePlusSDK

    @MainThread
    fun stop() : AdaptivePlusSDK

    fun setUserId(userId: String?) : AdaptivePlusSDK

    fun setUserProperties(userProperties: Map<String, String>?) : AdaptivePlusSDK

    fun setLocation(location: APLocation?) : AdaptivePlusSDK

    fun setLocale(locale: Locale?) : AdaptivePlusSDK

    fun setIsDebuggable(isDebuggable: Boolean) : AdaptivePlusSDK

    @MainThread
    fun showSplashScreen() : AdaptivePlusSDK

    @MainThread
    fun showSplashScreen(hasDrafts: Boolean) : AdaptivePlusSDK

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING
    )
    @MainThread
    fun showMockSplashScreen() : AdaptivePlusSDK

    fun setSplashScreenListener(listener: APSplashScreenListener?) : AdaptivePlusSDK
}