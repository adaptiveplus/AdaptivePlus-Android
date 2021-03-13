package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import com.sprintsquads.adaptiveplus.data.repositories.AdaptiveAnalyticsRepository
import com.sprintsquads.adaptiveplus.data.repositories.AdaptiveAuthRepository
import com.sprintsquads.adaptiveplus.data.repositories.AdaptiveStoriesRepository
import com.sprintsquads.adaptiveplus.data.repositories.AdaptiveTagRepository


internal fun provideAdaptiveAnalyticsRepository(
    context: Context?
): AdaptiveAnalyticsRepository {
    return AdaptiveAnalyticsRepository(
        provideNetworkServiceManager(context)
    )
}

internal fun provideAdaptiveAuthRepository(
    context: Context?
): AdaptiveAuthRepository {
    return AdaptiveAuthRepository(
        provideNetworkServiceManager(context)
    )
}

internal fun provideAdaptiveTagRepository(
    context: Context?
): AdaptiveTagRepository {
    return AdaptiveTagRepository(
        provideNetworkServiceManager(context)
    )
}

internal fun provideAdaptiveStoriesRepository(
    context: Context?
): AdaptiveStoriesRepository {
    return AdaptiveStoriesRepository(
        provideNetworkServiceManager(context)
    )
}