package com.sprintsquads.adaptiveplus.core.providers

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.core.managers.APActionsManager
import com.sprintsquads.adaptiveplus.core.managers.APActionsManagerImpl
import com.sprintsquads.adaptiveplus.core.managers.APSharedPreferences
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManager
import com.sprintsquads.adaptiveplus.core.managers.NetworkServiceManagerImpl
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegate


internal fun provideAdaptiveSharedPreferences(
    context: Context
) : APSharedPreferences {
    return APSharedPreferences(context)
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
    apViewModelDelegate: APViewModelDelegate
) : APActionsManager {
    return APActionsManagerImpl(fragmentActivity, fragmentManager, apViewModelDelegate)
}