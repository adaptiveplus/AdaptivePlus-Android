package plus.adaptive.sdk.utils

import android.content.Context


private val testAppIds = listOf("plus.adaptive.qaapp")


internal fun isQAApp(context: Context?) : Boolean {
    return context?.packageName in testAppIds
}