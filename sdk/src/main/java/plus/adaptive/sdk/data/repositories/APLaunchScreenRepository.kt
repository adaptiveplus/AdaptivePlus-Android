package plus.adaptive.sdk.data.repositories

import com.google.gson.Gson
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.APLaunchScreen
import plus.adaptive.sdk.data.models.network.APLaunchScreenRequestBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback


internal class APLaunchScreenRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    userRepository: APUserRepository,
    customGson: Gson
) : APBaseRepository(networkManager, authCredentialsManager, userRepository, customGson) {

    fun requestAPLaunchScreen(
        callback: RequestResultCallback<APLaunchScreen>
    ) {
        val obj = APLaunchScreenRequestBody(
            parserVersion = 1
        )
        val body = Gson().toJson(obj).toRequestBody(JSON_MEDIA_TYPE)

        // TODO: update url on endpoint readiness
        val request = Request.Builder()
            .url("$SDK_API_URL/ap-launchscreen-templates")
            .post(body)
            .build()

        executeRequest<APLaunchScreen>(request,
            { response ->
                callback.success(response)
            },
            { error ->
                callback.failure(error)
            },
            isReauthorizationOn = true
        )
    }
}