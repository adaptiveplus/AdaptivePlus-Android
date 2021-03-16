package com.sprintsquads.adaptiveplus.data.models.components

import com.sprintsquads.adaptiveplus.data.models.APColor
import java.io.Serializable


internal data class APImageComponent(
    val url: String,
    val border: Border?,
    val cornerRadius: Double?
) : APComponent, Serializable {

    data class Border(
        val width: Double,
        val activeColor: APColor,
        val padding: Double,
        val cornerRadius: Double
    ) : Serializable
}