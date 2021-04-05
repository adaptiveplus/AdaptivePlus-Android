package com.sprintsquads.adaptiveplus.data.repositories

import com.google.gson.Gson
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.SDK_API_URL
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.network.APViewRequestBody
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import okhttp3.Request
import okhttp3.RequestBody


internal class APViewRepository(
    networkManager: NetworkServiceManager
) : APBaseRepository(networkManager) {

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
            }
        )
    }
}