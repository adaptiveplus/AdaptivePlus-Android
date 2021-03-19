package com.sprintsquads.adaptiveplus.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
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

    data class ActionArea(
        val type: Type,
        val body: BaseBody?
    ) : Serializable {

        enum class Type {
            @SerializedName("BUTTON")
            BUTTON
        }

        interface BaseBody

        data class ButtonBody(
            val width: Double,
            val height: Double,
            val text: Text,
            val actions: List<APAction>,
            val cornerRadius: Double?,
            val backgroundColor: String,
            val border: Border?
        ) : BaseBody, Serializable {

            data class Text(
                val value: String,
                val font: APFont?
            ) : Serializable

            data class Border(
                val width: Double,
                val color: APColor
            ) : Serializable
        }
    }
}