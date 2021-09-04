package plus.adaptive.sdk.data.models.components

import plus.adaptive.sdk.data.models.APFont
import java.io.Serializable


internal data class APTextComponent(
    val value: APLocale,
    val font: APFont?
) : APComponent, Serializable {
    internal data class APLocale(
        val EN: String,
        val RU: String,
        val KZ: String,
    ): Serializable{
        var locale: String? = null
    }
}