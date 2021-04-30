package plus.adaptive.sdk.core.managers

import plus.adaptive.sdk.data.models.APAuthCredentials


internal class APAuthCredentialsManager {

    companion object {
        private const val clientId: String = "4a99f85d-cfec-4a73-a487-ec3c116eb5d2"
        private const val clientSecret: String = "2b3ea139d809b9ea47e521ad3336c4dc"
        private const val grantType: String = "channel_credentials"
        private var channelSecret: String? = null
        private var testChannelSecret: String? = null
    }


    fun setApiKey(apiKey: String) {
        channelSecret = apiKey
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING)
    fun setTestApiKey(channelSecret: String?) {
        testChannelSecret = channelSecret
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
}