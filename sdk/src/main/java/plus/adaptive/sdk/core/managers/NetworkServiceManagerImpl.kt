package plus.adaptive.sdk.core.managers

import android.os.Build
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import plus.adaptive.sdk.core.factories.Tls12SocketFactory
import plus.adaptive.sdk.data.CUSTOM_IP_ADDRESS
import plus.adaptive.sdk.data.IS_DEBUGGABLE
import plus.adaptive.sdk.data.REQUEST_TIMEOUT
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


internal class NetworkServiceManagerImpl : NetworkServiceManager {

    companion object {
        private var token: String? = null
        private val tokenLiveData = MutableLiveData<String?>()
    }


    private var okHttpClient: OkHttpClient? = null


    override fun updateToken(token: String?) {
        NetworkServiceManagerImpl.token = token

        if (tokenLiveData.value != token) {
            tokenLiveData.postValue(token)
        }
    }

    @MainThread
    override fun getTokenLiveData(): LiveData<String?> {
        if (tokenLiveData.value != token) {
            tokenLiveData.value = token
        }

        return tokenLiveData
    }

    override fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = provideOkHttpClient()
        }
        return okHttpClient!!
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val builder = provideOkHttpClientBuilder()
        builder.addNetworkInterceptor { chain ->
            token?.let { token ->
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
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)

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