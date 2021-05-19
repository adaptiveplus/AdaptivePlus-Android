package plus.adaptive.sdk.ui.splashscreen

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.core.managers.APSharedPreferences.Companion.CAMPAIGN_WATCHED_COUNT
import plus.adaptive.sdk.core.providers.provideAPActionsManager
import plus.adaptive.sdk.data.listeners.APSplashScreenListener
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APSplashScreen
import plus.adaptive.sdk.data.models.APSplashScreenTemplate
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.components.APGIFComponent
import plus.adaptive.sdk.data.models.components.APImageComponent
import plus.adaptive.sdk.data.models.components.APTextComponent
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APSplashScreenRepository
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.ui.ViewControllerDelegateProtocol
import plus.adaptive.sdk.utils.preloadAPFont
import plus.adaptive.sdk.utils.preloadGIF
import plus.adaptive.sdk.utils.preloadImage


internal class APSplashScreenViewController(
    private val context: Context,
    private val preferences: APSharedPreferences,
    private val cacheManager: APCacheManager,
    private val userRepository: APUserRepository,
    private val splashScreenRepository: APSplashScreenRepository
) : ViewControllerDelegateProtocol {

    private var splashScreenListener: APSplashScreenListener? = null


    fun show(hasDrafts: Boolean) {
        cacheManager.loadAPSplashScreenTemplateFromCache { dataModel ->
            if (dataModel != null) {
                showSplashScreenDialog(dataModel, hasDrafts)
            } else {
                splashScreenListener?.onFinish()
            }
        }

        requestAPSplashScreenTemplate(hasDrafts)
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING
    )
    fun showMock() {
        cacheManager.loadAPSplashScreenMockTemplateFromAssets { dataModel ->
            if (dataModel != null) {
                showSplashScreenDialog(dataModel, hasDrafts = false)
            } else {
                splashScreenListener?.onFinish()
            }
        }
    }

    private fun showSplashScreenDialog(dataModel: APSplashScreenTemplate, hasDrafts: Boolean) {
        getSplashScreenToShow(dataModel.splashScreens, hasDrafts)?.let { splashScreen ->
            val apSplashScreenDialog = APSplashScreenDialog.newInstance(
                splashScreen,
                dataModel.options,
                object: APSplashScreenDialogListener {
                    override fun onSplashScreenDialogDismissed() {
                        splashScreenListener?.onFinish()
                    }

                    override fun onRunActions(actions: List<APAction>) {
                        actions.forEach {
                            provideAPActionsManager(
                                this@APSplashScreenViewController
                            ).runAction(it)
                        }
                    }
                }
            )
            showDialog(apSplashScreenDialog)
        }
    }

    private fun requestAPSplashScreenTemplate(hasDrafts: Boolean) {
        splashScreenRepository.requestAPSplashScreenTemplate(
            hasDrafts,
            object: RequestResultCallback<APSplashScreenTemplate>() {
                override fun success(response: APSplashScreenTemplate) {
                    saveAPSplashScreenTemplateToCache(response)
                }

                override fun failure(error: APError?) { }
            }
        )
    }

    private fun saveAPSplashScreenTemplateToCache(dataModel: APSplashScreenTemplate) {
        cacheManager.saveAPSplashScreenTemplateToCache(dataModel) {
            preloadSplashScreenContent(dataModel)
        }
    }

    private fun preloadSplashScreenContent(dataModel: APSplashScreenTemplate) {
        dataModel.splashScreens.forEach { splashScreen ->
            splashScreen.layers.forEach { apLayer ->
                when (apLayer.component) {
                    is APImageComponent -> {
                        apLayer.component.url.let { preloadImage(context, it) }
                    }
                    is APGIFComponent -> {
                        apLayer.component.url.let { preloadGIF(context, it) }
                    }
                    is APTextComponent -> {
                        apLayer.component.font?.let { preloadAPFont(context, it) }
                    }
                }
            }
        }
    }

    private fun getSplashScreenToShow(
        splashScreens: List<APSplashScreen>,
        hasDrafts: Boolean
    ) : APSplashScreen? {
        var resIndex = 0

        userRepository.getAPUserId()?.let { userId ->
            var minWatchedCount = -1

            splashScreens.forEachIndexed { index, splashScreen ->
                val prefKey = "${userId}_${splashScreen.campaignId}_${CAMPAIGN_WATCHED_COUNT}"
                val watchedCount = maxOf(0, preferences.getInt(prefKey))

                if ((hasDrafts || splashScreen.status == APSplashScreen.Status.ACTIVE) &&
                    (splashScreen.showCount == null || watchedCount < splashScreen.showCount) &&
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

    override fun startActivity(intent: Intent) {
        context.startActivity(intent)
    }

    override fun showDialog(dialogFragment: DialogFragment) {
        try {
            getFragmentActivity()?.run {
                dialogFragment.show(supportFragmentManager, dialogFragment.tag)
            }
        } catch (e: Exception) {
            APCrashlytics.logCrash(e)
            e.printStackTrace()

            if (dialogFragment is APSplashScreenDialog) {
                splashScreenListener?.onFinish()
            }
        }
    }

    override fun dismissAllDialogs() { }

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
}