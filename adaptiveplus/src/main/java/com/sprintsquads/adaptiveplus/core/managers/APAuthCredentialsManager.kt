package com.sprintsquads.adaptiveplus.core.managers

import android.content.Context
import android.content.pm.PackageManager
import com.sprintsquads.adaptiveplus.data.META_KEY_CHANNEL_SECRET
import com.sprintsquads.adaptiveplus.data.models.APAuthCredentials
import com.sprintsquads.adaptiveplus.data.exceptions.APInitializationException


internal class APAuthCredentialsManager {

    companion object {
        private const val clientId: String = "4a99f85d-cfec-4a73-a487-ec3c116eb5d2"
        private const val clientSecret: String = "2b3ea139d809b9ea47e521ad3336c4dc"
        private const val grantType: String = "channel_credentials"
        private var channelSecret: String? = null
        private var testChannelSecret: String? = null
    }


    fun init(context: Context) {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        channelSecret = appInfo.metaData?.getString(META_KEY_CHANNEL_SECRET)

        if (channelSecret == null) {
            if (testChannelSecret == null) {
                throw APInitializationException()
            }
        }
    }

    fun getAuthCredentials() : APAuthCredentials? {
        return (testChannelSecret ?: channelSecret)?.let {
            APAuthCredentials(
                clientId = clientId,
                clientSecret = clientSecret,
                grantType = grantType,
                channelSecret = it
            )
        }
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING)
    fun setTestChannelSecret(channelSecret: String?) {
        testChannelSecret = channelSecret
    }
}