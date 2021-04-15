package com.sprintsquads.adaptiveplus.data.repositories

import com.sprintsquads.adaptiveplus.core.analytics.APAnalytics
import com.sprintsquads.adaptiveplus.core.managers.APAuthCredentialsManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.GLIDE_TIMEOUT
import com.sprintsquads.adaptiveplus.data.REQUEST_TIMEOUT
import com.sprintsquads.adaptiveplus.data.SDK_API_URL
import com.sprintsquads.adaptiveplus.data.models.network.APConfigsResponseBody
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import okhttp3.Request


internal class APAuthRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    private val userRepository: APUserRepository
) : APBaseRepository(networkManager, authCredentialsManager, userRepository) {

    fun requestToken(
        callback: RequestResultCallback<String>
    ) {
        authorize(callback)
    }

    fun requestAPConfigs(
        callback: RequestResultCallback<APConfigsResponseBody>
    ) {
        val request = Request.Builder()
            .url("$SDK_API_URL/configs")
            .build()

        executeRequest<APConfigsResponseBody>(request,
            { response ->
                applyAPConfigs(response)
                callback.success(response)
            },
            { error ->
                callback.failure(error)
            }
        )
    }

    private fun applyAPConfigs(configs: APConfigsResponseBody) {
        APAnalytics.updateConfig(configs.eventsSubmitPeriod, configs.eventsSubmitCount)
        configs.requestTimeout?.let { REQUEST_TIMEOUT = it }
        configs.imageRequestTimeout?.let { GLIDE_TIMEOUT = it.toInt() }
        configs.isEventTrackingDisabled?.let { userRepository.setIsEventTrackingDisabled(it) }
    }

}