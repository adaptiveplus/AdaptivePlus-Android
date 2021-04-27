package plus.adaptive.sdk.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APLog(
    val action: String,
    val message: String,
    val code: Int,
    val type: Type,
    val data: Map<String, Any?>?,
    var createdAt: String
) : Serializable {

    enum class Type {
        @SerializedName("fatal")
        FATAL,
        @SerializedName("error")
        ERROR,
        @SerializedName("warn")
        WARN,
        @SerializedName("info")
        INFO,
        @SerializedName("debug")
        DEBUG,
        @SerializedName("trace")
        TRACE
    }
}