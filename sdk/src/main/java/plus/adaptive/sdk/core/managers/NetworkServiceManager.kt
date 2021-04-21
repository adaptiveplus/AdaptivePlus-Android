package plus.adaptive.sdk.core.managers

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import okhttp3.OkHttpClient


internal interface NetworkServiceManager {
    /**
     * Method to update auth token
     *
     * @param token - auth token
     */
    fun updateToken(token: String?)

    /**
     * Getter of token live data
     *
     * @return token live data
     */
    @MainThread
    fun getTokenLiveData(): LiveData<String?>

    /**
     * Getter method returning OkHttpClient instance
     *
     * @return OkHttpClient instance
     * @see OkHttpClient
     */
    fun getOkHttpClient(): OkHttpClient
}