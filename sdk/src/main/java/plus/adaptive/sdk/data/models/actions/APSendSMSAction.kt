package plus.adaptive.sdk.data.models.actions

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APSendSMSAction(
    val phoneNumber: String,
    val message: String
) : APAction, Serializable