package plus.adaptive.sdk.ui.launchscreen

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APLaunchScreenTemplate
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APLaunchScreenRepository


internal class APLaunchScreenViewController(
    private val context: Context,
    private val cacheManager: APCacheManager,
    private val launchScreenRepository: APLaunchScreenRepository
) {

    fun show() {
        cacheManager.loadAPLaunchScreenModelFromCache { dataModel ->
            if (dataModel != null) {
                showLaunchScreenDialog(dataModel)
            }
        }

        requestAPLaunchScreenModel()
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING
    )
    fun showMock() {
        cacheManager.loadAPLaunchScreenMockModelFromAssets { dataModel ->
            showLaunchScreenDialog(dataModel)
        }
    }

    private fun showLaunchScreenDialog(dataModel: APLaunchScreenTemplate) {
        try {
            getFragmentActivity()?.run {
                val apLaunchScreenDialog = APLaunchScreenDialog.newInstance(dataModel)
                apLaunchScreenDialog.show(supportFragmentManager, apLaunchScreenDialog.tag)
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

    private fun requestAPLaunchScreenModel() {
        launchScreenRepository.requestAPLaunchScreen(
            object: RequestResultCallback<APLaunchScreenTemplate>() {
                override fun success(response: APLaunchScreenTemplate) {
                    saveAPLaunchScreenModelToCache(response)
                }

                override fun failure(error: APError?) { }
            }
        )
    }

    private fun saveAPLaunchScreenModelToCache(dataModel: APLaunchScreenTemplate) {
        cacheManager.saveAPLaunchScreenModelToCache(dataModel)
    }
}