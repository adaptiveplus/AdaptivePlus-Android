package plus.adaptive.sdk.data.models.components

import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.APFont
import plus.adaptive.sdk.data.models.APSnap
import java.io.Serializable


internal data class APButtonComponent(
    val text: APSnap.ButtonActionArea.Text,
    val actions: List<APAction>,
    val cornerRadius: Double,
    val backgroundColor: String
) : APComponent, Serializable