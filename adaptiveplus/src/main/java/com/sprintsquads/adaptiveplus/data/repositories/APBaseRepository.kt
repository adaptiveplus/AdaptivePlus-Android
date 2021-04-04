package com.sprintsquads.adaptiveplus.data.repositories

import com.google.gson.Gson
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.models.APError
import com.sprintsquads.adaptiveplus.data.models.network.BaseResponseBody
import okhttp3.MediaType
import okhttp3.Request


internal open class APBaseRepository(
    private val networkManager: NetworkServiceManager
) {

    protected fun updateToken(token: String?) {
        networkManager.updateToken(token)
    }

    protected fun executeRequest(
        request: Request,
        onSuccess: (response: String?) -> Unit,
        onError: (error: APError?) -> Unit
    ) {
        Thread {
            try {
                val response = networkManager.getOkHttpClient().newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = Gson().fromJson(
                        response.body()?.string(), BaseResponseBody::class.java)

                    if (responseBody.code == 200) {
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