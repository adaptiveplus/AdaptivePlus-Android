package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import com.sprintsquads.adaptiveplus.data.repositories.APAnalyticsRepository
import com.sprintsquads.adaptiveplus.data.repositories.APAuthRepository
import com.sprintsquads.adaptiveplus.data.repositories.APUserRepository
import com.sprintsquads.adaptiveplus.data.repositories.APViewRepository
import com.sprintsquads.adaptiveplus.utils.getUnprocessedAPViewGson


internal fun provideAPAnalyticsRepository(
    context: Context?
) : APAnalyticsRepository {
    return APAnalyticsRepository(
        provideNetworkServiceManager(),
        provideAPClientCredentialsManager(),
        provideAPUserRepository(context)
    )
}

internal fun provideAPAuthRepository(
    context: Context?
) : APAuthRepository {
    return APAuthRepository(
        provideNetworkServiceManager(),
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
        provideNetworkServiceManager(),
        provideAPClientCredentialsManager(),
        provideAPUserRepository(context),
        getUnprocessedAPViewGson()
    )
}