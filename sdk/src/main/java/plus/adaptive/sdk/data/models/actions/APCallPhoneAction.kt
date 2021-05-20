package plus.adaptive.sdk.data.models.actions

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APCallPhoneAction(
    val phoneNumber: String
) : APAction, Serializable
