package plus.adaptive.sdk.data.models.components

import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.APFont
import java.io.Serializable


internal data class APButtonComponent(
    val text: Text,
    val actions: List<APAction>,
    val cornerRadius: Double,
    val backgroundColor: String
) : APComponent, Serializable {

    data class Text(
        val value: String,
        val font: APFont?
    ) : Serializable
}