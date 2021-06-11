package plus.adaptive.sdk.data.repositories

import com.google.gson.Gson
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.data.SDK_API_URL
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.network.APPollAnswerRequestBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback


internal class APPollRepository(
    networkManager: NetworkServiceManager,
    authCredentialsManager: APAuthCredentialsManager,
    userRepository: APUserRepository
) : APBaseRepository(networkManager, authCredentialsManager, userRepository) {

    fun requestPollData(
        pollId: String,
        callback: RequestResultCallback<APPollData>
    ) {
        val request = Request.Builder()
            .url("$SDK_API_URL/surveys/$pollId/sdk")
            .build()

        executeRequest<APPollData>(request,
            { response ->
                callback.success(response)
            },
            { error ->
                callback.failure(error)
            },
            isReauthorizationOn = true
        )
    }

    fun submitChosenAnswer(
        pollId: String,
        answerId: String,
        callback: RequestResultCallback<Any?>
    ) {
        val body = Gson().toJson(
            APPollAnswerRequestBody(
                pollId = pollId,
                answerId = answerId
            )
        ).toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$SDK_API_URL/surveys/trigger-survey-answer")
            .post(body)
            .build()

        executeRequest<Any?>(request,
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