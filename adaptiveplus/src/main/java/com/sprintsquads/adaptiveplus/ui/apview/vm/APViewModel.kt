package com.sprintsquads.adaptiveplus.ui.apview.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.Event
import com.sprintsquads.adaptiveplus.data.repositories.APViewRepository
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.utils.*
import com.sprintsquads.adaptiveplus.utils.loadAPViewMockDataModelFromAssets
import com.sprintsquads.adaptiveplus.utils.loadAPViewDataModelFromCache
import com.sprintsquads.adaptiveplus.utils.removeAPViewDataModelFromCache


internal class APViewModel(
    application: Application,
    repository: APViewRepository
) : AndroidViewModel(application), APViewModelDelegate, APEntryPointViewModelProvider {

    val apViewDataModelLiveData: LiveData<APViewDataModel?>
        get() = _apViewDataModelLiveData
    val actionEventLiveData: LiveData<Event<Pair<APAction, String>>>
        get() = _actionEventLiveData

    private val _apViewDataModelLiveData = MutableLiveData<APViewDataModel?>()
    private val _actionEventLiveData = MutableLiveData<Event<Pair<APAction, String>>>()
    private val _apStoriesPauseNumberLiveData = MutableLiveData<Int>().apply { value = 0 }
    private val _isAPStoriesPausedLiveData =
        Transformations.map(_apStoriesPauseNumberLiveData) { it > 0 }

    private val _entryPointViewModelMap = mutableMapOf<String, APEntryPointViewModel>()


    private fun setAPViewDataModel(
        dataModel: APViewDataModel?,
        isCached: Boolean = false,
        isForceUpdate: Boolean = false
    ) {
        if (dataModel == null) {
            if (isForceUpdate) {
                _apViewDataModelLiveData.value = null
            } else {
                _apViewDataModelLiveData.value = _apViewDataModelLiveData.value
            }
        }
        else if (isForceUpdate || !isCached || _apViewDataModelLiveData.value == null) {
            if (!isCached) {
                saveAPViewDataModelToCache(dataModel.id, dataModel)
            }

            _apViewDataModelLiveData.value = dataModel
        }
    }

    fun requestAPViewDataModel(apViewId: String) {
        // TODO: implement
//        _apViewId = apViewId
//        val location = AdaptivePlusSDK().getUserLocation()
//
//        repository.requestAdaptiveComponent(
//                apViewId, location, object: AdaptivePlusCallback<AdaptiveTemplate>() {
//            override fun success(response: AdaptiveTemplate) {
//                runOnMainThread {
//                    setTemplate(response)
//                }
//            }
//
//            override fun failure(error: Any?) {
//                runOnMainThread {
//                    setTemplate(null)
//                }
//            }
//        }
//        )
    }

    override fun runActions(
        actions: List<APAction>,
        campaignId: String
    ) {
        for (action in actions) {
            // TODO: uncomment on analytics
//            AdaptiveAnalytics.logEvent(
//                action = action,
//                campaignId = campaignId,
//                apViewId = _apViewId
//            )

            runAction(action, campaignId)
        }
    }

    /**
     * Method to execute adaptive plus action
     *
     * @param action - a adaptive plus action to execute
     * @see APAction
     */
    private fun runAction(action: APAction, campaignId: String) {
        _actionEventLiveData.value =
                Event(Pair(action, campaignId))
    }

    override fun isAPStoriesPausedLiveData(): LiveData<Boolean> {
        return _isAPStoriesPausedLiveData
    }

    override fun pauseAPStories() {
        _apStoriesPauseNumberLiveData.value = _apStoriesPauseNumberLiveData.value?.inc() ?: 1
    }

    override fun resumeAPStories() {
        _apStoriesPauseNumberLiveData.value = _apStoriesPauseNumberLiveData.value?.dec() ?: 0
    }

    @Deprecated(
            message = "Not working. Only for testing purposes.",
            level = DeprecationLevel.WARNING)
    fun loadAPViewMockDataModelFromAssets(apViewId: String) {
        getApplication<Application>().applicationContext?.let { ctx ->
            loadAPViewMockDataModelFromAssets(
                ctx = ctx,
                apViewId = apViewId
            ) { dataModel ->
                runDelayedTask({ setAPViewDataModel(dataModel) }, 2000)
            }
        }
    }

    fun loadAPViewDataModelFromCache(apViewId: String, isForceUpdate: Boolean = false) {
        val userId = AdaptivePlusSDK().getUserId() ?: ""

        getApplication<Application>().applicationContext?.let { ctx ->
            loadAPViewDataModelFromCache(
                ctx = ctx,
                apViewId = apViewId,
                userId = userId
            ) { dataModel ->
                if (isForceUpdate || dataModel != null) {
                    setAPViewDataModel(
                        dataModel = dataModel,
                        isCached = true,
                        isForceUpdate = isForceUpdate
                    )
                }
            }
        }
    }

    private fun saveAPViewDataModelToCache(apViewId: String, dataModel: APViewDataModel) {
        val userId = AdaptivePlusSDK().getUserId() ?: ""

        getApplication<Application>().applicationContext?.let { ctx ->
            saveAPViewDataModelToCache(
                ctx = ctx,
                apViewId = apViewId,
                userId = userId,
                dataModel = dataModel
            )
        }
    }

    private fun removeAPViewDataModelFromCache(apViewId: String) {
        val userId = AdaptivePlusSDK().getUserId() ?: ""

        getApplication<Application>().applicationContext?.let { ctx ->
            removeAPViewDataModelFromCache(
                ctx = ctx,
                apViewId = apViewId,
                userId = userId
            )
        }
    }

    override fun getAPEntryPointViewModel(id: String): APEntryPointViewModel? {
        if (!_entryPointViewModelMap.contains(id)) {
            _entryPointViewModelMap[id] = APEntryPointViewModel(this)
        }
        return _entryPointViewModelMap[id]
    }
}