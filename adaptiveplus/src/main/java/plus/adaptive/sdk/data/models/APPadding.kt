package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APPadding(
    val top: Double,
    val bottom: Double,
    val left: Double,
    val right: Double
) : Serializable