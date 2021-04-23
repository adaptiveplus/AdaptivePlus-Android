package plus.adaptive.sdk.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APViewRequestBody(
    val parserVersion: Int,
    @SerializedName("isDraftEnabled")
    val hasDrafts: Boolean
) : Serializable