package plus.adaptive.sdk.core.providers

import android.content.Context
import plus.adaptive.sdk.data.repositories.APAnalyticsRepository
import plus.adaptive.sdk.data.repositories.APAuthRepository
import plus.adaptive.sdk.data.repositories.APCrashlyticsRepository
import plus.adaptive.sdk.data.repositories.APSplashScreenRepository
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.data.repositories.APViewRepository
import plus.adaptive.sdk.utils.getUnprocessedAPSplashScreenGson
import plus.adaptive.sdk.utils.getUnprocessedAPViewGson


internal fun provideAPAnalyticsRepository(
    context: Context?
) : APAnalyticsRepository {
    return APAnalyticsRepository(
        provideNetworkServiceManager(context),
        provideAPAuthCredentialsManager(),
        provideAPUserRepository(context)
    )
}

internal fun provideAPAuthRepository(
    context: Context?
) : APAuthRepository {
    return APAuthRepository(
        provideNetworkServiceManager(context),
        provideAPAuthCredentialsManager(),
        provideAPUserRepository(context)
    )
}

internal fun provideAPUserRepository(
    context: Context?
) : APUserRepository {
    return APUserRepository(
        context?.let { provideAPSharedPreferences(it) },
        provideAPAuthCredentialsManager()
    )
}

internal fun provideAPViewRepository(
    context: Context?
) : APViewRepository {
    return APViewRepository(
        provideNetworkServiceManager(context),
        provideAPAuthCredentialsManager(),
        provideAPUserRepository(context),
        getUnprocessedAPViewGson()
    )
}

internal fun provideAPCrashlyticsRepository() : APCrashlyticsRepository {
    return APCrashlyticsRepository(
        provideNetworkServiceManager(),
        provideAPAuthCredentialsManager(),
        provideAPUserRepository(null)
    )
}

internal fun provideAPSplashScreenRepository(
    context: Context?
) : APSplashScreenRepository {
    return APSplashScreenRepository(
        provideNetworkServiceManager(context),
        provideAPAuthCredentialsManager(),
        provideAPUserRepository(context),
        getUnprocessedAPSplashScreenGson()
    )
}