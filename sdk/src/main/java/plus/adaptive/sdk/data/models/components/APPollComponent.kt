package plus.adaptive.sdk.data.models.components

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APPollComponent(
    val id: String,
    val type: Type
) : APComponent, Serializable {

    enum class Type {
        @SerializedName("YES_NO_POLL")
        YES_NO_POLL,
        @SerializedName("MULTIPLE_CHOICE_POLL")
        MULTIPLE_CHOICE_POLL
    }
}