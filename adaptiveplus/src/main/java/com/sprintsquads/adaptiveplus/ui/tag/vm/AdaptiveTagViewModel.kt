package com.sprintsquads.adaptiveplus.ui.tag.vm

import android.app.Application
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sprintsquads.adaptiveplus.core.providers.provideAdaptiveTagRepository
import com.sprintsquads.adaptiveplus.data.models.AdaptiveAction
import com.sprintsquads.adaptiveplus.data.models.AdaptiveTagTemplate
import com.sprintsquads.adaptiveplus.data.models.Event
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.utils.*
import com.sprintsquads.adaptiveplus.utils.loadMockTemplateFromAssets
import com.sprintsquads.adaptiveplus.utils.loadTemplateFromCache
import com.sprintsquads.adaptiveplus.utils.removeTemplateFromCache
import java.util.*


internal class AdaptiveTagViewModel(
        application: Application
) : AndroidViewModel(application), AdaptiveTagViewModelDelegate {

    val tagTemplateLiveData: LiveData<AdaptiveTagTemplate?>
        get() = _tagTemplateLiveData
    val actionEventLiveData: LiveData<Event<Pair<AdaptiveAction, String>>>
        get() = _actionEventLiveData

    private val _tagTemplateLiveData = MutableLiveData<AdaptiveTagTemplate?>()
    private val _actionEventLiveData = MutableLiveData<Event<Pair<AdaptiveAction, String>>>()
//    private val _storiesPausesLiveData = MutableLiveData<Int>()

    private var _tagId: String = ""

    private var _template: AdaptiveTagTemplate? = null

    private var repository =
            provideAdaptiveTagRepository(application.applicationContext)


    private fun setTemplate(
            template: AdaptiveTagTemplate?,
            isCached: Boolean = false,
            isForceUpdate: Boolean = false
    ) {
        if (template == null) {
            if (isForceUpdate) {
                _tagTemplateLiveData.value = null
            } else {
                _tagTemplateLiveData.value = _tagTemplateLiveData.value
            }
        }
        else if (isForceUpdate || !isCached || _template == null) {
            if (!isCached) {
                saveTemplateToCache(template.id, template)
            }

            _tagTemplateLiveData.value = template
        }
    }

    fun requestTemplate(tagId: String) {
        // TODO: implement
//        _tagId = tagId
//        val location = AdaptivePlusSDK().getUserLocation()
//
//        repository.requestAdaptiveComponent(
//                tagId, location, object: AdaptivePlusCallback<AdaptiveTemplate>() {
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
        actions: List<AdaptiveAction>,
        campaignId: String
    ) {
//        for (actionId in actions) {
//            (_actions[actionId] ?: _bookmarksTemplate?.actions?.get(actionId))?.let {
//                params["actionId"] = actionId
//                AdaptiveAnalytics.logEvent(
//                        AnalyticsEvent(
//                                name = "action",
//                                campaignId = campaignId,
//                                tagId = _tagId,
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
     * @see AdaptiveAction
     */
    private fun runAction(action: AdaptiveAction, campaignId: String) {
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
    fun loadMockTemplateFromAssets(tagId: String) {
        _tagId = tagId

        getApplication<Application>().applicationContext?.let { ctx ->
            loadMockTemplateFromAssets(
                    ctx = ctx,
                    tagId = tagId
            ) { template ->
                Handler().postDelayed({
                    setTemplate(template)
                }, 2000)
            }
        }
    }

    fun loadTemplateFromCache(tagId: String, isForceUpdate: Boolean = false) {
        _tagId = tagId

        val userId = AdaptivePlusSDK().getUserId() ?: ""

        getApplication<Application>().applicationContext?.let { ctx ->
            loadTemplateFromCache(
                    ctx = ctx,
                    tagId = tagId,
                    userId = userId
            ) { template ->
                if (isForceUpdate || template != null) {
                    setTemplate(
                            template = template,
                            isCached = true,
                            isForceUpdate = isForceUpdate
                    )
                }
            }
        }
    }

    private fun saveTemplateToCache(tagId: String, template: AdaptiveTagTemplate) {
        val userId = AdaptivePlusSDK().getUserId() ?: ""

        getApplication<Application>().applicationContext?.let { ctx ->
            saveTemplateToCache(
                    ctx = ctx,
                    tagId = tagId,
                    userId = userId,
                    template = template
            )
        }
    }

    private fun removeTemplateFromCache(tagId: String) {
        val userId = AdaptivePlusSDK().getUserId() ?: ""

        getApplication<Application>().applicationContext?.let { ctx ->
            removeTemplateFromCache(
                    ctx = ctx,
                    tagId = tagId,
                    userId = userId
            )
        }
    }
}