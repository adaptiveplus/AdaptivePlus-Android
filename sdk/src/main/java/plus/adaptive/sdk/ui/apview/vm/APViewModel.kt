package plus.adaptive.sdk.ui.apview.vm

import androidx.lifecycle.*
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.APEntryPoint
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APViewDataModel
import plus.adaptive.sdk.data.models.Event
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.data.repositories.APViewRepository
import plus.adaptive.sdk.ui.apview.APEntryPointLifecycleListener
import plus.adaptive.sdk.utils.*


internal class APViewModel(
    private val apViewRepository: APViewRepository,
    private val userRepository: APUserRepository,
    private val cacheManager: APCacheManager,
    private val preferences: APSharedPreferences
) : ViewModel(), APViewModelDelegateProtocol, APEntryPointViewModelProvider {

    val apViewDataModelLiveData: LiveData<APViewDataModel?>
        get() = _apViewDataModelLiveData
    val actionEventLiveData: LiveData<Event<APAction>>
        get() = _actionEventLiveData
    val magnetizeEntryPointEventLiveData: LiveData<Event<String>>
        get() = _magnetizeEntryPointEventLiveData

    private val _apViewDataModelLiveData = MutableLiveData<APViewDataModel?>()
    private val _actionEventLiveData = MutableLiveData<Event<APAction>>()
    private val _magnetizeEntryPointEventLiveData = MutableLiveData<Event<String>>()
    private val _apStoriesPauseNumberLiveData = MutableLiveData<Int>().apply { value = 0 }
    private val _isAPStoriesPausedLiveData =
        Transformations.map(_apStoriesPauseNumberLiveData) { it > 0 }

    private val _entryPointViewModelMap = mutableMapOf<String, APEntryPointViewModel>()
    private var _resumedEntryPointId: String? = null
    private var _visibleEntryPointsPositionRange: IntRange = 0..0


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

                override fun failure(error: APError?) { }
            }
        )
    }

    override fun runActions(actions: List<APAction>) {
        for (action in actions) {
            runAction(action)
        }
    }

    private fun runAction(action: APAction) {
        _actionEventLiveData.value = Event(action)
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
        _apViewDataModelLiveData.value?.let { dataModel ->
            dataModel.entryPoints.findLast {
                getAPEntryPointViewModel(it)?.isActive() != true
            }?.let { entryPoint ->
                _magnetizeEntryPointEventLiveData.value = Event(entryPoint.id)
            }
        }
        _entryPointViewModelMap.forEach { (_, entryPointViewModel) ->
            entryPointViewModel.reset()
        }
    }

    override fun getAutoScrollPeriod(): Long? {
        return _apViewDataModelLiveData.value?.options?.autoScroll?.let { it * 1000 }?.toLong()
    }

    override fun showBorder(): Boolean {
        return _apViewDataModelLiveData.value?.options?.showBorder == true
    }

    override fun getAPViewId(): String {
        return _apViewDataModelLiveData.value?.id ?: ""
    }

    @Deprecated(
            message = "Not working. Only for testing purposes.",
            level = DeprecationLevel.WARNING)
    fun loadAPViewMockDataModelFromAssets(apViewId: String) {
        cacheManager.loadAPViewMockDataModelFromAssets(apViewId) { dataModel ->
            setAPViewDataModel(dataModel)
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
            val entryPointLifecycleListener = object: APEntryPointLifecycleListener {
                override fun onReady(isReady: Boolean) { onEntryPointReady(entryPoint.id, isReady) }
                override fun onComplete() { onEntryPointComplete(entryPoint.id) }
                override fun onError() {  }
            }

            _entryPointViewModelMap[entryPoint.id] =
                APEntryPointViewModel(
                    entryPoint = entryPoint,
                    preferences = preferences,
                    userRepository = userRepository,
                    lifecycleListener = entryPointLifecycleListener,
                    apViewModelDelegate = this
                )
        }
        return _entryPointViewModelMap[entryPoint.id]
    }

    private fun onEntryPointReady(id: String, isReady: Boolean) {
        _apViewDataModelLiveData.value?.let { dataModel ->
            val firstVisibleEntryPoint =
                dataModel.entryPoints.getOrNull(_visibleEntryPointsPositionRange.first)

            if (getAutoScrollPeriod() != null && isReady &&
                id == firstVisibleEntryPoint?.id && _resumedEntryPointId == null
            ) {
                resumeEntryPoint(id)
            }
        }
    }

    private fun onEntryPointComplete(id: String) {
        if (id == _resumedEntryPointId) {
            _resumedEntryPointId = null

            _apViewDataModelLiveData.value?.let { dataModel ->
                if (getAutoScrollPeriod() != null) {
                    val resumedEntryPointPosition =
                        dataModel.entryPoints.indexOfFirst {
                            it.id == id
                        }
                    val entryPointToResumePosition =
                        (resumedEntryPointPosition + 1) % dataModel.entryPoints.size
                    val entryPointToResume = dataModel.entryPoints[entryPointToResumePosition]

                    resumeEntryPoint(entryPointToResume.id)
                }
            }
        }
    }

    private fun pauseEntryPoint(id: String) {
        _apViewDataModelLiveData.value?.let { dataModel ->
            dataModel.entryPoints.find { it.id == id }?.let { entryPoint ->
                getAPEntryPointViewModel(entryPoint)?.run {
                    if (id == _resumedEntryPointId) {
                        _resumedEntryPointId = null
                    }
                    pause()
                }
            }
        }
    }

    private fun resumeEntryPoint(id: String) {
        _apViewDataModelLiveData.value?.let { dataModel ->
            dataModel.entryPoints.find { it.id == id }?.let { entryPoint ->
                _magnetizeEntryPointEventLiveData.value = Event(id)
                getAPEntryPointViewModel(entryPoint)?.run {
                    _resumedEntryPointId = id
                    resume()
                }
            }
        }
    }

    fun onResume() {
        resetAutoScroll()
    }

    fun onPause() {
        _resumedEntryPointId?.let { pauseEntryPoint(it) }
    }

    fun setVisibleEntryPointsPositionRange(range: IntRange) {
        this._visibleEntryPointsPositionRange = range
    }

    private fun resetAutoScroll() {
        _apViewDataModelLiveData.value?.let { dataModel ->
            if (getAutoScrollPeriod() != null) {
                val range = _visibleEntryPointsPositionRange
                val resumedEntryPointPosition =
                    dataModel.entryPoints.indexOfFirst {
                        it.id == _resumedEntryPointId
                    }

                if (resumedEntryPointPosition !in range) {
                    _resumedEntryPointId?.let { pauseEntryPoint(it) }
                    dataModel.entryPoints.getOrNull(range.first)?.id?.let { resumeEntryPoint(it) }
                }
            }
        }
    }
}