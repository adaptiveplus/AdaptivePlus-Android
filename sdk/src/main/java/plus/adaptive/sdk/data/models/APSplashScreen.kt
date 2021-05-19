package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import plus.adaptive.sdk.data.models.actions.APAction
import java.io.Serializable


@Keep
internal data class APSplashScreen(
    val id: String,
    val campaignId: String,
    val showCount: Int?,
    val showTime: Double?,
    val layers: List<APLayer>,
    val actions: List<APAction>?
) : Serializable