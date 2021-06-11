package plus.adaptive.sdk.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APPollAnswerRequestBody(
    @SerializedName("surveyId")
    val pollId: String,
    val answerId: String
) : Serializable