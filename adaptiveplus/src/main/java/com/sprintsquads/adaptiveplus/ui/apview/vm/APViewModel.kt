package com.sprintsquads.adaptiveplus.ui.apview.vm

import androidx.lifecycle.*
import com.sprintsquads.adaptiveplus.core.managers.APCacheManager
import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APEntryPoint
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.Event
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import com.sprintsquads.adaptiveplus.data.repositories.APUserRepository
import com.sprintsquads.adaptiveplus.data.repositories.APViewRepository
import com.sprintsquads.adaptiveplus.utils.*


internal class APViewModel(
    private val apViewRepository: APViewRepository,
    private val userRepository: APUserRepository,
    private val cacheManager: APCacheManager,
    private val preferences: APSharedPreferences
) : ViewModel(), APViewModelDelegateProtocol, APEntryPointViewModelProvider {

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
        dataModel: APViewDataModel,
        isCached: Boolean = false
    ) {
        if (!isCached || _apViewDataModelLiveData.value == null) {
            if (!isCached) {
                saveAPViewDataModelToCache(dataModel.id, dataModel)
            }

            _apViewDataModelLiveData.value = dataModel
        }
    }

    fun requestAPViewDataModel(apViewId: String) {
        apViewRepository.requestAPView(
            apViewId, object: RequestResultCallback<APViewDataModel>() {
                override fun success(response: APViewDataModel) {
                    runOnMainThread {
                        setAPViewDataModel(response)
                    }
                }

                override fun failure(error: Any?) { }
            }
        )
    }

    override fun runActions(
        actions: List<APAction>,
        campaignId: String
    ) {
        for (action in actions) {
            runAction(action, campaignId)
        }
    }

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

    override fun onAPStoriesDismissed() {
        _entryPointViewModelMap.forEach { (_, entryPointViewModel) ->
            entryPointViewModel.reset()
        }
    }

    @Deprecated(
            message = "Not working. Only for testing purposes.",
            level = DeprecationLevel.WARNING)
    fun loadAPViewMockDataModelFromAssets(apViewId: String) {
        cacheManager.loadAPViewMockDataModelFromAssets(apViewId) { dataModel ->
            runDelayedTask({ setAPViewDataModel(dataModel) }, 2000)
        }
    }

    fun loadAPViewDataModelFromCache(apViewId: String) {
        cacheManager.loadAPViewDataModelFromCache(apViewId) { dataModel ->
            if (dataModel != null) {
                setAPViewDataModel(
                    dataModel = dataModel,
                    isCached = true
                )
            }
        }
    }

    private fun saveAPViewDataModelToCache(apViewId: String, dataModel: APViewDataModel) {
        cacheManager.saveAPViewDataModelToCache(apViewId, dataModel)
    }

    override fun getAPEntryPointViewModel(entryPoint: APEntryPoint): APEntryPointViewModel? {
        if (!_entryPointViewModelMap.contains(entryPoint.id)) {
            _entryPointViewModelMap[entryPoint.id] =
                APEntryPointViewModel(entryPoint, this, preferences, userRepository)
        }
        return _entryPointViewModelMap[entryPoint.id]
    }
}