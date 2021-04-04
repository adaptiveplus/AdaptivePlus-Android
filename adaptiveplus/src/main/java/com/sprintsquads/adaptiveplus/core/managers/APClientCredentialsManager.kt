package com.sprintsquads.adaptiveplus.core.managers

import android.content.Context
import android.content.pm.PackageManager
import com.sprintsquads.adaptiveplus.data.META_KEY_CLIENT_ID
import com.sprintsquads.adaptiveplus.data.META_KEY_CLIENT_SECRET
import com.sprintsquads.adaptiveplus.data.models.APClientCredentials
import com.sprintsquads.adaptiveplus.sdk.exceptions.APInitializationException


internal class APClientCredentialsManager {

    companion object {
        private var clientCredentials: APClientCredentials? = null
        private var testClientCredentials: APClientCredentials? = null
    }


    fun init(context: Context) {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val clientId = appInfo.metaData?.getString(META_KEY_CLIENT_ID)
        val clientSecret = appInfo.metaData?.getString(META_KEY_CLIENT_SECRET)

        if (clientId == null || clientSecret == null) {
            if (testClientCredentials == null) {
                throw APInitializationException()
            }
        }
        else {
            clientCredentials = APClientCredentials(clientId, clientSecret)
        }
    }

    fun getClientCredentials() : APClientCredentials? {
        return testClientCredentials ?: clientCredentials
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING)
    fun setTestCredentials(credentials: APClientCredentials?) {
        testClientCredentials = credentials
    }
}