package plus.adaptive.sdk.data.models.actions

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APCustomAction(
    val parameters: HashMap<String, Any>?
) : APAction, Serializable