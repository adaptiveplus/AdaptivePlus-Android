package com.sprintsquads.adaptiveplus.data.repositories

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sprintsquads.adaptiveplus.core.managers.APAuthCredentialsManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.SDK_API_URL
import com.sprintsquads.adaptiveplus.data.models.APError
import com.sprintsquads.adaptiveplus.data.models.network.BaseResponseBody
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import com.sprintsquads.adaptiveplus.data.models.network.TokenRequestBody
import com.sprintsquads.adaptiveplus.data.models.network.TokenResponseBody
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody


internal open class APBaseRepository(
    private val networkManager: NetworkServiceManager,
    private val authCredentialsManager: APAuthCredentialsManager,
    private val userRepository: APUserRepository,
    private val customGson: Gson? = null
) {

    private inline fun <reified T> executeRequest(
        request: Request,
        crossinline onSuccess: (response: T) -> Unit,
        crossinline onError: (error: APError?) -> Unit
    ) {
        Thread {
            try {
                val response = networkManager.getOkHttpClient().newCall(request).execute()

                if (response.isSuccessful) {
                    val dataType = object: TypeToken<BaseResponseBody<T>>(){}.type
                    val responseBody = (customGson ?: Gson())
                        .fromJson<BaseResponseBody<T>>(response.body()?.string(), dataType)

                    if (responseBody.code == 0) {
                        onSuccess.invoke(responseBody.data)
                    } else {
                        onError.invoke(
                            APError(
                                code = responseBody.code,
                                message = responseBody.message
                            )
                        )
                    }
                } else {
                    onError.invoke(
                        APError(
                            code = response.code(),
                            message = response.message()
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError.invoke(null)
            }
        }.start()
    }

    protected inline fun <reified T> executeRequest(
        request: Request,
        crossinline onSuccess: (response: T) -> Unit,
        crossinline onError: (error: APError?) -> Unit,
        isReauthorizationOn: Boolean = false
    ) {
        val authRequestResultCallback = object: RequestResultCallback<String>() {
            override fun success(response: String) {
                executeRequest(request, onSuccess, onError)
            }

            override fun failure(error: APError?) {
                onError.invoke(error)
            }
        }

        executeRequest(request, onSuccess,
            { error ->
                if (isReauthorizationOn && error?.code == 401) {
                    authorize(authRequestResultCallback)
                } else {
                    onError.invoke(error)
                }
            }
        )
    }

    protected fun authorize(
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
            userDevice = user.device,
            userLocation = user.location
        )
        val body = RequestBody.create(JSON_MEDIA_TYPE, Gson().toJson(tokenRequestBody))

        val request = Request.Builder()
            .url("$SDK_API_URL/oauth/channel/token")
            .addHeader(HEADER_CLIENT_ID, clientId)
            .addHeader(HEADER_CLIENT_SECRET, clientSecret)
            .addHeader(HEADER_GRANT_TYPE, grantType)
            .addHeader(HEADER_CHANNEL_SECRET, channelSecret)
            .post(body)
            .build()

        executeRequest<TokenResponseBody>(request,
            { response ->
                userRepository.setAPUserId(response.apUserId)
                networkManager.updateToken(response.token)
                callback.success(response.token)
            },
            { error ->
                networkManager.updateToken(null)
                callback.failure(error)
            }
        )
    }


    companion object {
        private const val HEADER_CLIENT_ID = "client_id"
        private const val HEADER_CLIENT_SECRET = "client_secret"
        private const val HEADER_GRANT_TYPE = "grant_type"
        private const val HEADER_CHANNEL_SECRET = "channel_secret"

        @JvmStatic
        protected val JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8")
    }
}