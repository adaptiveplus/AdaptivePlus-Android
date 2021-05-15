package plus.adaptive.sdk.ui.launchscreen

import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APLaunchScreen
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APLaunchScreenRepository


internal class APLaunchScreenViewController(
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

    private fun showLaunchScreenDialog(dataModel: APLaunchScreen) {
        // TODO: show dialog
    }

    private fun requestAPLaunchScreenModel() {
        launchScreenRepository.requestAPLaunchScreen(
            object: RequestResultCallback<APLaunchScreen>() {
                override fun success(response: APLaunchScreen) {
                    saveAPLaunchScreenModelToCache(response)
                }

                override fun failure(error: APError?) { }
            }
        )
    }

    private fun saveAPLaunchScreenModelToCache(dataModel: APLaunchScreen) {
        cacheManager.saveAPLaunchScreenModelToCache(dataModel)
    }
}