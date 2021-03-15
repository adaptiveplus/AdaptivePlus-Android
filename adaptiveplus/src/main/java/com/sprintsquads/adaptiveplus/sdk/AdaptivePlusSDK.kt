package com.sprintsquads.adaptiveplus.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sprintsquads.adaptiveplus.core.providers.provideAdaptiveAuthRepository
import com.sprintsquads.adaptiveplus.data.*
import com.sprintsquads.adaptiveplus.data.BASE_API_URL
import com.sprintsquads.adaptiveplus.data.IS_DEBUGGABLE
import com.sprintsquads.adaptiveplus.data.LOCALE
import com.sprintsquads.adaptiveplus.data.models.network.RequestState
import com.sprintsquads.adaptiveplus.data.repositories.AdaptiveAuthRepository
import com.sprintsquads.adaptiveplus.sdk.data.AdaptiveLocation
import com.sprintsquads.adaptiveplus.sdk.data.UserConfig


class AdaptivePlusSDK {

    private var authRepository: AdaptiveAuthRepository? = null


    @SuppressLint("HardwareIds")
    fun start(
        context: Context,
        userId: String? = null,
        userConfig: UserConfig? = null,
        isDebuggable: Boolean = false,
        locale: String? = null,
        location: AdaptiveLocation? = null
    ) {
        IS_DEBUGGABLE = isDebuggable
        LOCALE = locale ?:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales.get(0).language
                } else {
                    context.resources.configuration.locale.language
                }

        userLocation = location

        stop(context)

        // TODO: uncomment
//        AdaptiveAnalytics.init(
//            provideAdaptiveAnalyticsRepository(context)
//        )

        if (deviceId == null) {
            deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

        AdaptivePlusSDK.userId = userId ?: deviceId
        AdaptivePlusSDK.userConfig = userConfig

        isStartedLiveData.postValue(true)
        authorize(context, true)
    }

    internal fun authorize(context: Context, isForced: Boolean = false) {
        /*
        if (!isStarted || userId == null) {
            return
        }

        if (!isForced && tokenRequestState == RequestState.LOADING) {
            return
        }

        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )

        appInfo.metaData?.getString(META_KEY_BASE_API_URL)?.let { url ->
            BASE_API_URL = url
        }

        val mCompanySecret =
            appInfo.metaData?.getString(META_KEY_COMPANY_SECRET)
                ?: mCompanySecret
        val mAppSecret =
            appInfo.metaData?.getString(META_KEY_APP_SECRET)
                ?: mAppSecret

        if (mCompanySecret == null || mAppSecret == null) {
            throw AdaptiveInitializationException()
        }

        val authConfig = AuthConfig(
            companySecret = mCompanySecret,
            appSecret = mAppSecret
        )

        val appVersion = try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }

        val requestBody = TokenRequestBody(
            userId = userId!!,
            appId = mAppId ?: context.packageName,
            age = userConfig?.age,
            gender = userConfig?.gender,
            appVersion = appVersion,
            sdkVersion = BuildConfig.VERSION_NAME
        )

        tokenRequestState = RequestState.LOADING

        authRepositoryInstance(context)?.requestToken(
            authConfig, requestBody, object: AdaptivePlusCallback<String>() {
                override fun success(response: String) {
                    tokenRequestState = RequestState.SUCCESS
                    tokenLiveData.postValue(response)
                    requestConfigs()
                }

                override fun failure(error: Any?) {
                    tokenRequestState = RequestState.ERROR
                    tokenLiveData.postValue(null)
                }
            }
        )
         */
    }

    fun stop(context: Context) {
        /*
        isStarted = false
        userId = null
        userConfig = null

        authRepositoryInstance(context)?.removeToken()
        tokenRequestState = RequestState.NONE
        tokenLiveData.postValue(null)
         */
    }

    private fun requestConfigs() {
        /*
        authRepositoryInstance(null)?.requestConfigs(
            object: AdaptivePlusCallback<AdaptiveConfigsResponseBody>() {
                override fun success(response: AdaptiveConfigsResponseBody) {
                    AdaptiveAnalytics.updateConfig(
                        timeout = response.eventSendingPeriod,
                        eventCount = response.maxSendingEventCount
                    )
                }

                override fun failure(error: Any?) {
                    // TODO: decide what to do with error
                }
            })
         */
    }

    private fun authRepositoryInstance(context: Context?) : AdaptiveAuthRepository? {
        if (authRepository == null && context != null) {
            authRepository = provideAdaptiveAuthRepository(context)
        }

        return authRepository
    }

    internal fun getTokenLiveData(): LiveData<String?> = tokenLiveData

    internal fun getTokenRequestState(): RequestState = tokenRequestState

    @Deprecated(
        message = "Not working. Only for testing purposes.",
        level = DeprecationLevel.WARNING)
    fun setTestEnvironment(
        context: Context,
        appId: String,
        companySecret: String,
        appSecret: String,
        baseUrl: String,
        customIP: String?
    ) {
        val testAppIds = listOf(
            "com.s10s.adaptiveplussampleapp",
            "com.s10s.adaptiveplusdemo",
            "com.sprintsquads.adaptiveplusqaapp")
        if (context.packageName !in testAppIds) {
            return
        }

        mAppId = appId
        mAppSecret = appSecret
        mCompanySecret = companySecret
        BASE_API_URL = baseUrl
        CUSTOM_IP_ADDRESS = customIP
    }

    internal fun getUserId(): String? = userId

    internal fun isStartedLiveData(): LiveData<Boolean> = isStartedLiveData

    internal fun getUserLocation(): AdaptiveLocation? = userLocation

    fun updateUserLocation(location: AdaptiveLocation?) {
        userLocation = location
    }


    companion object {
        private var deviceId: String? = null
        private var userId: String? = null
        private var userConfig: UserConfig? = null
        private var userLocation: AdaptiveLocation? = null

        private var mAppId: String? = null
        private var mAppSecret: String? = null
        private var mCompanySecret: String? = null

        private var isStartedLiveData = MutableLiveData<Boolean>().apply {
            value = false
        }

        private val tokenLiveData = MutableLiveData<String?>()
        private var tokenRequestState = RequestState.NONE
    }
}