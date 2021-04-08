package com.sprintsquads.adaptiveplus.data.repositories

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.models.APError
import com.sprintsquads.adaptiveplus.data.models.network.BaseResponseBody
import okhttp3.MediaType
import okhttp3.Request


internal open class APBaseRepository(
    private val networkManager: NetworkServiceManager,
    private val customGson: Gson? = null
) {

    protected fun updateToken(token: String?) {
        networkManager.updateToken(token)
    }

    protected inline fun <reified T> executeRequest(
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


    companion object {
        @JvmStatic
        protected val JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8")
    }
}