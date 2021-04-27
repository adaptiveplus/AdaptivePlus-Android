package plus.adaptive.sdk.data.repositories

import com.google.gson.Gson
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.APLog


internal class APCrashlyticsRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    userRepository: APUserRepository
) : APBaseRepository(networkManager, authCredentialsManager, userRepository) {

    fun submitLog(log: APLog) {
        val body = Gson().toJson(log).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$SDK_API_URL/channel-logs")
            .post(body)
            .build()

        executeRequest<Any?>(request, {}, {})
    }
}