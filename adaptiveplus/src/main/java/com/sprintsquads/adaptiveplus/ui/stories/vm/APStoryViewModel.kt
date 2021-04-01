package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences.Companion.STORY_WATCHED_STATE
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEventInfo
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapState
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapStateInfo


internal class APStoryViewModel(
    private val story: APStory,
    private val storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegate,
    private val preferences: APSharedPreferences
) : ViewModel(), APStoryViewModelDelegate {

    val snapReadinessUpdatedEventLiveData: LiveData<String>
        get() = _snapReadinessUpdatedEventLiveData
    val snapEventInfoLiveData: LiveData<APSnapEventInfo>
        get() = _snapEventInfoLiveData

    val isStoriesPausedLiveData: LiveData<Boolean>
        get() = storiesDialogViewModelDelegate.isAPStoriesPausedLiveData()

    private val _snapReadinessUpdatedEventLiveData = MutableLiveData<String>()
    private val _snapStatusLiveData = MutableLiveData<APSnapStateInfo>()
    private val _snapEventInfoLiveData = MutableLiveData<APSnapEventInfo>()

    private val snapReadinessMap = mutableMapOf<String, Boolean>()


    init {
        snapReadinessMap.clear()
        story.snaps.map { it.id to false }.toMap().let {
            snapReadinessMap.putAll(it)
        }
    }

    override fun updateSnapReadiness(id: String, isReady: Boolean) {
        if (id in snapReadinessMap) {
            snapReadinessMap[id] = isReady
            _snapReadinessUpdatedEventLiveData.value = id
        }
    }

    fun isSnapReady(id: String): Boolean {
        return snapReadinessMap.getOrElse(id, { false })
    }

    override fun runActions(actions: List<APAction>) {
        storiesDialogViewModelDelegate.runActions(actions, story.campaignId ?: "")
    }

    fun isStoriesPaused() : Boolean = isStoriesPausedLiveData.value == true

    fun updateStoryProgressState(snapId: String, state: APSnapState) {
        _snapStatusLiveData.value =
            APSnapStateInfo(
                snapId = snapId,
                state = state
            )
    }

    override fun onSnapEvent(eventInfo: APSnapEventInfo) {
        _snapEventInfoLiveData.value = eventInfo
    }

    fun setStoryWatched() {
        val userId = AdaptivePlusSDK().getUserId() ?: ""
        preferences.saveBoolean("${userId}_${story.id}_$STORY_WATCHED_STATE", true)
    }
}