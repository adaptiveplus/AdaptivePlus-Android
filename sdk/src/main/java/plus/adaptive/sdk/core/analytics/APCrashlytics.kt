package plus.adaptive.sdk.core.analytics

import plus.adaptive.sdk.core.providers.provideAPCrashlyticsRepository
import plus.adaptive.sdk.data.models.APLog
import plus.adaptive.sdk.data.repositories.APCrashlyticsRepository
import plus.adaptive.sdk.utils.getCurrentTimeString


internal class APCrashlytics
private constructor(
    private val crashlyticsRepository: APCrashlyticsRepository
) {

    companion object {
        private var crashlyticsInstance: APCrashlytics? = null

        private fun instance() : APCrashlytics? {
            if (crashlyticsInstance == null) {
                crashlyticsInstance = APCrashlytics(provideAPCrashlyticsRepository())
            }
            return crashlyticsInstance
        }

        fun logCrash(t: Throwable) {
            instance()?.logCrash(t)
        }
    }

    private fun logCrash(t: Throwable) {
        val log = APLog(
            action = t.stackTrace.getOrNull(0)?.toString() ?: t.toString(),
            message = t.message ?: t.localizedMessage ?: "",
            code = -1,
            type = APLog.Type.FATAL,
            data = mapOf("stacktrace" to t.stackTrace),
            createdAt = getCurrentTimeString()
        )
        submitLog(log)
    }

    private fun submitLog(log: APLog) {
        crashlyticsRepository.submitLog(log)
    }
}