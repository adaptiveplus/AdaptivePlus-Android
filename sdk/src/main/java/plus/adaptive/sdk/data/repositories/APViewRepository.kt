package plus.adaptive.sdk.data.repositories

import com.google.gson.Gson
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.APViewDataModel
import plus.adaptive.sdk.data.models.network.APViewRequestBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import okhttp3.Request
import okhttp3.RequestBody


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
        callback: RequestResultCallback<APViewDataModel>
    ) {
        val obj = APViewRequestBody(
            parserVersion = 1
        )
        val body = RequestBody.create(
            JSON_MEDIA_TYPE,
            Gson().toJson(obj))

        val request = Request.Builder()
            .url("$SDK_API_URL/ap-view-templates/$apViewId")
            .post(body)
            .build()

        executeRequest<APViewDataModel>(request,
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