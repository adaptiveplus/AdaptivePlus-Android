package com.sprintsquads.adaptiveplus.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sprintsquads.adaptiveplus.core.providers.provideAPAuthRepository
import com.sprintsquads.adaptiveplus.core.providers.provideAPClientCredentialsManager
import com.sprintsquads.adaptiveplus.core.providers.provideAPUserRepository
import com.sprintsquads.adaptiveplus.data.*
import com.sprintsquads.adaptiveplus.data.BASE_API_URL
import com.sprintsquads.adaptiveplus.data.IS_DEBUGGABLE
import com.sprintsquads.adaptiveplus.data.LOCALE
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import com.sprintsquads.adaptiveplus.data.models.network.RequestState
import com.sprintsquads.adaptiveplus.data.repositories.APAuthRepository
import com.sprintsquads.adaptiveplus.data.repositories.APUserRepository
import com.sprintsquads.adaptiveplus.sdk.data.APLocation
import com.sprintsquads.adaptiveplus.sdk.exceptions.APInitializationException


class AdaptivePlusSDK {

    private var authRepository: APAuthRepository? = null
    private var userRepository: APUserRepository? = null


    @SuppressLint("HardwareIds")
    fun start(
        context: Context,
        userId: String? = null,
        userProperties: Map<String, Any>? = null,
        location: APLocation? = null,
        locale: String? = null,
        isDebuggable: Boolean = false
    ) {
        stop()

        provideAPClientCredentialsManager().init(context)

        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )

        appInfo.metaData?.getString(META_KEY_BASE_API_URL)?.let { url ->
            BASE_API_URL = url
        }

        if (BASE_API_URL == null) {
            throw APInitializationException()
        }

        IS_DEBUGGABLE = isDebuggable
        LOCALE = locale ?:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales.get(0).language
                } else {
                    context.resources.configuration.locale.language
                }

        userRepositoryInstance(context)?.run {
            setExternalUserId(userId)
            setUserProperties(userProperties)
            setUserLocation(location)

            setDeviceId(
                Settings.Secure.getString(
                    context.contentResolver, Settings.Secure.ANDROID_ID))
        }

        isStartedLiveData.value = true
        authorize(context, true)
    }

    internal fun authorize(context: Context, isForced: Boolean = false) {
        if (isStartedLiveData.value != true ||
            (!isForced && tokenRequestState == RequestState.IN_PROCESS)
        ) {
            return
        }

        tokenRequestState = RequestState.IN_PROCESS

        authRepositoryInstance(context)?.requestToken(
            object: RequestResultCallback<String>() {
                override fun success(response: String) {
                    tokenRequestState = RequestState.SUCCESS
                }

                override fun failure(error: Any?) {
                    tokenRequestState = RequestState.ERROR
                }
            }
        )
    }

    fun stop() {
        isStartedLiveData.value = false
        tokenRequestState = RequestState.NONE
    }

    private fun authRepositoryInstance(context: Context?) : APAuthRepository? {
        if (authRepository == null && context != null) {
            authRepository = provideAPAuthRepository(context)
        }

        return authRepository
    }

    private fun userRepositoryInstance(context: Context?) : APUserRepository? {
        if (userRepository == null && context != null) {
            userRepository = provideAPUserRepository(context)
        }

        return userRepository
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING)
    fun setTestEnvironment(
        context: Context,
        channelSecret: String,
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

        provideAPClientCredentialsManager().setTestChannelSecret(channelSecret)
        BASE_API_URL = baseUrl
        CUSTOM_IP_ADDRESS = customIP
    }

    internal fun isStartedLiveData(): LiveData<Boolean> = isStartedLiveData

    fun updateUserProperties(userProperties: Map<String, Any>?) {
        userRepositoryInstance(null)?.setUserProperties(userProperties)
    }

    fun updateUserLocation(location: APLocation?) {
        userRepositoryInstance(null)?.setUserLocation(location)
    }


    companion object {
        private var isStartedLiveData = MutableLiveData<Boolean>().apply {
            value = false
        }

        private var tokenRequestState = RequestState.NONE
    }
}