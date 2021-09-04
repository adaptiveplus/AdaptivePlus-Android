package plus.adaptive.sdk.data.repositories

import com.google.gson.Gson
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.APCarouselViewDataModel
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.network.APViewDMRequestBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.utils.checkAPCarouselViewDataModelProperties
import plus.adaptive.sdk.utils.magnifyAPCarouselViewDataModel
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import plus.adaptive.sdk.data.models.story.APTemplateDataModel


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
     * @see APCarouselViewDataModel
     * @see RequestResultCallback
     */
    fun requestAPView(
        apViewId: String,
        hasDrafts: Boolean,
        callback: RequestResultCallback<APCarouselViewDataModel>
    ) {
        val obj = APViewDMRequestBody(
            parserVersion = 1,
            hasDrafts = hasDrafts
        )
        val body = Gson().toJson(obj).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$SDK_API_URL/ap-view-templates/by-key/$apViewId")
            .post(body)
            .build()

        executeRequest<APCarouselViewDataModel>(request,
            { response ->
                try {
                    checkAPCarouselViewDataModelProperties(response)
                    val dataModel = magnifyAPCarouselViewDataModel(response)
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


    fun requestTemplate(
        apViewId: String,
        hasDrafts: Boolean,
        callback: RequestResultCallback<APTemplateDataModel>
    ) {
        val obj = APViewDMRequestBody(
            parserVersion = 1,
            hasDrafts = hasDrafts
        )
        val body = Gson().toJson(obj).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$SDK_API_URL/ap-view-templates/by-key/$apViewId")
            .post(body)
            .build()

        executeRequest<APTemplateDataModel>(request,
            { response ->
                try {
                    callback.success(response)
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