package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegate


internal class APStoriesDialogViewModel(
    private val apViewModelDelegate: APViewModelDelegate
) : ViewModel(), APStoriesDialogViewModelDelegate {

    private val _isStoriesExternallyPausedLiveData = apViewModelDelegate.isAPStoriesPausedLiveData()
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

    override fun runActions(actions: List<APAction>, campaignId: String) {
        apViewModelDelegate.runActions(actions, campaignId)
    }

    override fun isAPStoriesPausedLiveData(): LiveData<Boolean> {
        return _isStoriesPausedLiveData
    }
}