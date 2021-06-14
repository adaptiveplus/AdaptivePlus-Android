package plus.adaptive.sdk.ui.components.poll

import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APPollRepository
import plus.adaptive.sdk.ui.components.core.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.core.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.core.vm.APBaseComponentViewModel


internal class APPollComponentViewModel(
    containerViewModel: APComponentContainerViewModel,
    lifecycleListener: APComponentLifecycleListener,
    private val component: APPollComponent,
    private val repository: APPollRepository
) : APBaseComponentViewModel(containerViewModel, lifecycleListener) {

    private var pollData: APPollData? = null


    override fun prepare() {
        lifecycleListener.onReady(false)
        mComponentViewController?.prepare()

        repository.requestPollData(component.id, object: RequestResultCallback<APPollData>() {
            override fun success(response: APPollData) {
                pollData = response
                reset()
            }

            override fun failure(error: APError?) {
                reset()
            }
        })
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {
        mComponentViewController?.reset()
    }

    fun getPollData(): APPollData? = pollData

    fun onPollBuildSuccess() {
        lifecycleListener.onReady(true)
    }

    fun onPollBuildFail() {
        lifecycleListener.onError()
    }
}