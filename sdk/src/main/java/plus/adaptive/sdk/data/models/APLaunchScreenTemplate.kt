package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APLaunchScreenTemplate(
    val id: String,
    val options: Options,
    var launchScreens: List<APLaunchScreen>
) : Serializable {

    data class Options(
        val screenWidth: Double
    ) : Serializable

}
