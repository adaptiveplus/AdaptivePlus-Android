package com.sprintsquads.adaptiveplus.data.repositories

import com.google.gson.Gson
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.data.models.network.ErrorResponseBody
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response


internal open class APBaseRepository(
    private val networkManager: NetworkServiceManager
) {

    protected fun updateToken(token: String?) {
        networkManager.updateToken(token)
    }

    protected fun executeRequest(
        request: Request,
        onSuccess: (response: Response) -> Unit,
        onError: (error: ErrorResponseBody?) -> Unit
    ) {
        Thread {
            try {
                val response = networkManager.getOkHttpClient().newCall(request).execute()

                if (response.isSuccessful) {
                    onSuccess.invoke(response)
                } else {
                    val error = Gson().fromJson(response.body()?.string(), ErrorResponseBody::class.java)
                    onError.invoke(error)
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