package plus.adaptive.sdk.ui.splashscreen

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.core.managers.APSharedPreferences.Companion.CAMPAIGN_WATCHED_COUNT
import plus.adaptive.sdk.data.listeners.APSplashScreenListener
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APSplashScreen
import plus.adaptive.sdk.data.models.APSplashScreenTemplate
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APSplashScreenRepository
import plus.adaptive.sdk.data.repositories.APUserRepository


internal class APSplashScreenViewController(
    private val context: Context,
    private val cacheManager: APCacheManager,
    private val preferences: APSharedPreferences,
    private val userRepository: APUserRepository,
    private val splashScreenRepository: APSplashScreenRepository
) {

    private var splashScreenListener: APSplashScreenListener? = null


    fun show() {
        cacheManager.loadAPSplashScreenTemplateFromCache { dataModel ->
            if (dataModel != null) {
                showSplashScreenDialog(dataModel)
            } else {
                splashScreenListener?.onFinish()
            }
        }

        requestAPSplashScreenTemplate()
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING
    )
    fun showMock() {
        cacheManager.loadAPSplashScreenMockTemplateFromAssets { dataModel ->
            if (dataModel != null) {
                showSplashScreenDialog(dataModel)
            } else {
                splashScreenListener?.onFinish()
            }
        }
    }

    private fun showSplashScreenDialog(dataModel: APSplashScreenTemplate) {
        try {
            getFragmentActivity()?.run {
                getSplashScreenToShow(dataModel.splashScreens)?.let { splashScreen ->
                    val apSplashScreenDialog = APSplashScreenDialog.newInstance(
                        dataModel.options.screenWidth,
                        splashScreen,
                        object: APSplashScreenDialogListener {
                            override fun onDismiss() {
                                splashScreenListener?.onFinish()
                            }
                        }
                    )
                    apSplashScreenDialog.show(supportFragmentManager, apSplashScreenDialog.tag)
                }
            }
        } catch (e: IllegalStateException) {
            APCrashlytics.logCrash(e)
            e.printStackTrace()
            splashScreenListener?.onFinish()
        }
    }

    private fun getFragmentActivity(): FragmentActivity? {
        var context = context

        while (context is ContextWrapper) {
            if (context is FragmentActivity) {
                return context
            }
            context = context.baseContext
        }

        return null
    }

    private fun requestAPSplashScreenTemplate() {
        splashScreenRepository.requestAPSplashScreenTemplate(
            object: RequestResultCallback<APSplashScreenTemplate>() {
                override fun success(response: APSplashScreenTemplate) {
                    saveAPSplashScreenTemplateToCache(response)
                }

                override fun failure(error: APError?) { }
            }
        )
    }

    private fun saveAPSplashScreenTemplateToCache(dataModel: APSplashScreenTemplate) {
        cacheManager.saveAPSplashScreenTemplateToCache(dataModel)
    }

    private fun getSplashScreenToShow(splashScreens: List<APSplashScreen>) : APSplashScreen? {
        var resIndex = 0

        userRepository.getAPUserId()?.let { userId ->
            var minWatchedCount = -1

            splashScreens.forEachIndexed { index, splashScreen ->
                val prefKey = "${userId}_${splashScreen.campaignId}_${CAMPAIGN_WATCHED_COUNT}"
                val watchedCount = maxOf(0, preferences.getInt(prefKey))

                if ((splashScreen.showCount == null || watchedCount < splashScreen.showCount) &&
                    (minWatchedCount == -1 || watchedCount < minWatchedCount)
                ) {
                    minWatchedCount = watchedCount
                    resIndex = index
                }
            }
        }

        return splashScreens.getOrNull(resIndex)
    }

    fun setSplashScreenListener(listener: APSplashScreenListener?) {
        this.splashScreenListener = listener
    }
}