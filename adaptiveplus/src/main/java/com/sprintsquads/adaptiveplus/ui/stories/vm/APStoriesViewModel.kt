package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.data.repositories.APStoriesRepository
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegate
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEvent
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapStatus


internal class APStoriesViewModel(
    private val repository: APStoriesRepository,
    private val preferences: APSharedPreferences
): ViewModel() {

    val snapReadinessUpdatedEventLiveData: LiveData<String>
        get() = _snapReadinessUpdatedEventLiveData
    val isIdleStateLiveData: LiveData<Boolean>
        get() = _isIdleStateLiveData
    val snapEventLiveData: LiveData<APSnapEvent>
        get() = _snapEventLiveData

    val isStoriesExternallyPausedLiveData: LiveData<Boolean>?
        get() = apViewModelDelegate?.isAPStoriesPausedLiveData()

    private val _snapReadinessUpdatedEventLiveData = MutableLiveData<String>()
    private val _isIdleStateLiveData = MutableLiveData<Boolean>().apply { value = true }
    private val _snapStatusLiveData = MutableLiveData<APSnapStatus>()
    private val _snapEventLiveData = MutableLiveData<APSnapEvent>()

    private var apViewModelDelegate: APViewModelDelegate? = null

    private val snapReadinessMap = mutableMapOf<String, Boolean>()

    private var storiesCount: Int = -1


    fun init(
        stories: List<APStory>,
        apViewModelDelegate: APViewModelDelegate? = null
    ) {
        this.apViewModelDelegate = apViewModelDelegate
        setStateIsIdle(true)

        snapReadinessMap.clear()
        for (story in stories) {
            story.snaps.map { it.id to false }.toMap().let {
                snapReadinessMap.putAll(it)
            }
        }

        storiesCount = stories.size
    }

    fun setAPViewModelDelegate(delegate: APViewModelDelegate?) {
        this.apViewModelDelegate = delegate
    }

    fun updateSnapReadiness(id: String, isReady: Boolean) {
        if (id in snapReadinessMap) {
            snapReadinessMap[id] = isReady
            _snapReadinessUpdatedEventLiveData.value = id
        }
    }

    fun isSnapReady(id: String): Boolean {
        return snapReadinessMap.getOrElse(id, { false })
    }

    fun runActions(actions: List<APAction>, campaignId: String) {
        apViewModelDelegate?.runActions(actions, campaignId)
    }

    fun setStateIsIdle(isIdle: Boolean) {
        _isIdleStateLiveData.value = isIdle
    }

    fun isStateIdle() : Boolean = _isIdleStateLiveData.value == true

    fun isStoriesExternallyPaused() : Boolean = isStoriesExternallyPausedLiveData?.value == true

    fun updateStoryProgressState(snapId: String, state: APSnapStatus.State) {
        _snapStatusLiveData.value =
            APSnapStatus(
                snapId = snapId,
                state = state
            )
    }

    fun onSnapEvent(snapId: String, event: APSnapEvent.Type) {
        _snapEventLiveData.value =
            APSnapEvent(
                snapId = snapId,
                event = event
            )
    }
}