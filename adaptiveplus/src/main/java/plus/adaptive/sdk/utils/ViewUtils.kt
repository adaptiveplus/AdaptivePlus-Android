package plus.adaptive.sdk.utils

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo


internal fun isViewVisibleToUser(view: View): Boolean {
    return try {
        val nodeInfo = AccessibilityNodeInfo.obtain()
        view.onInitializeAccessibilityNodeInfo(nodeInfo)

        val result = nodeInfo.isVisibleToUser

        nodeInfo.recycle()

        result
    } catch (e: NullPointerException) {
        e.printStackTrace()
        false
    }
}