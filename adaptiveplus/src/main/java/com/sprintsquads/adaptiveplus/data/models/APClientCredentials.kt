package com.sprintsquads.adaptiveplus.data.models

import java.io.Serializable


internal data class APClientCredentials(
    val clientId: String,
    val clientSecret: String
) : Serializable
