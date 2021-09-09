package plus.adaptive.sdk.ui.apview.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.data.models.*
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.APCarouselViewDataModel
import plus.adaptive.sdk.data.models.APEntryPoint
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.Event
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.models.story.APTemplateDataModel
import plus.adaptive.sdk.data.models.story.Campaign
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.data.repositories.APViewRepository
import plus.adaptive.sdk.ui.apview.APEntryPointLifecycleListener
import plus.adaptive.sdk.ui.apview.newVm.CampaignViewModel
import plus.adaptive.sdk.utils.runOnMainThread


internal class APViewViewModel(
    private val apViewRepository: APViewRepository,
    private val userRepository: APUserRepository,
    private val cacheManager: APCacheManager,
    private val preferences: APSharedPreferences
) : ViewModel(), APViewVMDelegateProtocol, APEntryPointViewModelProvider {

    val apCarouselViewDataModelLiveData: LiveData<APCarouselViewDataModel?>
        get() = _apCarouselViewDataModelLiveData
    val storyDataModelLiveData: LiveData<APTemplateDataModel?>
        get() = _storyDataModelLiveData
    val actionEventLiveData: LiveData<Event<APAction>>
        get() = _actionEventLiveData
    val magnetizeEntryPointEventLiveData: LiveData<Event<String>>
        get() = _magnetizeEntryPointEventLiveData

    private val _apCarouselViewDataModelLiveData = MutableLiveData<APCarouselViewDataModel?>()
    private val _storyDataModelLiveData = MutableLiveData<APTemplateDataModel?>()
    private val _actionEventLiveData = MutableLiveData<Event<APAction>>()
    private val _magnetizeEntryPointEventLiveData = MutableLiveData<Event<String>>()
    private val _apStoriesPauseNumberLiveData = MutableLiveData<Int>().apply { value = 0 }
    private val _isAPStoriesPausedLiveData =
        Transformations.map(_apStoriesPauseNumberLiveData) { it > 0 }

    private val _entryPointViewModelMap = mutableMapOf<String, APEntryPointViewModel>()
    private val _storyViewModelMap = mutableMapOf<String, CampaignViewModel>()
    private var _resumedEntryPointId: String? = null
    private var _visibleEntryPointsPositionRange: IntRange = 0..0

    private var id: String = ""

    private fun setAPCarouselViewDataModel(
        dataModel: APCarouselViewDataModel,
        isCached: Boolean = false,
        appViewId: String,
        showCount: Boolean = true
    ) {
        sortAndFilterCampaigns(dataModel, showCount)
        if (!isCached) {
            saveAPCarouselViewDataModelToCache(appViewId, dataModel)
        }
        _apCarouselViewDataModelLiveData.value = dataModel
    }

    private fun setStoriesDataModel(
        dataModel: APTemplateDataModel,
        isCached: Boolean = false,
        appViewId: String,
        isEmptyViewId: Boolean = false
    ) {
        id = dataModel.id
        if (!isCached || _storyDataModelLiveData.value == null) {
            sortAndFilterCampaigns(dataModel, true)
            if (!isCached) {
                saveAPTemplateViewDataModelToCache(appViewId, dataModel)
            }
            _storyDataModelLiveData.value = dataModel
        }
    }

    private fun sortAndFilterCampaigns(dataModel: APCarouselViewDataModel, showCount: Boolean) {
        val activeEntries = mutableListOf<APEntryPoint>()
        dataModel.entryPoints.forEach {
            val count = getWatchedBannerCount(it.id)
            it.showCount?.apply {
                if (this >= count) {
                    activeEntries.add(it)
                    if(showCount) {
                        APAnalytics.logEvent(
                            APAnalyticsEvent(
                                name = "shown-banner",
                                campaignId = it.campaignId,
                                apViewId = id,
                                params = mapOf("bannerId" to it.id)
                            )
                        )
                        preferences.saveWatchedBannerIdCount(it.id)
                    }
                }
            } ?: activeEntries.add(it)
        }
        dataModel.entryPoints = activeEntries
    }

    private fun sortAndFilterCampaigns(dataModel: APTemplateDataModel, swapInAdapter: Boolean = false) {
        val notWatched = mutableListOf<Campaign>()
        val watched = mutableListOf<Campaign>()
        dataModel.campaigns.forEachIndexed { index, campaign ->
            campaign.body.story?.let { story ->
                var isWatched = false
                var localShowCount = 0
                getWatchedStorySet()?.forEach { watchedStoryId ->
                    if (story.id == watchedStoryId) {
                        isWatched = true
                        localShowCount = getWatchedStoryCount(watchedStoryId)
                    }
                }
                if (isWatched) {
                    story.showBorder = false
                    if(campaign.showCount!= null && localShowCount<=campaign.showCount){
                        watched.add(campaign)
                        showStoryShownEvent(campaignId = campaign.id, storyId = story.id)
                    }
                } else {
                    notWatched.add(campaign)
                    showStoryShownEvent(campaignId = campaign.id, storyId = story.id)
                }
            }
        }
        val newStoryCampaign = mutableListOf<Campaign>().apply {
            addAll(notWatched)
            addAll(watched)
        }
        if(swapInAdapter){
            dataModel.campaigns = newStoryCampaign
        }
    }

    private fun showStoryShownEvent(campaignId: String?, storyId: String){
        APAnalytics.logEvent(
            APAnalyticsEvent(
                name = "shown-outer-image",
                campaignId = campaignId,
                apViewId = id,
                params = mapOf("storyId" to storyId)
            )
        )
    }

    fun requestAPViewDataModel(apViewId: String, hasDrafts: Boolean) {
        apViewRepository.requestAPView(
            apViewId, hasDrafts, object: RequestResultCallback<APCarouselViewDataModel>() {
                override fun success(response: APCarouselViewDataModel) {
                    if(response.entryPoints.isNullOrEmpty()){
                        requestTemplate(apViewId, hasDrafts)
                    } else {
                        runOnMainThread {
                            setAPCarouselViewDataModel(
                                dataModel = response,
                                appViewId = apViewId,
                                showCount = true
                            )
                        }
                    }
                }

                override fun failure(error: APError?) {
                    runOnMainThread {
                        _apCarouselViewDataModelLiveData.value?.let {
                            setAPCarouselViewDataModel(
                                dataModel = it,
                                appViewId = apViewId,
                                showCount = false
                            )
                        }
                    }
                }
            }
        )
    }

    fun requestTemplate(apViewId: String, hasDrafts: Boolean) {
        apViewRepository.requestTemplate(
            apViewId, hasDrafts, object: RequestResultCallback<APTemplateDataModel>() {
                override fun success(response: APTemplateDataModel) {
                    runOnMainThread {
                        setStoriesDataModel(
                            dataModel = response,
                            isEmptyViewId = apViewId.isEmpty(),
                            appViewId = apViewId
                        )
                    }
                }

                override fun failure(error: APError?) {
                    runOnMainThread {
                        _storyDataModelLiveData.value?.let {
                            setStoriesDataModel(
                                dataModel = it,
                                appViewId = apViewId
                            )
                        }
                    }
                }
            }
        )
    }

    override fun runActions(actions: List<APAction?>) {
        for (action in actions) {
            action?.let {
                runAction(it)
            }
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

    override fun onAPStoriesFinished(campaignId: String?) {
        _apCarouselViewDataModelLiveData.value?.let { dataModel ->
            dataModel.entryPoints.firstOrNull {
                it.campaignId == campaignId
            }?.let { entryPoint ->
                _magnetizeEntryPointEventLiveData.value = Event(entryPoint.id)
            }
        }
        _entryPointViewModelMap.forEach { (_, entryPointViewModel) ->
            entryPointViewModel.reset()
        }
        _storyDataModelLiveData.value?.let { dataModel ->
            dataModel.campaigns.firstOrNull {
                it.id == campaignId
            }?.let { entryPoint ->
                _magnetizeEntryPointEventLiveData.value = Event(entryPoint.id)
            }
            sortAndFilterCampaigns(dataModel)
            dataModel.campaigns.forEach { campaign ->
                getStoriesViewModel(campaign)?.updateStoryShowBorderAndReset(campaign.body.story?.showBorder)
            }
            _storyDataModelLiveData.value = dataModel
        }
    }

    override fun getAutoScrollPeriod(): Long? {
        val autoScroll = _storyDataModelLiveData.value?.options?.autoScroll?.let { it * 1000 }?.toLong()
        if (autoScroll == null || autoScroll <= 0L) {
            return null
        }
        return autoScroll
    }

    override fun showBorder(): Boolean {
        return  _storyDataModelLiveData.value?.options?.showBorder == true
    }

    override fun getAPViewId(): String {
        return id
    }

    @Deprecated(
            message = "Not working. Only for testing purposes.",
            level = DeprecationLevel.WARNING)
    fun loadAPViewMockDataModelFromAssets(apViewId: String) {
        cacheManager.loadAPCarouselViewDataModelFromAssets(apViewId) { dataModel ->
            setAPCarouselViewDataModel(
                dataModel, appViewId = "")
        }
    }

    fun loadAPCarouselViewDataModelFromCache(apViewId: String) {
        cacheManager.loadAPCarouselViewDataModelFromCache(apViewId) { dataModel ->
            if (dataModel != null) {
                setAPCarouselViewDataModel(
                    dataModel = dataModel,
                    isCached = true,
                    appViewId = apViewId,
                    showCount = false
                )
            }
        }
    }

    fun loadAPTemplateViewDataModelFromCache(apViewId: String) {
        cacheManager.loadAPTemplateViewDataModelFromCache(apViewId) { dataModel ->
            if (dataModel != null) {
                setStoriesDataModel(
                    dataModel = dataModel,
                    isCached = true,
                    appViewId = apViewId
                )
            }
        }
    }

    private fun saveAPCarouselViewDataModelToCache(apViewId: String, dataModel: APCarouselViewDataModel) {
        cacheManager.saveAPCarouselViewDataModelToCache(apViewId, dataModel)
    }

    private fun saveAPTemplateViewDataModelToCache(appViewId: String, dataModel: APTemplateDataModel) {
        cacheManager.saveAPTemplateViewDataModelToCache(appViewId, dataModel)
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
                    apViewVMDelegate = this
                )
        }
        return _entryPointViewModelMap[entryPoint.id]
    }

    override fun getStoriesViewModel(campaign: Campaign): CampaignViewModel? {
        if (!_storyViewModelMap.contains(campaign.id)) {
            val campaignLifecycleListener = object: APEntryPointLifecycleListener {
                override fun onReady(isReady: Boolean) { onEntryPointReady(campaign.id, isReady) }
                override fun onComplete() { onEntryPointComplete(campaign.id) }
                override fun onError() {  }
            }

            _storyViewModelMap[campaign.id] =
                CampaignViewModel(
                    campaign = campaign,
                    preferences = preferences,
                    userRepository = userRepository,
                    lifecycleListener = campaignLifecycleListener,
                    apViewVMDelegate = this
                )
        }
        return _storyViewModelMap[campaign.id]
    }

    private fun onEntryPointReady(id: String, isReady: Boolean) {
        _apCarouselViewDataModelLiveData.value?.let { dataModel ->
            val firstVisibleEntryPoint =
                dataModel.entryPoints.getOrNull(_visibleEntryPointsPositionRange.first)

            if (getAutoScrollPeriod() != null && isReady &&
                id == firstVisibleEntryPoint?.id && _resumedEntryPointId == null
            ) {
                resumeEntryPoint(id)
            }
        }

        _storyDataModelLiveData.value?.let { dataModel ->
            val firstVisibleEntryPoint =
                dataModel.campaigns.getOrNull(_visibleEntryPointsPositionRange.first)

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

            _apCarouselViewDataModelLiveData.value?.let { dataModel ->
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

            _storyDataModelLiveData.value?.let { dataModel ->
                if (getAutoScrollPeriod() != null) {
                    val resumedEntryPointPosition =
                        dataModel.campaigns.indexOfFirst {
                            it.id == id
                        }
                    val entryPointToResumePosition =
                        (resumedEntryPointPosition + 1) % dataModel.campaigns.size
                    val entryPointToResume = dataModel.campaigns[entryPointToResumePosition]

                    resumeEntryPoint(entryPointToResume.id)
                }
            }
        }
    }

    private fun pauseEntryPoint(id: String) {
        _apCarouselViewDataModelLiveData.value?.let { dataModel ->
            dataModel.entryPoints.find { it.id == id }?.let { entryPoint ->
                getAPEntryPointViewModel(entryPoint)?.run {
                    if (id == _resumedEntryPointId) {
                        _resumedEntryPointId = null
                    }
                    pause()
                }
            }
        }
        _storyDataModelLiveData.value?.let { dataModel ->
            dataModel.campaigns.find { it.id == id }?.let { entryPoint ->
                getStoriesViewModel(entryPoint)?.run {
                    if (id == _resumedEntryPointId) {
                        _resumedEntryPointId = null
                    }
                    pause()
                }
            }
        }
    }

    private fun resumeEntryPoint(id: String) {
        _apCarouselViewDataModelLiveData.value?.let { dataModel ->
            dataModel.entryPoints.find { it.id == id }?.let { entryPoint ->
                _magnetizeEntryPointEventLiveData.value = Event(id)
                getAPEntryPointViewModel(entryPoint)?.run {
                    _resumedEntryPointId = id
                    resume()
                }
            }
        }
        _storyDataModelLiveData.value?.let { dataModel ->
            dataModel.campaigns.find { it.id == id }?.let { entryPoint ->
                _magnetizeEntryPointEventLiveData.value = Event(id)
                getStoriesViewModel(entryPoint)?.run {
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
        _apCarouselViewDataModelLiveData.value?.let { dataModel ->
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
        _storyDataModelLiveData.value?.let { dataModel ->
            if (getAutoScrollPeriod() != null) {
                val range = _visibleEntryPointsPositionRange
                val resumedEntryPointPosition =
                    dataModel.campaigns.indexOfFirst {
                        it.id == _resumedEntryPointId
                    }

                if (resumedEntryPointPosition !in range) {
                    _resumedEntryPointId?.let { pauseEntryPoint(it) }
                    dataModel.campaigns.getOrNull(range.first)?.id?.let { resumeEntryPoint(it) }
                }
            }
        }
    }

    private fun getWatchedStorySet(): MutableSet<String>? {
        userRepository.getAPUser().externalId?.let {
            return preferences.getWatchedStoryIds(it)
        } ?: return null
    }

    private fun getWatchedStoryCount(storyId: String): Int {
        return preferences.getWatchedStoryCount(storyId)
    }

    private fun getWatchedBannerCount(bannerID: String): Int {
        return preferences.getWatchedBannerCount(bannerID)
    }
}