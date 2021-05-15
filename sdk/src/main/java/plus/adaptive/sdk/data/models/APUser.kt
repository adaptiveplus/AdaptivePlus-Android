package plus.adaptive.sdk.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


internal data class APUser(
    @SerializedName("ap_id")
    val apId: String?,
    @SerializedName("external_id")
    val externalId: String?,
    @SerializedName("userDevice")
    val device: Device?,
    @SerializedName("userProperties")
    val properties: Map<String, String>?,
    @SerializedName("location")
    val location: APLocation?
) : Serializable {

    data class Device(
        @SerializedName("deviceId")
        val id: String,
        @SerializedName("deviceManufacturer")
        val manufacturer: String,
        @SerializedName("deviceModel")
        val model: String,
        @SerializedName("deviceType")
        val type: Type,
        @SerializedName("deviceLocale")
        var locale: String,
        @SerializedName("osName")
        val osName: String,
        @SerializedName("osVersion")
        val osVersion: String,
        @SerializedName("storeAppId")
        val storeAppId: String,
        @SerializedName("appPackageName")
        val appPackageName: String,
        @SerializedName("appVersionName")
        val appVersionName: String?,
        @SerializedName("adaptiveSdkVersion")
        val apSdkVersion: String,
        @SerializedName("limitEventTracking")
        var isEventTrackingDisabled: Boolean = false,
        @SerializedName("operatorName")
        val operatorName: String?,
        @SerializedName("mcc")
        val mcc: String?,
        @SerializedName("mnc")
        val mnc: String?,
    ) : Serializable {

        enum class Type {
            @SerializedName("PHONE")
            PHONE,
            @SerializedName("TABLET")
            TABLET,
            @SerializedName("PHABLET")
            PHABLET,
            @SerializedName("TV")
            TV
        }
    }
}
