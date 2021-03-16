package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import com.sprintsquads.adaptiveplus.data.repositories.APAnalyticsRepository
import com.sprintsquads.adaptiveplus.data.repositories.APAuthRepository
import com.sprintsquads.adaptiveplus.data.repositories.APStoriesRepository
import com.sprintsquads.adaptiveplus.data.repositories.APViewRepository


internal fun provideAPAnalyticsRepository(
    context: Context?
): APAnalyticsRepository {
    return APAnalyticsRepository(
        provideNetworkServiceManager(context)
    )
}

internal fun provideAPAuthRepository(
    context: Context?
): APAuthRepository {
    return APAuthRepository(
        provideNetworkServiceManager(context)
    )
}

internal fun provideAPViewRepository(
    context: Context?
): APViewRepository {
    return APViewRepository(
        provideNetworkServiceManager(context)
    )
}

internal fun provideAPStoriesRepository(
    context: Context?
): APStoriesRepository {
    return APStoriesRepository(
        provideNetworkServiceManager(context)
    )
}