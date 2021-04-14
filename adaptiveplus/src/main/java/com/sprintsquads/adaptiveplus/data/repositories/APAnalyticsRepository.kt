package com.sprintsquads.adaptiveplus.data.repositories

import com.google.gson.Gson
import com.sprintsquads.adaptiveplus.core.managers.APAuthCredentialsManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.SDK_API_URL
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import okhttp3.Request
import okhttp3.RequestBody


internal class APAnalyticsRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    userRepository: APUserRepository
) : APBaseRepository(networkManager, authCredentialsManager, userRepository) {

    fun submitAnalytics(
        events: List<Map<String, Any>>,
        callback: RequestResultCallback<Any?>
    ) {
        val body = RequestBody.create(
            JSON_MEDIA_TYPE,
            Gson().toJson(events))

        val request = Request.Builder()
            .url("$SDK_API_URL/events")
            .post(body)
            .build()

        executeRequest<Any>(request,
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