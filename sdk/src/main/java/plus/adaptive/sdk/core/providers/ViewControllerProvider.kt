package plus.adaptive.sdk.core.providers

import android.content.Context
import plus.adaptive.sdk.ui.launchscreen.APLaunchScreenViewController


internal fun provideAPLaunchScreenViewController(
    context: Context
) : APLaunchScreenViewController {
    return APLaunchScreenViewController(
        context,
        provideAPCacheManager(context),
        provideAPLaunchScreenRepository(context)
    )
}