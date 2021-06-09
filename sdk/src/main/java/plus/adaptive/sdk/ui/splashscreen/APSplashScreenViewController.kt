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
import plus.adaptive.sdk.utils.runOnMainThread


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
        getFirstWorkingSplashScreenOnReadiness(
            getFilteredAndSortedSplashScreens(dataModel.splashScreens, hasDrafts),
            onReady = { splashScreen ->
                val apSplashScreenDialog = APSplashScreenDialog
                    .newInstance(
                        splashScreen = splashScreen,
                        options = dataModel.options,
                        apViewId = dataModel.id
                    ).apply {
                        setViewControllerDelegate(
                            object : APSplashScreenViewControllerDelegateProtocol {
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
            },
            onFail = {
                splashScreenListener?.onFinish()
            }
        )
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
            preloadSplashScreenViewContent(dataModel)
        }
    }

    private fun preloadSplashScreenViewContent(dataModel: APSplashScreenViewDataModel) {
        dataModel.splashScreens.forEach { preloadSplashScreen(it) }
    }

    private fun preloadSplashScreen(
        splashScreen: APSplashScreen,
        onReady: (() -> Unit)? = null,
        onFail: (() -> Unit)? = null
    ) {
        var readyLayersCount = 0
        var isFailed = false

        val incrementAndCheckIfAllReady = {
            readyLayersCount++

            if (!isFailed && readyLayersCount == splashScreen.layers.size) {
                onReady?.invoke()
            }
        }

        val failOnce = {
            if (!isFailed) {
                isFailed = true
                onFail?.invoke()
            }
        }

        splashScreen.layers.forEach { apLayer ->
            when (apLayer.component) {
                is APImageComponent -> {
                    apLayer.component.url.let {
                        preloadImage(context, it,
                            onResourceReady = {
                                incrementAndCheckIfAllReady()
                            },
                            onLoadFailed = {
                                failOnce()
                            }
                        )
                    }
                }
                is APGIFComponent -> {
                    apLayer.component.url.let {
                        preloadGIF(context, it,
                            onResourceReady = {
                                incrementAndCheckIfAllReady()
                            },
                            onLoadFailed = {
                                failOnce()
                            }
                        )
                    }
                }
                is APTextComponent -> {
                    apLayer.component.font?.let {
                        preloadAPFont(context, it,
                            onResourceReady = {
                                incrementAndCheckIfAllReady()
                            },
                            onLoadFailed = {
                                incrementAndCheckIfAllReady()
                            }
                        )
                    }
                }
                else -> incrementAndCheckIfAllReady()
            }
        }
    }

    private fun getFilteredAndSortedSplashScreens(
        splashScreens: List<APSplashScreen>,
        hasDrafts: Boolean
    ) : List<APSplashScreen> {
        return userRepository.getAPUserId()?.let { userId ->
            splashScreens.filter { splashScreen ->
                val prefKey = "${userId}_${splashScreen.campaignId}_${CAMPAIGN_WATCHED_COUNT}"
                val watchedCount = maxOf(0, preferences.getInt(prefKey))

                (hasDrafts || splashScreen.status == APSplashScreen.Status.ACTIVE) &&
                    (splashScreen.showCount == null || watchedCount < splashScreen.showCount)
            }.sortedBy { splashScreen ->
                val prefKey = "${userId}_${splashScreen.campaignId}_${CAMPAIGN_WATCHED_COUNT}"
                val watchedCount = maxOf(0, preferences.getInt(prefKey))
                watchedCount
            }
        } ?: listOf()
    }

    private fun getFirstWorkingSplashScreenOnReadiness(
        splashScreens: List<APSplashScreen>,
        startIndex: Int = 0,
        onReady: (splashScreen: APSplashScreen) -> Unit,
        onFail: () -> Unit
    ) {
        splashScreens.getOrNull(startIndex)?.let { splashScreen ->
            runOnMainThread {
                preloadSplashScreen(
                    splashScreen,
                    onReady = {
                        onReady.invoke(splashScreen)
                    },
                    onFail = {
                        getFirstWorkingSplashScreenOnReadiness(
                            splashScreens,
                            startIndex + 1,
                            onReady,
                            onFail
                        )
                    }
                )
            }
        } ?: run {
            onFail.invoke()
        }
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