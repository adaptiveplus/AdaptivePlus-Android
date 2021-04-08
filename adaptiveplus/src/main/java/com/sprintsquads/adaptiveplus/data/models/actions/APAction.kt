package com.sprintsquads.adaptiveplus.data.models.actions

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal interface APAction {
    enum class Type {
        @SerializedName("SHOW_STORY")
        SHOW_STORY,
        @SerializedName("OPEN_WEB_LINK")
        OPEN_WEB_LINK,
        @SerializedName("CUSTOM")
        CUSTOM
    }
}