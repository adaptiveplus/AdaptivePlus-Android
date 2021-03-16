package com.sprintsquads.adaptiveplus.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APFont(
    val family: String,
    val style: String,
    val size: Double,
    val color: String,
    val align: Align,
    val letterSpacing: Double
) : Serializable {

    enum class Align {
        @SerializedName("left")
        LEFT,
        @SerializedName("center")
        CENTER,
        @SerializedName("right")
        RIGHT
    }
}