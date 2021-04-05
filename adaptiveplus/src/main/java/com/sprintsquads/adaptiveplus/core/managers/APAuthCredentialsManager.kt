package com.sprintsquads.adaptiveplus.core.managers

import android.content.Context
import android.content.pm.PackageManager
import com.sprintsquads.adaptiveplus.data.META_KEY_CHANNEL_SECRET
import com.sprintsquads.adaptiveplus.data.models.APAuthCredentials
import com.sprintsquads.adaptiveplus.sdk.exceptions.APInitializationException


internal class APAuthCredentialsManager {

    companion object {
        private const val clientId: String = "43f4a067-aa4b-4e19-888f-05237cf8a865"
        private const val clientSecret: String = "BpLnfgDsc2WD8F2qNfHK5a84jjJkwzDkh9h2fhfUVuS9jZ8uVbhV3vC5AWX39IVU"
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