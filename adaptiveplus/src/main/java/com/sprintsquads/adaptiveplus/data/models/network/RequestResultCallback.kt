package com.sprintsquads.adaptiveplus.data.models.network

import com.sprintsquads.adaptiveplus.data.models.APError

/**
 * Request result callback generic class
 */
internal abstract class RequestResultCallback<T> {
    abstract fun success(response: T)
    abstract fun failure(error: APError? = null)
}