package plus.adaptive.sdk.ui.stories.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.core.providers.provideAPSharedPreferences
import plus.adaptive.sdk.core.providers.provideAPUserRepository
import plus.adaptive.sdk.data.models.APStory


internal class APStoryViewModelFactory(
    private val context: Context,
    private val story: APStory,
    private val storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegateProtocol
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APStoryViewModel::class.java)) {
            return APStoryViewModel(
                story,
                storiesDialogViewModelDelegate,
                provideAPSharedPreferences(context),
                provideAPUserRepository(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}