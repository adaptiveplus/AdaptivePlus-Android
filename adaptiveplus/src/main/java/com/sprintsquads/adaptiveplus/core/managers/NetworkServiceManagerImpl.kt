package com.sprintsquads.adaptiveplus.core.managers

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sprintsquads.adaptiveplus.core.factories.Tls12SocketFactory
import com.sprintsquads.adaptiveplus.data.CUSTOM_IP_ADDRESS
import com.sprintsquads.adaptiveplus.data.IS_DEBUGGABLE
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


internal class NetworkServiceManagerImpl
private constructor(
    private val preferences: APSharedPreferences? = null
): NetworkServiceManager {

    companion object {
        private const val CONNECT_TIMEOUT_SECONDS: Long = 30
        private const val WRITE_TIMEOUT_SECONDS: Long = 30
        private const val READ_TIMEOUT_SECONDS: Long = 30

        private val tokenLiveData = MutableLiveData<String?>()


        fun newInstance(
            preferences: APSharedPreferences? = null
        ): NetworkServiceManagerImpl {
            return NetworkServiceManagerImpl(preferences)
        }
    }


    private var okHttpClient: OkHttpClient? = null


    private fun tokenInstance(): String? {
        return if (tokenLiveData.value == null) {
            val token = preferences?.getString(APSharedPreferences.AUTH_TOKEN)

            if (token != null) {
                tokenLiveData.postValue(token)
            }

            token
        } else {
            tokenLiveData.value
        }
    }

    override fun updateToken(token: String?) {
        tokenLiveData.postValue(token)

        if (token == null) {
            preferences?.remove(APSharedPreferences.AUTH_TOKEN)
        }
        else {
            preferences?.saveString(APSharedPreferences.AUTH_TOKEN, token)
        }
    }

    override fun getTokenLiveData(): LiveData<String?> = tokenLiveData

    override fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = provideOkHttpClient()
        }
        return okHttpClient!!
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val builder = provideOkHttpClientBuilder()
        builder.addNetworkInterceptor { chain ->
            tokenInstance()?.let { token ->
                val originalRequest = chain.request()

                val authValue = "Bearer $token"
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", authValue)
                    .build()
                chain.proceed(newRequest)
            }
                ?:
                chain.proceed(chain.request())
        }
        // Only for testing purposes in QA app
        builder.addNetworkInterceptor { chain ->
            CUSTOM_IP_ADDRESS?.let { ipAddress ->
                val originalRequest = chain.request()

                val newRequest = originalRequest.newBuilder()
                    .header("x-forwarded-for", ipAddress)
                    .build()
                chain.proceed(newRequest)
            }
                ?:
                chain.proceed(chain.request())
        }
        return builder.build()
    }

    private fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)

        if (IS_DEBUGGABLE) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            val httpLoggingInterceptor1 = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
            httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY

            builder.addNetworkInterceptor(httpLoggingInterceptor)
            builder.addNetworkInterceptor(httpLoggingInterceptor1)
        }

        return enableTls12OnPreLollipop(builder)
    }

    private fun enableTls12OnPreLollipop(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        if (Build.VERSION.SDK_INT in 16..21) {
            try {
                val trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(null as KeyStore?)
                val trustManagers = trustManagerFactory.trustManagers
                check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                    "Unexpected default trust managers:" + Arrays.toString(trustManagers)
                }
                val trustManager = trustManagers[0] as X509TrustManager
                val sc = SSLContext.getInstance("TLSv1.2")
                sc.init(null, arrayOf<TrustManager>(trustManager), null)
                builder.sslSocketFactory(Tls12SocketFactory(sc.socketFactory), trustManager)

                val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build()

                val specs = ArrayList<ConnectionSpec>()
                specs.add(cs)
                specs.add(ConnectionSpec.COMPATIBLE_TLS)
                specs.add(ConnectionSpec.CLEARTEXT)

                builder.connectionSpecs(specs)
            } catch (exc: Exception) { }
        }

        return builder
    }
}