package com.sprintsquads.adaptiveplus.data.repositories

import com.google.gson.Gson
import com.sprintsquads.adaptiveplus.core.managers.APAuthCredentialsManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.SDK_API_URL
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import com.sprintsquads.adaptiveplus.data.models.network.TokenRequestBody
import com.sprintsquads.adaptiveplus.data.models.network.TokenResponseBody
import okhttp3.Request
import okhttp3.RequestBody


internal class APAuthRepository(
    private val authCredentialsManager: APAuthCredentialsManager,
    private val userRepository: APUserRepository,
    networkManager: NetworkServiceManager
) : APBaseRepository(networkManager) {

    companion object {
        private const val HEADER_CLIENT_ID = "client_id"
        private const val HEADER_CLIENT_SECRET = "client_secret"
        private const val HEADER_GRANT_TYPE = "grant_type"
        private const val HEADER_CHANNEL_SECRET = "channel_secret"
    }


    /**
     * Method to request authorization token
     *
     * @param callback - token request result callback
     * @see RequestResultCallback
     */
    fun requestToken(
        callback: RequestResultCallback<String>
    ) {
        val credentials = authCredentialsManager.getAuthCredentials()
        val clientId = credentials?.clientId ?: ""
        val clientSecret = credentials?.clientSecret ?: ""
        val grantType = credentials?.grantType ?: ""
        val channelSecret = credentials?.channelSecret ?: ""

        val user = userRepository.getAPUser()

        val tokenRequestBody = TokenRequestBody(
            apUserId = user.apId,
            externalUserId = user.externalId,
            userProperties = user.properties,
            userDevice = TokenRequestBody.UserDevice(
                deviceId = user.deviceId
            )
        )
        val body = RequestBody.create(JSON_MEDIA_TYPE, Gson().toJson(tokenRequestBody))

        val request = Request.Builder()
            .url("$SDK_API_URL/oauth/token")
            .addHeader(HEADER_CLIENT_ID, clientId)
            .addHeader(HEADER_CLIENT_SECRET, clientSecret)
            .addHeader(HEADER_GRANT_TYPE, grantType)
            .addHeader(HEADER_CHANNEL_SECRET, channelSecret)
            .post(body)
            .build()

        executeRequest<TokenResponseBody>(request,
            { response ->
                userRepository.setAPUserId(response.apUserId)
                updateToken(response.token)
                callback.success(response.token)
            },
            { error ->
                updateToken(null)
                callback.failure(error)
            }
        )
    }

}