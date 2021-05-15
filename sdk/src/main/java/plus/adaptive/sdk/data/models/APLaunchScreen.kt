package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
internal data class APLaunchScreen(
    val id: String,
    val options: Options,
    @SerializedName("launchScreens")
    var instances: List<APLaunchScreenInstance>
) : Serializable {

    data class Options(
        val screenWidth: Double
    ) : Serializable

}
