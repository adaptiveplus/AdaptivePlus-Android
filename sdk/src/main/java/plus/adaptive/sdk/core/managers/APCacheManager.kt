package plus.adaptive.sdk.core.managers

import plus.adaptive.sdk.data.models.APLaunchScreen
import plus.adaptive.sdk.data.models.APViewDataModel


internal interface APCacheManager {
    @Deprecated(
        message = "Only for development purposes.",
        level = DeprecationLevel.WARNING)
    fun loadAPViewMockDataModelFromAssets(
        apViewId: String,
        onSuccess: (dataModel: APViewDataModel) -> Unit
    )

    fun loadAPViewDataModelFromCache(
        apViewId: String,
        onResult: (dataModel: APViewDataModel?) -> Unit
    )

    fun saveAPViewDataModelToCache(
        apViewId: String,
        dataModel: APViewDataModel
    )

    fun removeAPViewDataModelFromCache(
        apViewId: String
    )

    @Deprecated(
        message = "Only for development purposes.",
        level = DeprecationLevel.WARNING)
    fun loadAPLaunchScreenMockModelFromAssets(
        onSuccess: (dataModel: APLaunchScreen) -> Unit
    )

    fun loadAPLaunchScreenModelFromCache(
        onResult: (dataModel: APLaunchScreen?) -> Unit
    )

    fun saveAPLaunchScreenModelToCache(
        dataModel: APLaunchScreen
    )

    fun removeAPLaunchScreenModelFromCache()
}