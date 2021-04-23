package plus.adaptive.sdk.data.models.components

import plus.adaptive.sdk.data.models.APGradientColor
import java.io.Serializable


internal data class APImageComponent(
    val url: String,
    val border: Border?,
    val cornerRadius: Double?,
    val loadingColor: String?
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