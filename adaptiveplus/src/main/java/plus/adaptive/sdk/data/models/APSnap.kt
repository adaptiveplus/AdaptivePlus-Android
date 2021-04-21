package plus.adaptive.sdk.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import plus.adaptive.sdk.data.models.actions.APAction
import java.io.Serializable

@Keep
internal data class APSnap(
    val id: String,
    val layers: List<APLayer>,
    val width: Double,
    val height: Double,
    val actionAreaHeight: Double?,
    val actionArea: ActionArea?,
    val showTime: Double
) : Serializable {

    interface ActionArea : Serializable {
        enum class Type {
            @SerializedName("BUTTON")
            BUTTON
        }
    }

    data class ButtonActionArea(
        val text: Text,
        val actions: List<APAction>,
        val cornerRadius: Double?,
        val backgroundColor: String,
        val border: Border?
    ) : ActionArea, Serializable {

        data class Text(
            val value: String,
            val font: APFont?
        ) : Serializable

        data class Border(
            val width: Double,
            val color: APGradientColor
        ) : Serializable
    }
}