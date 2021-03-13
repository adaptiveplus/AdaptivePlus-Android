package com.sprintsquads.adaptiveplus.data.models.network

/**
 * Request result callback generic class
 */
internal abstract class RequestResultCallback<T> {
    abstract fun success(response: T)
    abstract fun failure(error: Any? = null)
}