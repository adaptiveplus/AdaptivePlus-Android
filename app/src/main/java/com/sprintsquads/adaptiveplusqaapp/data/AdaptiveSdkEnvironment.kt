package com.sprintsquads.adaptiveplusqaapp.data

import java.io.Serializable


data class AdaptiveSdkEnvironment(
    val name: String,
    val appId: String,
    val companySecret: String,
    val appSecret: String,
    val baseApiUrl: String,
    var tags: List<Tag>
) : Serializable {

    data class Tag(
        val id: String,
        val loadingType: LoadingType,
        val isInstructions: Boolean? = false,
        val isOnboarding: Boolean? = false,
        val hasBookmarks: Boolean? = false
    ) : Serializable {

        enum class LoadingType {
            EMPTY,
            BANNERS_FULLSCREEN
        }
    }
}