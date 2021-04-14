package com.sprintsquads.adaptiveplus.sdk.data

import java.io.Serializable


data class APUserProperties(
    val gender: APGender? = null,
    val age: Int? = null
) : Serializable