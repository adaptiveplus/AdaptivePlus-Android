package com.sprintsquads.adaptiveplusqaapp.data

import java.io.Serializable


data class APSdkEnvironment(
    val name: String,
    val appId: String,
    val channelSecret: String,
    val baseApiUrl: String,
    var apViews: List<APView>
) : Serializable {

    data class APView(
        val id: String
    ) : Serializable
}