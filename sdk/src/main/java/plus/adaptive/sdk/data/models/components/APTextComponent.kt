package plus.adaptive.sdk.data.models.components

import plus.adaptive.sdk.data.models.APFont
import java.io.Serializable


internal data class APTextComponent(
    val value: String,
    val font: APFont?
) : APComponent, Serializable