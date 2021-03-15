package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
internal data class AdaptiveAction(
    val kind: Kind,
    val params: HashMap<String, Any>?
) : Serializable {

    enum class Kind {
        @SerializedName("SHOW_STORY")
        SHOW_STORY,
        @SerializedName("OPEN_WEB_LINK")
        OPEN_WEB_LINK,
        @SerializedName("CUSTOM")
        CUSTOM,
        @SerializedName("SHOW_POPUP_VIEW")
        SHOW_POPUP_VIEW
    }
}