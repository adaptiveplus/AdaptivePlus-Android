package com.sprintsquads.adaptiveplus.sdk.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class APUserConfig(
    val age: Int? = null,
    val gender: Gender? = null
) : Serializable {
    enum class Gender(val value: String) {
        @SerializedName("MALE")
        MALE("MALE"),
        @SerializedName("FEMALE")
        FEMALE("FEMALE")
    }
}