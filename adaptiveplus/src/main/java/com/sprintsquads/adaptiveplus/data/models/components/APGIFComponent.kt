package com.sprintsquads.adaptiveplus.data.models.components

import com.sprintsquads.adaptiveplus.data.models.APGradientColor
import java.io.Serializable


internal data class APGIFComponent(
    val url: String,
    val border: Border?,
    val cornerRadius: Double?
) : APComponent, Serializable {

    data class Border(
        val active: State,
        val inactive: State
    ) : Serializable {

        data class State(
            val width: Double,
            val color: APGradientColor,
            val padding: Double,
            val cornerRadius: Double
        ) : Serializable
    }
}
