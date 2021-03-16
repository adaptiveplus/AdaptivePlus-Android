package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
internal data class APViewDataModel(
    val id: String,
    val options: Options,
    @SerializedName("entryPoints")
    val entryPoints: List<APEntryPoint>,
    val stories: List<APStory>
) : Serializable {

    data class Options(
        val isViewless: Boolean,
        val width: Double,
        val height: Double,
        val cornerRadius: Double,
        val magnetize: Boolean,
        val padding: APPadding,
        val spacing: Double,
        val screenWidth: Double
    ) : Serializable

}