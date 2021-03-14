package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.core.managers.*
import com.sprintsquads.adaptiveplus.core.managers.AdaptiveActionsManager
import com.sprintsquads.adaptiveplus.core.managers.AdaptiveActionsManagerImpl
import com.sprintsquads.adaptiveplus.core.managers.AdaptiveSharedPreferences
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManagerImpl
import com.sprintsquads.adaptiveplus.ui.tag.vm.AdaptiveTagViewModelDelegate


internal fun provideAdaptiveSharedPreferences(
    context: Context
) : AdaptiveSharedPreferences {
    return AdaptiveSharedPreferences(context)
}

internal fun provideNetworkServiceManager(
    context: Context?
) : NetworkServiceManager {
    val preferences = context?.let { provideAdaptiveSharedPreferences(it) }
    return NetworkServiceManagerImpl.newInstance(preferences)
}

internal fun provideAdaptiveActionsManager(
    fragmentActivity: FragmentActivity,
    fragmentManager: FragmentManager,
    tagViewModelDelegate: AdaptiveTagViewModelDelegate
) : AdaptiveActionsManager {
    return AdaptiveActionsManagerImpl(fragmentActivity, fragmentManager, tagViewModelDelegate)
}