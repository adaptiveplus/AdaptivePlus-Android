package plus.adaptive.sdk.data.models

import java.io.Serializable


internal data class APGradientColor(
    val startColor: String,
    val endColor: String?,
    val angle: Double?
) : Serializable