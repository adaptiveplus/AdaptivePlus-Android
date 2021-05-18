package plus.adaptive.sdk.data.repositories

import com.google.gson.Gson
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APSplashScreenTemplate
import plus.adaptive.sdk.data.models.network.APSplashScreenTemplateRequestBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.utils.checkAPSplashScreenTemplateProperties
import plus.adaptive.sdk.utils.magnifyAPSplashScreenTemplate


internal class APSplashScreenRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    userRepository: APUserRepository,
    customGson: Gson
) : APBaseRepository(networkManager, authCredentialsManager, userRepository, customGson) {

    fun requestAPSplashScreenTemplate(
        callback: RequestResultCallback<APSplashScreenTemplate>
    ) {
        val obj = APSplashScreenTemplateRequestBody(
            parserVersion = 1,
            hasDrafts = false
        )
        val body = Gson().toJson(obj).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$SDK_API_URL/ap-view-launch-screen-templates")
            .post(body)
            .build()

        executeRequest<APSplashScreenTemplate>(request,
            { response ->
                try {
                    checkAPSplashScreenTemplateProperties(response)
                    val dataModel = magnifyAPSplashScreenTemplate(response)
                    callback.success(dataModel)
                } catch (e: Exception) {
                    APCrashlytics.logCrash(e)
                    e.printStackTrace()
                    callback.failure(
                        APError(
                            code = -1,
                            message = e.message
                        )
                    )
                }
            },
            { error ->
                callback.failure(error)
            },
            isReauthorizationOn = true
        )
    }
}