package com.sprintsquads.adaptiveplus.data.repositories

import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.data.models.APUser
import com.sprintsquads.adaptiveplus.data.models.APLocation


internal class APUserRepository(
    private val preferences: APSharedPreferences?
) {

    companion object {
        private var apUserId: String? = null
        private var externalUserId: String? = null
        private var userDevice: APUser.Device? = null
        private var userProperties: Map<String, String>? = null
        private var userLocation: APLocation? = null
        private var isEventTrackingDisabled: Boolean? = null
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

    fun setUserProperties(properties: Map<String, String>?) {
        userProperties = properties
    }

    fun setUserLocation(location: APLocation?) {
        userLocation = location
    }

    fun setUserDevice(device: APUser.Device) {
        userDevice = device
    }

    fun setIsEventTrackingDisabled(isDisabled: Boolean?) {
        if (isDisabled == null) {
            preferences?.remove(APSharedPreferences.IS_EVENT_TRACKING_DISABLED)
        } else {
            preferences?.saveBoolean(APSharedPreferences.IS_EVENT_TRACKING_DISABLED, isDisabled)
        }
        isEventTrackingDisabled = isDisabled
    }

    fun getIsEventTrackingDisabled() : Boolean {
        if (isEventTrackingDisabled == null) {
            isEventTrackingDisabled =
                preferences?.getBoolean(APSharedPreferences.IS_EVENT_TRACKING_DISABLED) ?: false
        }
        return isEventTrackingDisabled!!
    }

    fun getAPUser() : APUser {
        return APUser(
            apId = getAPUserId(),
            externalId = externalUserId,
            device = userDevice?.apply { isEventTrackingDisabled = getIsEventTrackingDisabled() },
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