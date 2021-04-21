package plus.adaptive.sdk.ui.stories.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.data.models.APSnap


internal class APSnapViewModelFactory(
    private val snap: APSnap,
    private val storyViewModelDelegate: APStoryViewModelDelegateProtocol?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APSnapViewModel::class.java)) {
            return APSnapViewModel(snap, storyViewModelDelegate) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}