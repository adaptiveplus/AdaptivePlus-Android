package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import com.sprintsquads.adaptiveplus.data.repositories.APAnalyticsRepository
import com.sprintsquads.adaptiveplus.data.repositories.APAuthRepository
import com.sprintsquads.adaptiveplus.data.repositories.APUserRepository
import com.sprintsquads.adaptiveplus.data.repositories.APViewRepository


internal fun provideAPAnalyticsRepository(
    context: Context?
) : APAnalyticsRepository {
    return APAnalyticsRepository(
        provideNetworkServiceManager(context)
    )
}

internal fun provideAPAuthRepository(
    context: Context?
) : APAuthRepository {
    return APAuthRepository(
        provideAPClientCredentialsManager(),
        provideAPUserRepository(context),
        provideNetworkServiceManager(context)
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
        provideNetworkServiceManager(context)
    )
}