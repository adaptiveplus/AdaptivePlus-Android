package plus.adaptive.sdk.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APFont(
    val family: String,
    val style: Style?,
    val size: Double,
    val color: String,
    val align: Align,
    val letterSpacing: Double,
    val lineHeight: Double?
) : Serializable {

    enum class Align {
        @SerializedName("left")
        LEFT,
        @SerializedName("center")
        CENTER,
        @SerializedName("right")
        RIGHT
    }

    enum class Style {
        @SerializedName("100")
        THIN,
        @SerializedName("100italic")
        THIN_ITALIC,
        @SerializedName("200")
        EXTRA_LIGHT,
        @SerializedName("200italic")
        EXTRA_LIGHT_ITALIC,
        @SerializedName("300")
        LIGHT,
        @SerializedName("300italic")
        LIGHT_ITALIC,
        @SerializedName("regular")
        REGULAR,
        @SerializedName("italic")
        REGULAR_ITALIC,
        @SerializedName("500")
        MEDIUM,
        @SerializedName("500italic")
        MEDIUM_ITALIC,
        @SerializedName("600")
        SEMIBOLD,
        @SerializedName("600italic")
        SEMIBOLD_ITALIC,
        @SerializedName("700")
        BOLD,
        @SerializedName("700italic")
        BOLD_ITALIC,
        @SerializedName("800")
        EXTRA_BOLD,
        @SerializedName("800italic")
        EXTRA_BOLD_ITALIC,
        @SerializedName("900")
        BLACK,
        @SerializedName("900italic")
        BLACK_ITALIC
    }
}