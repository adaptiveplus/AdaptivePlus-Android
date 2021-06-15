package plus.adaptive.sdk.ui.stories.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.core.providers.provideAPSharedPreferences
import plus.adaptive.sdk.core.providers.provideAPUserRepository
import plus.adaptive.sdk.data.models.APSnap


internal class APSnapViewModelFactory(
    private val snap: APSnap,
    private val storyViewModelDelegate: APStoryViewModelDelegateProtocol?,
    private val context: Context?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APSnapViewModel::class.java)) {
            return APSnapViewModel(
                snap,
                storyViewModelDelegate,
                context?.let { provideAPSharedPreferences(it) },
                provideAPUserRepository(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}