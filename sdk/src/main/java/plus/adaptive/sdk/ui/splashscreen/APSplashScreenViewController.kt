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
import plus.adaptive.sdk.data.models.APSplashScreenViewDataModel
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.components.APGIFComponent
import plus.adaptive.sdk.data.models.components.APImageComponent
import plus.adaptive.sdk.data.models.components.APTextComponent
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APSplashScreenRepository
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.ui.ViewControllerDelegateProtocol
import plus.adaptive.sdk.ui.dialogs.APDialogFragment
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

    private var activeDialogCount = 0
    private var splashScreenListener: APSplashScreenListener? = null


    fun show(hasDrafts: Boolean) {
        cacheManager.loadAPSplashScreenViewDataModelFromCache { dataModel ->
            if (dataModel != null) {
                showSplashScreenDialog(dataModel, hasDrafts)
                requestAPSplashScreenViewDataModel(hasDrafts)
            } else {
                requestAPSplashScreenViewDataModel(hasDrafts, showOnResponse = true)
            }
        }
    }

    @Deprecated(
        message = "Only for testing purposes.",
        level = DeprecationLevel.WARNING
    )
    fun showMock() {
        cacheManager.loadAPSplashScreenViewDataModelFromAssets { dataModel ->
            if (dataModel != null) {
                showSplashScreenDialog(dataModel, hasDrafts = false)
            } else {
                splashScreenListener?.onFinish()
            }
        }
    }

    private fun showSplashScreenDialog(
        dataModel: APSplashScreenViewDataModel,
        hasDrafts: Boolean
    ) {
        getSplashScreenToShow(dataModel.splashScreens, hasDrafts)?.let { splashScreen ->
            val apSplashScreenDialog = APSplashScreenDialog
                .newInstance(
                    splashScreen = splashScreen,
                    options = dataModel.options,
                    apViewId = dataModel.id
                ).apply {
                    setViewControllerDelegate(
                        object: APSplashScreenViewControllerDelegateProtocol {
                            override fun runActions(actions: List<APAction>) {
                                provideAPActionsManager(
                                    this@APSplashScreenViewController
                                ).apply {
                                    setAPCustomActionListener { params ->
                                        splashScreenListener?.onRunAPCustomAction(params)
                                    }
                                    actions.forEach { action ->
                                        runAction(action)
                                    }
                                }
                            }
                        }
                    )
                }
            showDialog(apSplashScreenDialog)
        } ?: run {
            splashScreenListener?.onFinish()
        }
    }

    private fun requestAPSplashScreenViewDataModel(
        hasDrafts: Boolean,
        showOnResponse: Boolean = false
    ) {
        splashScreenRepository.requestAPSplashScreenViewDataModel(
            hasDrafts,
            object: RequestResultCallback<APSplashScreenViewDataModel>() {
                override fun success(response: APSplashScreenViewDataModel) {
                    saveAPSplashScreenViewDataModelToCache(response)

                    if (showOnResponse) {
                        showSplashScreenDialog(response, hasDrafts)
                    }
                }

                override fun failure(error: APError?) {
                    if (showOnResponse) {
                        splashScreenListener?.onFinish()
                    }
                }
            }
        )
    }

    private fun saveAPSplashScreenViewDataModelToCache(dataModel: APSplashScreenViewDataModel) {
        cacheManager.saveAPSplashScreenViewDataModelToCache(dataModel) {
            preloadSplashScreenContent(dataModel)
        }
    }

    private fun preloadSplashScreenContent(dataModel: APSplashScreenViewDataModel) {
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
                if (dialogFragment is APDialogFragment) {
                    increaseActiveDialogCount()
                    dialogFragment.addOnDismissListener {
                        decreaseActiveDialogCount()
                    }
                }

                dialogFragment.show(supportFragmentManager, dialogFragment.tag)
            }
        } catch (e: Exception) {
            APCrashlytics.logCrash(e)
            e.printStackTrace()

            if (dialogFragment is APDialogFragment) {
                decreaseActiveDialogCount()
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

    private fun increaseActiveDialogCount() {
        activeDialogCount++
    }

    private fun decreaseActiveDialogCount() {
        activeDialogCount--

        if (activeDialogCount <= 0) {
            splashScreenListener?.onFinish()
            splashScreenListener = null
        }
    }
}