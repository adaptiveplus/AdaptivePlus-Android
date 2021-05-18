package plus.adaptive.sdk.data.repositories

import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.data.LOCALE
import plus.adaptive.sdk.data.models.APUser
import plus.adaptive.sdk.data.models.APLocation


internal class APUserRepository(
    private val preferences: APSharedPreferences?,
    private val authCredentialsManager: APAuthCredentialsManager
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
        val apiKey = authCredentialsManager.getAuthCredentials()?.channelSecret ?: ""

        if (apUserId == null) {
            preferences?.remove("${apiKey}_${APSharedPreferences.AP_USER_ID}")
        } else {
            preferences?.saveString("${apiKey}_${APSharedPreferences.AP_USER_ID}", apUserId)
        }

        APUserRepository.apUserId = apUserId
    }

    fun setExternalUserId(userId: String?) {
        if (userId != null) {
            val apiKey = authCredentialsManager.getAuthCredentials()?.channelSecret ?: ""
            val oldUserId = preferences?.getString("${apiKey}_${APSharedPreferences.EXTERNAL_USER_ID}")

            if (userId != oldUserId) {
                setAPUserId(null)
                preferences?.saveString("${apiKey}_${APSharedPreferences.EXTERNAL_USER_ID}", userId)
            }
        }

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
        userDevice?.apply {
            locale = LOCALE.language
            isEventTrackingDisabled = getIsEventTrackingDisabled()
        }

        return APUser(
            apId = getAPUserId(),
            externalId = externalUserId,
            device = userDevice,
            properties = userProperties,
            location = userLocation
        )
    }

    fun getAPUserId() : String? {
        if (apUserId == null) {
            val apiKey = authCredentialsManager.getAuthCredentials()?.channelSecret ?: ""
            apUserId = preferences?.getString("${apiKey}_${APSharedPreferences.AP_USER_ID}")
        }
        return apUserId
    }
}