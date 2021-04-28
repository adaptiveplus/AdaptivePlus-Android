package plus.adaptive.sdk.utils

import plus.adaptive.sdk.core.analytics.APCrashlytics
import kotlin.Exception


internal fun safeRun(
    onExceptionCaught: ((t: Exception) -> Unit)? = null,
    executable: () -> Unit
) {
    try {
        executable.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
        APCrashlytics.logCrash(e)
        onExceptionCaught?.invoke(e)
    }
}