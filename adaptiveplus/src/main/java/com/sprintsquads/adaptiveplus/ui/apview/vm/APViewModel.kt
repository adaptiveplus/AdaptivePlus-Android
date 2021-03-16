package com.sprintsquads.adaptiveplus.ui.apview.vm

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sprintsquads.adaptiveplus.core.providers.provideAPViewRepository
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.Event
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.utils.*
import com.sprintsquads.adaptiveplus.utils.loadAPViewMockDataModelFromAssets
import com.sprintsquads.adaptiveplus.utils.loadAPViewDataModelFromCache
import com.sprintsquads.adaptiveplus.utils.removeAPViewDataModelFromCache


internal class APViewModel(
        application: Application
) : AndroidViewModel(application), APViewModelDelegate {

    val apViewDataModelLiveData: LiveData<APViewDataModel?>
        get() = _apViewDataModelLiveData
    val actionEventLiveData: LiveData<Event<Pair<APAction, String>>>
        get() = _actionEventLiveData

    private val _apViewDataModelLiveData = MutableLiveData<APViewDataModel?>()
    private val _actionEventLiveData = MutableLiveData<Event<Pair<APAction, String>>>()
//    private val _storiesPausesLiveData = MutableLiveData<Int>()

    private var _apViewId: String = ""
    private var _apViewDataModel: APViewDataModel? = null

    private var repository = provideAPViewRepository(application.applicationContext)


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
        else if (isForceUpdate || !isCached || _apViewDataModel == null) {
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
//        for (actionId in actions) {
//            (_actions[actionId] ?: _bookmarksTemplate?.actions?.get(actionId))?.let {
//                params["actionId"] = actionId
//                AdaptiveAnalytics.logEvent(
//                        AnalyticsEvent(
//                                name = "action",
//                                campaignId = campaignId,
//                                apViewId = _apViewId,
//                                parameters = params
//                        )
//                )
//
//                runAction(it, campaignId)
//            }
//        }
    }

    /**
     * Method to execute adaptive action
     *
     * @param action - a adaptive action to execute
     * @see APAction
     */
    private fun runAction(action: APAction, campaignId: String) {
        _actionEventLiveData.value =
                Event(Pair(action, campaignId))
    }

//    override fun getStoriesPausesLiveData(): LiveData<Int> {
//        return _storiesPausesLiveData
//    }
//
//    override fun pauseStories() {
//        _storiesPausesLiveData.value = _storiesPausesLiveData.value?.inc() ?: 1
//    }
//
//    override fun resumeStories() {
//        _storiesPausesLiveData.value = _storiesPausesLiveData.value?.dec() ?: 0
//    }

    @Deprecated(
            message = "Not working. Only for testing purposes.",
            level = DeprecationLevel.WARNING)
    fun loadAPViewMockDataModelFromAssets(apViewId: String) {
        _apViewId = apViewId

        getApplication<Application>().applicationContext?.let { ctx ->
            loadAPViewMockDataModelFromAssets(
                ctx = ctx,
                apViewId = apViewId
            ) { dataModel ->
                Looper.myLooper()?.let { looper ->
                    Handler(looper).postDelayed({
                        setAPViewDataModel(dataModel)
                    }, 2000)
                }
            }
        }
    }

    fun loadAPViewDataModelFromCache(apViewId: String, isForceUpdate: Boolean = false) {
        _apViewId = apViewId

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
}