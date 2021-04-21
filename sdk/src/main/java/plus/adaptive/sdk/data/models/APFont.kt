package plus.adaptive.sdk.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APFont(
    val family: String,
    val style: Style,
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
        @SerializedName("thin") // = "100"
        THIN,
        @SerializedName("thinItalic") // = "100italic"
        THIN_ITALIC,
        @SerializedName("extralight") // = "200"
        EXTRA_LIGHT,
        @SerializedName("extralightItalic") // = "200italic"
        EXTRA_LIGHT_ITALIC,
        @SerializedName("light") // = "300"
        LIGHT,
        @SerializedName("lightItalic") // = "300italic"
        LIGHT_ITALIC,
        @SerializedName("regular") // = "regular"
        REGULAR,
        @SerializedName("regularItalic") // = "italic"
        REGULAR_ITALIC,
        @SerializedName("medium") // = "500"
        MEDIUM,
        @SerializedName("mediumItalic") // = "500italic"
        MEDIUM_ITALIC,
        @SerializedName("semibold") // = "600"
        SEMIBOLD,
        @SerializedName("semiboldItalic") // = "600italic"
        SEMIBOLD_ITALIC,
        @SerializedName("bold") // = "700"
        BOLD,
        @SerializedName("boldItalic") // = "700italic"
        BOLD_ITALIC,
        @SerializedName("extrabold") // = "800"
        EXTRA_BOLD,
        @SerializedName("extraboldItalic") // = "800italic"
        EXTRA_BOLD_ITALIC,
        @SerializedName("black") // = "900"
        BLACK,
        @SerializedName("blackItalic") // = "900italic"
        BLACK_ITALIC
    }
}