package com.sprintsquads.adaptiveplus.data.models.components

import com.sprintsquads.adaptiveplus.data.models.APFont
import java.io.Serializable


internal data class APTextComponent(
    val value: String,
    val font: APFont?
) : APComponent, Serializable