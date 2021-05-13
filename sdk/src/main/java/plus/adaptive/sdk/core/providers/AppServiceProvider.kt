package plus.adaptive.sdk.core.providers

import android.content.Context
import plus.adaptive.sdk.core.managers.APActionsManager
import plus.adaptive.sdk.core.managers.APActionsManagerImpl
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.core.managers.APCacheManagerImpl
import plus.adaptive.sdk.core.managers.APAuthCredentialsManager
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.core.managers.NetworkServiceManager
import plus.adaptive.sdk.core.managers.NetworkServiceManagerImpl
import plus.adaptive.sdk.ui.apview.APViewDelegateProtocol
import plus.adaptive.sdk.ui.apview.vm.APViewModelDelegateProtocol


internal fun provideAPSharedPreferences(
    context: Context
) : APSharedPreferences {
    return APSharedPreferences(context)
}

internal fun provideNetworkServiceManager(
    context: Context? = null
) : NetworkServiceManager {
    return NetworkServiceManagerImpl(
        context?.let { provideAPSharedPreferences(context) },
        provideAPUserRepository(context)
    )
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
    return APCacheManagerImpl(context, provideAPUserRepository(context))
}

internal fun provideAPClientCredentialsManager() : APAuthCredentialsManager {
    return APAuthCredentialsManager()
}