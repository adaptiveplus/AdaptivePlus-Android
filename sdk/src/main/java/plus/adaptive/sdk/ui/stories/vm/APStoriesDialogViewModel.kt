package plus.adaptive.sdk.ui.stories.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.ui.apview.vm.APViewVMDelegateProtocol


internal class APStoriesDialogViewModel(
    private val apViewVMDelegate: APViewVMDelegateProtocol
) : ViewModel(), APStoriesDialogViewModelDelegateProtocol {

    private val _isStoriesExternallyPausedLiveData = apViewVMDelegate.isAPStoriesPausedLiveData()
    private val _isIdleStateLiveData = MutableLiveData<Boolean>().apply { value = true }

    private val _isStoriesPausedLiveData = MediatorLiveData<Boolean>().apply {
        addSource(_isIdleStateLiveData) { isIdleState ->
            value = !isIdleState || _isStoriesExternallyPausedLiveData.value == true
        }
        addSource(_isStoriesExternallyPausedLiveData) { isExternallyPaused ->
            value = _isIdleStateLiveData.value != true || isExternallyPaused
        }
    }


    fun setStateIsIdle(isIdle: Boolean) {
        _isIdleStateLiveData.value = isIdle
    }

    override fun runActions(actions: List<APAction>) {
        apViewVMDelegate.runActions(actions)
    }

    override fun isAPStoriesPausedLiveData(): LiveData<Boolean> {
        return _isStoriesPausedLiveData
    }

    override fun getAPViewId(): String {
        return apViewVMDelegate.getAPViewId()
    }
}