package plus.adaptive.sdk.core.providers

import android.content.Context
import plus.adaptive.sdk.ui.splashscreen.APSplashScreenViewController


internal fun provideAPSplashScreenViewController(
    context: Context
) : APSplashScreenViewController {
    return APSplashScreenViewController(
        context,
        provideAPCacheManager(context),
        provideAPSplashScreenRepository(context)
    )
}