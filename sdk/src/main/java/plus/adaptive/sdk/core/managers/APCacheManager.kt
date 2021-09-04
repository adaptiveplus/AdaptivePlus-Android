package plus.adaptive.sdk.core.managers

import plus.adaptive.sdk.data.models.APSplashScreenViewDataModel
import plus.adaptive.sdk.data.models.APCarouselViewDataModel
import plus.adaptive.sdk.data.models.story.APTemplateDataModel


internal interface APCacheManager {
    @Deprecated(
        message = "Only for development purposes.",
        level = DeprecationLevel.WARNING)
    fun loadAPCarouselViewDataModelFromAssets(
        apViewId: String,
        onSuccess: (dataModel: APCarouselViewDataModel) -> Unit
    )

    fun loadAPCarouselViewDataModelFromCache(
        apViewId: String,
        onResult: (dataModel: APCarouselViewDataModel?) -> Unit
    )

    fun loadAPTemplateViewDataModelFromCache(
        apViewId: String,
        onResult: (dataModel: APTemplateDataModel?) -> Unit
    )

    fun saveAPCarouselViewDataModelToCache(
        apViewId: String,
        dataModel: APCarouselViewDataModel
    )

    fun saveAPTemplateViewDataModelToCache(
        apViewId: String,
        dataModel: APTemplateDataModel
    )

    fun removeAPCarouselViewDataModelFromCache(
        apViewId: String
    )

    @Deprecated(
        message = "Only for development purposes.",
        level = DeprecationLevel.WARNING)
    fun loadAPSplashScreenViewDataModelFromAssets(
        onResult: (dataModel: APSplashScreenViewDataModel?) -> Unit
    )

    fun loadAPSplashScreenViewDataModelFromCache(
        onResult: (dataModel: APSplashScreenViewDataModel?) -> Unit
    )

    fun saveAPSplashScreenViewDataModelToCache(
        dataModel: APSplashScreenViewDataModel,
        onSuccess: (() -> Unit)? = null
    )

    fun removeAPSplashScreenViewDataModelFromCache()
}