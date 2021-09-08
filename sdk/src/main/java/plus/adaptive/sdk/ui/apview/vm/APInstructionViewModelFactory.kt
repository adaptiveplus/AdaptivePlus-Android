package plus.adaptive.sdk.ui.apview.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.core.providers.provideAPCacheManager
import plus.adaptive.sdk.core.providers.provideAPSharedPreferences
import plus.adaptive.sdk.core.providers.provideAPUserRepository
import plus.adaptive.sdk.core.providers.provideAPViewRepository


internal class APInstructionViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APInstructionViewModel::class.java)) {
            return APInstructionViewModel(
                provideAPViewRepository(context),
                provideAPUserRepository(context),
                provideAPCacheManager(context),
                provideAPSharedPreferences(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}