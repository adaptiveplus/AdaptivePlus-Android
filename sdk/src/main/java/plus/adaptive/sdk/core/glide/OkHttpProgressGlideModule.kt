package plus.adaptive.sdk.core.glide

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.*
import okio.*
import java.io.InputStream


@GlideModule
internal class OkHttpProgressGlideModule : AppGlideModule() {

    companion object {
        fun forget(url: String) {
            DispatchingProgressListener.forget(url)
        }

        fun expect(url: String, listener: UIProgressListener?) {
            DispatchingProgressListener.expect(url, listener)
        }
    }


    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = OkHttpClient.Builder()
            .addInterceptor(createInterceptor(DispatchingProgressListener()))
            .build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }

    private fun createInterceptor(listener: ResponseProgressListener): Interceptor {
        return Interceptor { chain ->
            val request: Request = chain.request()
            val response: Response = chain.proceed(request)
            response.newBuilder()
                .body(OkHttpProgressResponseBody(
                    request.url(), response.body(), listener))
                .build()
        }
    }

    interface UIProgressListener {
        fun onProgress(bytesRead: Long, expectedLength: Long)

        /**
         * Control how often the listener needs an update. 0% and 100% will always be dispatched.
         * @return in percentage (0.2 = call [.onProgress] around every 0.2 percent of progress)
         */
        fun getGranularityPercentage() : Float
    }

    private interface ResponseProgressListener {
        fun update(url: HttpUrl, bytesRead: Long, contentLength: Long)
    }


    private class DispatchingProgressListener : ResponseProgressListener {

        companion object {
            private val LISTENERS = mutableMapOf<String, UIProgressListener>()
            private val PROGRESSES = mutableMapOf<String, Long>()


            fun forget(url: String) {
                LISTENERS.remove(url)
                PROGRESSES.remove(url)
            }

            fun expect(url: String, listener: UIProgressListener?) {
                listener?.let {
                    LISTENERS[url] = it
                }
            }
        }


        private val handler: Handler = Handler(Looper.getMainLooper())


        override fun update(url: HttpUrl, bytesRead: Long, contentLength: Long) {
            val key = url.toString()
            val listener = LISTENERS[key] ?: return
            if (contentLength <= bytesRead) {
                forget(key)
            }
            if (needsDispatch(key, bytesRead, contentLength, listener.getGranularityPercentage())) {
                handler.post { listener.onProgress(bytesRead, contentLength) }
            }
        }

        private fun needsDispatch(key: String, current: Long, total: Long, granularity: Float): Boolean {
            if (granularity == 0f || current == 0L || total == current) {
                return true
            }
            val percent = 100f * current / total
            val currentProgress = (percent / granularity).toLong()
            val lastProgress = PROGRESSES[key]
            return if (lastProgress == null || currentProgress != lastProgress) {
                PROGRESSES[key] = currentProgress
                true
            } else {
                false
            }
        }
    }

    private class OkHttpProgressResponseBody(
        private val url: HttpUrl,
        private val responseBody: ResponseBody?,
        private val progressListener: ResponseProgressListener
    ) : ResponseBody() {

        private var bufferedSource: BufferedSource? = null


        override fun contentType(): MediaType? {
            return responseBody?.contentType()
        }

        override fun contentLength(): Long {
            return responseBody?.contentLength() ?: -1
        }

        override fun source(): BufferedSource? {
            if (bufferedSource == null) {
                responseBody?.source()?.let {
                    bufferedSource = Okio.buffer(source(it))
                }
            }
            return bufferedSource
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                private var totalBytesRead = 0L

                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    val fullLength = responseBody?.contentLength() ?: -1L

                    if (fullLength != -1L) {
                        if (bytesRead == -1L) { // this source is exhausted
                            totalBytesRead = fullLength
                        } else {
                            totalBytesRead += bytesRead
                        }

                        progressListener.update(url, totalBytesRead, fullLength)
                    }

                    return bytesRead
                }
            }
        }
    }
}