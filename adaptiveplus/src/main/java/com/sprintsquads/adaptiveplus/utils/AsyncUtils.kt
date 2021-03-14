package com.sprintsquads.adaptiveplus.utils

import android.os.Handler
import android.os.Looper


internal fun runOnMainThread(task: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        task.invoke()
    }
}

internal fun runDelayedTask(task: () -> Unit, delay: Long) {
    if (Looper.myLooper() == null) {
        Looper.prepare()
    }
    Looper.myLooper()?.let { looper ->
        Handler(looper).postDelayed({ task.invoke() }, delay)
    }
}