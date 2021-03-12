package com.sprintsquads.adaptiveplus.sdk.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AdaptiveOnboardingItem(
    val title: String?,
    val subtitle: String?,
    val imageUrl: String?,
    val imageType: ImageType?,
    val hasActions: Boolean
) : Serializable {
    enum class ImageType(val value: String) {
        @SerializedName("SQUARE")
        SQUARE("SQUARE"),
        @SerializedName("FULLSCREEN")
        FULLSCREEN("FULLSCREEN"),
        @SerializedName("THREE_BY_FOUR")
        THREE_BY_FOUR("THREE_BY_FOUR")
    }
}