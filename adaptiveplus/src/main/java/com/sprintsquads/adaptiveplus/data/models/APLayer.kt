package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.sprintsquads.adaptiveplus.data.models.components.APComponent
import java.io.Serializable

@Keep
internal data class APLayer(
    val type: Type,
    val options: Options,
    val component: APComponent?
) : Serializable {

    enum class Type {
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