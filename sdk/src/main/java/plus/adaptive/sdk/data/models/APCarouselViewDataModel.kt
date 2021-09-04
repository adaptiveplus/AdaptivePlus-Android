package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
internal data class APCarouselViewDataModel(
    val id: String,
    val options: Options,
    var entryPoints: List<APEntryPoint>
) : Serializable {

    data class Options(
        val width: Double,
        val height: Double,
        val cornerRadius: Double,
        val magnetize: Boolean,
        val autoScroll: Double?,
        val padding: APPadding,
        val spacing: Double,
        val screenWidth: Double,
        val showBorder: Boolean?
    ) : Serializable

}