package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APSplashScreenTemplate(
    val id: String,
    val options: Options,
    var splashScreens: List<APSplashScreen>
) : Serializable {

    data class Options(
        val screenWidth: Double
    ) : Serializable

}
