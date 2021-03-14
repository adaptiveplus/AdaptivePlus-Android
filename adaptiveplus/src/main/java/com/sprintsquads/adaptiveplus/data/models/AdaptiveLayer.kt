package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
internal data class AdaptiveLayer(
    val kind: Kind,
    val options: Options,
    val component: AdaptiveComponent
) : Serializable {

    enum class Kind {
        @SerializedName("BACKGROUND")
        BACKGROUND,
        @SerializedName("IMAGE")
        IMAGE,
        @SerializedName("TEXT")
        TEXT,
        @SerializedName("BUTTON")
        BUTTON
    }

    data class Options(
        val position: Position,
        val opacity: Double
    ) : Serializable

    data class Position(
        val x: Double,
        val y: Double,
        val width: Double,
        val height: Double,
        val angle: Double
    ) : Serializable
}