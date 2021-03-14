package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
internal data class AdaptiveTagTemplate(
    val id: String,
    val options: Options,
    @SerializedName("entryPoints")
    val entries: List<AdaptiveEntry>,
    val stories: List<AdaptiveStory>
) : Serializable {

    data class Options(
        val isViewless: Boolean,
        val width: Double,
        val height: Double,
        val cornerRadius: Double,
        val magnetize: Boolean,
        val padding: AdaptivePadding,
        val spacing: Double,
        val screenWidth: Double
    ) : Serializable

}