package plus.adaptive.sdk.core.providers

import android.content.Context
import plus.adaptive.sdk.data.repositories.APAnalyticsRepository
import plus.adaptive.sdk.data.repositories.APAuthRepository
import plus.adaptive.sdk.data.repositories.APCrashlyticsRepository
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.data.repositories.APViewRepository
import plus.adaptive.sdk.utils.getUnprocessedAPViewGson


internal fun provideAPAnalyticsRepository(
    context: Context?
) : APAnalyticsRepository {
    return APAnalyticsRepository(
        provideNetworkServiceManager(context),
        provideAPClientCredentialsManager(),
        provideAPUserRepository(context)
    )
}

internal fun provideAPAuthRepository(
    context: Context?
) : APAuthRepository {
    return APAuthRepository(
        provideNetworkServiceManager(context),
        provideAPClientCredentialsManager(),
        provideAPUserRepository(context)
    )
}

internal fun provideAPUserRepository(
    context: Context?
) : APUserRepository {
    return APUserRepository(
        context?.let { provideAPSharedPreferences(it) }
    )
}

internal fun provideAPViewRepository(
    context: Context?
) : APViewRepository {
    return APViewRepository(
        provideNetworkServiceManager(context),
        provideAPClientCredentialsManager(),
        provideAPUserRepository(context),
        getUnprocessedAPViewGson()
    )
}

internal fun provideAPCrashlyticsRepository() : APCrashlyticsRepository {
    return APCrashlyticsRepository(
        provideNetworkServiceManager(),
        provideAPClientCredentialsManager(),
        provideAPUserRepository(null)
    )
}