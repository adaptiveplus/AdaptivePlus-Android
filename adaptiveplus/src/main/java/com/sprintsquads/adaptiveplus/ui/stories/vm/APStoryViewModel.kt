package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.data.models.actions.APAction
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.data.repositories.APUserRepository
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEventInfo
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapState
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapStateInfo


internal class APStoryViewModel(
    private val story: APStory,
    private val storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegateProtocol,
    private val preferences: APSharedPreferences,
    private val userRepository: APUserRepository
) : ViewModel(), APStoryViewModelDelegateProtocol {

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
        storiesDialogViewModelDelegate.runActions(actions)
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

    fun setStoryCampaignWatched() {
        val userId = userRepository.getAPUser().apId ?: ""
        val prefKey = "${userId}_${story.campaignId}_${APSharedPreferences.IS_CAMPAIGN_WATCHED}"
        preferences.saveBoolean(prefKey, true)
    }

    override fun getAPViewId(): String {
        return storiesDialogViewModelDelegate.getAPViewId()
    }

    override fun getCampaignId(): String {
        return story.campaignId
    }
}