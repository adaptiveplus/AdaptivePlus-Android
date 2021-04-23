package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import plus.adaptive.sdk.data.models.actions.APAction
import java.io.Serializable

@Keep
internal data class APEntryPoint(
    val id: String,
    val updatedAt: String,
    val campaignId: String,
    val status: Status?,
    val showOnce: Boolean,
    val layers: List<APLayer>,
    val actions: List<APAction>
) : Serializable {

    enum class Status {
        @SerializedName("ACTIVE")
        ACTIVE,
        @SerializedName("DRAFT")
        DRAFT
    }
}
