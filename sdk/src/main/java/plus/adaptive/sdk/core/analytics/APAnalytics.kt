package plus.adaptive.sdk.core.analytics

import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.models.network.RequestState
import plus.adaptive.sdk.data.repositories.APAnalyticsRepository
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.utils.getCurrentTimeString
import plus.adaptive.sdk.utils.runDelayedTask


internal class APAnalytics
private constructor(
    private val userRepository: APUserRepository,
    private val analyticsRepository: APAnalyticsRepository
) {

    companion object {
        private const val ERROR_STATE_TIMEOUT = 15000L
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
    private var lastErrorTime: Long = 0L

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
        val currentTime = System.currentTimeMillis()

        if (submitRequestState == RequestState.IN_PROCESS ||
            currentTime - lastErrorTime < ERROR_STATE_TIMEOUT - 50
        ) {
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
                event.apViewId?.let { put("apViewId", it) }
                event.campaignId?.let { put("campaignId", it) }
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
                lastErrorTime = System.currentTimeMillis()
                runCheckTask(isError = true)
            }
        })
    }

    private fun runCheckTask(isError: Boolean = false) {
        if (isError) {
            runDelayedTask(task, ERROR_STATE_TIMEOUT)
        } else {
            runDelayedTask(task, SUBMIT_REQUEST_TIMEOUT)
        }
    }
}