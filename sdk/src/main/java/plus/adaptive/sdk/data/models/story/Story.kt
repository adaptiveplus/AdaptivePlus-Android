package plus.adaptive.sdk.data.models.story

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import plus.adaptive.sdk.data.models.*
import plus.adaptive.sdk.data.models.APFont
import plus.adaptive.sdk.data.models.APLayer
import plus.adaptive.sdk.data.models.APPadding
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.APSnap
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.data.models.components.APTextComponent.APLocale
import java.io.Serializable

@Keep
internal data class APTemplateDataModel(
    val id: String,
    val options: Options,
    @SerializedName("campaigns")
    var campaigns: List<Campaign>
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
        val showBorder: Boolean?,
        val outerStyles: APOuterStyles?

    ) : Serializable

}

@Keep
internal data class APOuterStyles(
    val width: Double,
    val height: Double,
    val cornerRadius: Double,
    val hasTextUnderImage: Boolean,
    val outerSize: OuterSize,
    val outerImageLoadingColor: String
) : Serializable {
    enum class OuterSize {
        S,
        M,
        L
    }
}

@Keep
internal data class Campaign(
    val id: String,
    val updatedAt: String,
    val body: APBody,
    val status: Status?,
    val showCount: Int?
) : Serializable {

    enum class Status {
        @SerializedName("ACTIVE")
        ACTIVE,
        @SerializedName("DRAFT")
        DRAFT
    }

    data class APBody (
        val story: Story?,
        val instruction: Story?
    ) : Serializable
}

@Keep
internal data class Story(
    val id: String,
    val type: String,
    val body: Body,
    var showBorder: Boolean? = true
) : Serializable {
    internal data class Body(
        var campaignId: String,
        val snaps: List<Snap>,
        val outerText: APLocale,
        val outerBorderColor: String?,
        val outerStyles: APOuterStyles?,
        val outerImageUrl: String?
    ): Serializable
}

@Keep
internal data class Snap(
    val id: String,
    val type: String,
    val body: Body,
    val position: Int?
) : Serializable {
    internal data class Body (
        val layers: List<Layer>,
        val width : Double,
        val height : Double,
        val actionAreaHeight : Double?,
        val actionArea : ActionArea?,
        val showTime: Double
    ) : Serializable
}


@Keep
internal data class Layer(
    val type: APLayer.Type,
    val options: APLayer.Options,
    val component: Component?
) : Serializable {

    data class Component (
        val id: String,
        val color : String?,
        val url: String?,
        val type: APPollComponent.Type?,
        val question: APPollData.Question?,
        val answers: List<APPollData.Answer>?,
        val value: APLocale?,
        val font: APFont?,
        val text: Text?,
        val cornerRadius: Double?,
        val loadingColor: String?
    ) : Serializable
}


@Keep
internal data class ActionArea(
    val type: Type,
    val body: Body?
) : Serializable {
    enum class Type {
        @SerializedName("BUTTON")
        BUTTON
    }

    internal data class Body(
        val text: Text,
        val actions: List<Action>,
        val cornerRadius: Double,
        val backgroundColor: String,
        val border: APSnap.ButtonActionArea.Border
    ) : Serializable
}

@Keep
internal data class Action(
    val type: Type,
    val parameters: HashMap<String, Any>?,
    val name: String?
) : Serializable {

    enum class Type {
        @SerializedName("SHOW_STORY")
        SHOW_STORY,
        @SerializedName("OPEN_WEB_LINK")
        OPEN_WEB_LINK,
        @SerializedName("CUSTOM")
        CUSTOM,
        @SerializedName("SEND_SMS")
        SEND_SMS,
        @SerializedName("CALL")
        CALL
    }
}

internal data class Text(
    val font: APFont,
    val value: APLocale,
) : Serializable