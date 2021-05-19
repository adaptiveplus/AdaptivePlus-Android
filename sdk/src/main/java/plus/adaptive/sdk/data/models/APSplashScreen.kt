package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import plus.adaptive.sdk.data.models.actions.APAction
import java.io.Serializable


@Keep
internal data class APSplashScreen(
    val id: String,
    val campaignId: String,
    val status: Status,
    val showCount: Int?,
    val showTime: Double?,
    val layers: List<APLayer>,
    val actions: List<APAction>?
) : Serializable {

    enum class Status {
        @SerializedName("ACTIVE")
        ACTIVE,
        @SerializedName("DRAFT")
        DRAFT
    }
}