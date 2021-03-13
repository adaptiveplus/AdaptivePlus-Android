package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import com.sprintsquads.adaptiveplus.core.managers.AdaptiveSharedPreferences
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManagerImpl


internal fun provideAdaptiveSharedPreferences(
    context: Context
): AdaptiveSharedPreferences {
    return AdaptiveSharedPreferences(context)
}

internal fun provideNetworkServiceManager(
    context: Context?
): NetworkServiceManager {
    val preferences = context?.let { provideAdaptiveSharedPreferences(it) }
    return NetworkServiceManagerImpl.newInstance(preferences)
}