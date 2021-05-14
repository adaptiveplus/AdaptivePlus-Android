package plus.adaptive.sdk.core.managers

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData


internal interface APSDKManager {

    @MainThread
    fun start()

    @MainThread
    fun stop()

    fun isStartedLiveData() : LiveData<Boolean>

    fun authorize(isForced: Boolean = false)

}