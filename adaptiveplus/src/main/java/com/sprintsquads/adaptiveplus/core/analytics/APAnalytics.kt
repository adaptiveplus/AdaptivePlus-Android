package com.sprintsquads.adaptiveplus.core.analytics

import com.sprintsquads.adaptiveplus.data.models.APAnalyticsEvent
import com.sprintsquads.adaptiveplus.data.models.APError
import com.sprintsquads.adaptiveplus.data.models.network.RequestResultCallback
import com.sprintsquads.adaptiveplus.data.models.network.RequestState
import com.sprintsquads.adaptiveplus.data.repositories.APAnalyticsRepository
import com.sprintsquads.adaptiveplus.data.repositories.APUserRepository
import com.sprintsquads.adaptiveplus.utils.getCurrentTimeString
import com.sprintsquads.adaptiveplus.utils.runDelayedTask


internal class APAnalytics
private constructor(
    private val userRepository: APUserRepository,
    private val analyticsRepository: APAnalyticsRepository
) {

    companion object {
        private var SUBMIT_REQUEST_TIMEOUT = 120000L
        private var EVENTS_SUBMIT_COUNT = 25

        private var analyticsInstance: APAnalytics? = null


        fun reset() {
            SUBMIT_REQUEST_TIMEOUT = 120000L
            EVENTS_SUBMIT_COUNT = 25
            analyticsInstance = null
        }

        fun init(userRepository: APUserRepository, analyticsRepository: APAnalyticsRepository) {
            analyticsInstance = APAnalytics(userRepository, analyticsRepository)
        }

        fun logEvent(event: APAnalyticsEvent) {
            analyticsInstance?.logEvent(event)
        }

        fun updateConfig(timeout: Long?, eventCount: Int?) {
            timeout?.let { SUBMIT_REQUEST_TIMEOUT = it }
            eventCount?.let { EVENTS_SUBMIT_COUNT = it }
            analyticsInstance?.runCheckTask()
        }
    }


    private var submitRequestState = RequestState.NONE
    private var lastSubmitTime: Long = 0L

    private val eventBuffer = mutableListOf<APAnalyticsEvent>()

    private val task = {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastSubmitTime > SUBMIT_REQUEST_TIMEOUT - 200) {
            submitEvents()
        }
    }


    init {
        lastSubmitTime = System.currentTimeMillis()
        runCheckTask()
    }

    private fun logEvent(event: APAnalyticsEvent) {
        if (userRepository.getIsEventTrackingDisabled()) {
            return
        }

        event.createdAt = getCurrentTimeString()
        eventBuffer.add(event)

        if (eventBuffer.size >= EVENTS_SUBMIT_COUNT) {
            submitEvents()
        }
    }

    private fun submitEvents() {
        if (submitRequestState == RequestState.IN_PROCESS) {
            return
        }

        if (eventBuffer.isEmpty()) {
            runCheckTask()
            return
        }

        submitRequestState = RequestState.IN_PROCESS

        val events = eventBuffer.map { event ->
            event.params.toMutableMap().apply {
                put("eventName", event.name)
                put("campaignId", event.campaignId)
                put("apViewId", event.apViewId)
                event.createdAt?.let { put("createdAt", it) }
            }
        }

        analyticsRepository.submitAnalytics(events, object: RequestResultCallback<Any?>() {
            override fun success(response: Any?) {
                submitRequestState = RequestState.SUCCESS
                eventBuffer.clear()
                lastSubmitTime = System.currentTimeMillis()
                runCheckTask()
            }

            override fun failure(error: APError?) {
                submitRequestState = RequestState.ERROR
                runCheckTask()
            }
        })
    }

    private fun runCheckTask() {
        runDelayedTask(task, SUBMIT_REQUEST_TIMEOUT)
    }
}