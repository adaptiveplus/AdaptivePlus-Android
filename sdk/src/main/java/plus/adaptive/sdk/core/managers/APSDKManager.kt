package plus.adaptive.sdk.core.managers

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import plus.adaptive.sdk.data.models.network.RequestResultCallback


internal interface APSDKManager {

    @MainThread
    fun start()

    @MainThread
    fun stop()

    fun isStartedLiveData() : LiveData<Boolean>

    fun authorize(
        isForced: Boolean = false,
        requestResultCallback: RequestResultCallback<Any?>? = null
    )

}