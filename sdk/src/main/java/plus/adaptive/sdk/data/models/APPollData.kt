package plus.adaptive.sdk.data.models

import java.io.Serializable


internal data class APPollData(
    val question: Question,
    val answers: List<Answer>,
    val totalResponseCount: Int
) : Serializable {

    data class Question(
        val id: String,
        val text: String
    ) : Serializable

    data class Answer(
        val id: String,
        val text: String,
        val responseCount: Int
    ) : Serializable
}