package com.sprintsquads.adaptiveplus.data.models.components

import com.sprintsquads.adaptiveplus.data.models.AdaptiveAction
import com.sprintsquads.adaptiveplus.data.models.AdaptiveFont
import java.io.Serializable


internal data class AdaptiveButtonComponent(
    val text: Text,
    val actions: List<AdaptiveAction>,
    val cornerRadius: Double,
    val backgroundColor: String
) : AdaptiveComponent, Serializable {

    data class Text(
        val value: String,
        val font: AdaptiveFont?
    ) : Serializable
}