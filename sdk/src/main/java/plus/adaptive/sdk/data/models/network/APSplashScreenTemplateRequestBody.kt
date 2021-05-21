package plus.adaptive.sdk.data.models.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APSplashScreenViewDataModelRequestBody(
    val parserVersion: Int,
    @SerializedName("isDraftEnabled")
    val hasDrafts: Boolean
) : Serializable
