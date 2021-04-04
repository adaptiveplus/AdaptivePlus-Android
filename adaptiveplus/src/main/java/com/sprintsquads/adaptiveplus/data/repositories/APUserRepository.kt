package com.sprintsquads.adaptiveplus.data.repositories

import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.data.models.APUser
import com.sprintsquads.adaptiveplus.sdk.data.APLocation


internal class APUserRepository(
    private val preferences: APSharedPreferences?
) {

    companion object {
        private var apUserId: String? = null
        private var externalUserId: String? = null
        private var deviceId: String? = null
        private var userProperties: Map<String, Any?>? = null
        private var userLocation: APLocation? = null
    }


    fun setAPUserId(apUserId: String?) {
        if (apUserId == null) {
            preferences?.remove(APSharedPreferences.AP_USER_ID)
        } else {
            preferences?.saveString(APSharedPreferences.AP_USER_ID, apUserId)
        }
        APUserRepository.apUserId = apUserId
    }

    fun setExternalUserId(userId: String?) {
        externalUserId = userId
    }

    fun setUserProperties(properties: Map<String, Any?>?) {
        userProperties = properties
    }

    fun setUserLocation(location: APLocation?) {
        userLocation = location
    }

    fun setDeviceId(deviceId: String) {
        APUserRepository.deviceId = deviceId
    }

    fun getAPUser() : APUser {
        return APUser(
            apId = getAPUserId(),
            externalId = externalUserId,
            deviceId = deviceId ?: "",
            properties = userProperties,
            location = userLocation
        )
    }

    private fun getAPUserId() : String? {
        if (apUserId == null) {
            apUserId = preferences?.getString(APSharedPreferences.AP_USER_ID)
        }
        return apUserId
    }
}