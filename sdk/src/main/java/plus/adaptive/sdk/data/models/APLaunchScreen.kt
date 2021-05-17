package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import java.io.Serializable


@Keep
internal data class APLaunchScreen(
    val id: String,
    val campaignId: String,
    val showCount: Int,
    val showTime: Double,
    val layers: List<APLayer>
) : Serializable