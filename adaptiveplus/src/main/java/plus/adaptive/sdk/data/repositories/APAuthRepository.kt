package plus.adaptive.sdk.data.repositories

import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.GLIDE_TIMEOUT
import plus.adaptive.sdk.data.REQUEST_TIMEOUT
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.network.APConfigsResponseBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback
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
        APAnalytics.updateConfig(configs.eventsSubmitPeriod?.let { (it * 1000).toLong() }, configs.eventsSubmitCount)
        configs.requestTimeout?.let { REQUEST_TIMEOUT = it.toLong() }
        configs.imageRequestTimeout?.let { GLIDE_TIMEOUT = (it * 1000).toInt() }
        configs.isEventTrackingDisabled?.let { userRepository.setIsEventTrackingDisabled(it) }
    }

}