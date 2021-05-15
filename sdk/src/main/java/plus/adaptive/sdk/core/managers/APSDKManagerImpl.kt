package plus.adaptive.sdk.core.managers

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.network.APConfigsResponseBody
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.models.network.RequestState
import plus.adaptive.sdk.data.repositories.APAuthRepository


internal class APSDKManagerImpl(
    private val networkServiceManager: NetworkServiceManager?,
    private val authRepository: APAuthRepository?
) : APSDKManager {

    companion object {
        private val isStartedLiveData = MutableLiveData<Boolean>().apply { value = false }
        private var tokenRequestState = RequestState.NONE
    }


    @MainThread
    override fun start() {
        isStartedLiveData.value = true
        tokenRequestState = RequestState.NONE
    }

    @MainThread
    override fun stop() {
        isStartedLiveData.value = false
        tokenRequestState = RequestState.NONE
    }

    override fun isStartedLiveData(): LiveData<Boolean> = isStartedLiveData

    override fun authorize(
        isForced: Boolean,
        requestResultCallback: RequestResultCallback<Any?>?
    ) {
        if (isStartedLiveData.value != true ||
            (!isForced && tokenRequestState == RequestState.IN_PROCESS)
        ) {
            return
        }

        if (networkServiceManager?.isTokenExpired() != true) {
            requestResultCallback?.success(null)
            requestAPConfigs()
            return
        }

        tokenRequestState = RequestState.IN_PROCESS

        authRepository?.requestToken(
            object : RequestResultCallback<String>() {
                override fun success(response: String) {
                    tokenRequestState = RequestState.SUCCESS
                    requestAPConfigs()
                    requestResultCallback?.success(null)
                }

                override fun failure(error: APError?) {
                    tokenRequestState = RequestState.ERROR
                    requestResultCallback?.failure(error)
                }
            }
        )
    }

    private fun requestAPConfigs() {
        authRepository?.requestAPConfigs(
            object : RequestResultCallback<APConfigsResponseBody>() {
                override fun success(response: APConfigsResponseBody) {}
                override fun failure(error: APError?) {}
            }
        )
    }
}