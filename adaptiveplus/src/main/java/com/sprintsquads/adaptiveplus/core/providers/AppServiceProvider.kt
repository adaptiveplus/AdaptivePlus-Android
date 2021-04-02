package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import com.sprintsquads.adaptiveplus.core.managers.APActionsManager
import com.sprintsquads.adaptiveplus.core.managers.APActionsManagerImpl
import com.sprintsquads.adaptiveplus.core.managers.APCacheManager
import com.sprintsquads.adaptiveplus.core.managers.APCacheManagerImpl
import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManagerImpl
import com.sprintsquads.adaptiveplus.ui.apview.APViewDelegateProtocol
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegateProtocol


internal fun provideAPSharedPreferences(
    context: Context
) : APSharedPreferences {
    return APSharedPreferences(context)
}

internal fun provideNetworkServiceManager(
    context: Context?
) : NetworkServiceManager {
    val preferences = context?.let { provideAPSharedPreferences(it) }
    return NetworkServiceManagerImpl.newInstance(preferences)
}

internal fun provideAPActionsManager(
    apViewDelegate: APViewDelegateProtocol,
    apViewModelDelegate: APViewModelDelegateProtocol
) : APActionsManager {
    return APActionsManagerImpl(apViewDelegate, apViewModelDelegate)
}

internal fun provideAPCacheManager(
    context: Context
) : APCacheManager {
    return APCacheManagerImpl(context)
}