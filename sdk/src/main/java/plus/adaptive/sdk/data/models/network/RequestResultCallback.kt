package plus.adaptive.sdk.data.models.network

import plus.adaptive.sdk.data.models.APError

/**
 * Request result callback generic class
 */
internal abstract class RequestResultCallback<T> {
    abstract fun success(response: T)
    abstract fun failure(error: APError? = null)
}