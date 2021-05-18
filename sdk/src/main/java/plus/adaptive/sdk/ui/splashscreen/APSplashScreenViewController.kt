package plus.adaptive.sdk.ui.splashscreen

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APSplashScreenTemplate
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APSplashScreenRepository


internal class APSplashScreenViewController(
    private val context: Context,
    private val cacheManager: APCacheManager,
    private val splashScreenRepository: APSplashScreenRepository
) {

    fun show() {
        cacheManager.loadAPSplashScreenTemplateFromCache { dataModel ->
            if (dataModel != null) {
                showSplashScreenDialog(dataModel)
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
            showSplashScreenDialog(dataModel)
        }
    }

    private fun showSplashScreenDialog(dataModel: APSplashScreenTemplate) {
        try {
            getFragmentActivity()?.run {
                dataModel.splashScreens.firstOrNull()?.let {
                    val apSplashScreenDialog = APSplashScreenDialog.newInstance(
                        dataModel.options.screenWidth, it)
                    apSplashScreenDialog.show(supportFragmentManager, apSplashScreenDialog.tag)
                }
            }
        } catch (e: IllegalStateException) {
            APCrashlytics.logCrash(e)
            e.printStackTrace()
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
}