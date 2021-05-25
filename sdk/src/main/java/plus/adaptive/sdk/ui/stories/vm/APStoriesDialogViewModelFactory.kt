package plus.adaptive.sdk.ui.stories.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.ui.apview.vm.APViewVMDelegateProtocol


internal class APStoriesDialogViewModelFactory(
    private val apViewVMDelegate: APViewVMDelegateProtocol
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APStoriesDialogViewModel::class.java)) {
            return APStoriesDialogViewModel(apViewVMDelegate) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}