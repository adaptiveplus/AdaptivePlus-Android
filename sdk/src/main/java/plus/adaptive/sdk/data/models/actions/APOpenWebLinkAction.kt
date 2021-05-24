package plus.adaptive.sdk.data.models.actions

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APOpenWebLinkAction(
    val url: String,
    val isWebView: Boolean?
) : APAction, Serializable