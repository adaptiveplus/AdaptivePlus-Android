package com.sprintsquads.adaptiveplus.data.models.components

import com.sprintsquads.adaptiveplus.data.models.APColor
import java.io.Serializable


internal data class APImageComponent(
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
            val color: APColor,
            val padding: Double,
            val cornerRadius: Double
        ) : Serializable
    }
}