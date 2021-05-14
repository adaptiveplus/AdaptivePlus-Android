package plus.adaptive.sdk.core.managers

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import okhttp3.OkHttpClient
import plus.adaptive.sdk.data.models.AuthTokenData


internal interface NetworkServiceManager {
    /**
     * Method to update auth token
     *
     * @param token - auth token
     * @param expiresIn - auth token expires in ${value} seconds
     */
    fun updateToken(token: String?, expiresIn: Int?)

    /**
     * Getter of token live data
     *
     * @return token live data
     */
    @MainThread
    fun getTokenLiveData(): LiveData<AuthTokenData?>

    /**
     * Getter of is token expired
     *
     * @return is token expired
     */
    fun isTokenExpired(): Boolean

    /**
     * Getter method returning OkHttpClient instance
     *
     * @return OkHttpClient instance
     * @see OkHttpClient
     */
    fun getOkHttpClient(): OkHttpClient
}