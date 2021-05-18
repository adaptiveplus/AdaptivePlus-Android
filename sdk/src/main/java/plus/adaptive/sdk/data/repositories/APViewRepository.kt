package plus.adaptive.sdk.data.repositories

import com.google.gson.Gson
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.APViewDataModel
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.network.APViewRequestBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.utils.checkAPViewDataModelProperties
import plus.adaptive.sdk.utils.magnifyAPViewDataModel
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


internal class APViewRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    userRepository: APUserRepository,
    customGson: Gson
) : APBaseRepository(networkManager, authCredentialsManager, userRepository, customGson) {

    /**
     * Method to request adaptive plus view data model
     *
     * @param apViewId - id of the adaptive plus view
     * @param callback - APView data model request result callback
     * @see APViewDataModel
     * @see RequestResultCallback
     */
    fun requestAPView(
        apViewId: String,
        hasDrafts: Boolean,
        callback: RequestResultCallback<APViewDataModel>
    ) {
        val obj = APViewRequestBody(
            parserVersion = 1,
            hasDrafts = hasDrafts
        )
        val body = Gson().toJson(obj).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$SDK_API_URL/ap-view-templates/$apViewId")
            .post(body)
            .build()

        executeRequest<APViewDataModel>(request,
            { response ->
                try {
                    checkAPViewDataModelProperties(response)
                    val dataModel = magnifyAPViewDataModel(response)
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