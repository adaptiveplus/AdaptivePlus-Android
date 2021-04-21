package plus.adaptive.sdk.ui.stories.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.ui.apview.vm.APViewModelDelegateProtocol


internal class APStoriesDialogViewModelFactory(
    private val apViewModelDelegate: APViewModelDelegateProtocol
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APStoriesDialogViewModel::class.java)) {
            return APStoriesDialogViewModel(apViewModelDelegate) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}