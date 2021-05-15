package plus.adaptive.sdk.ui.launchscreen.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


internal class APLaunchScreenDialogViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APLaunchScreenDialogViewModel::class.java)) {
            return APLaunchScreenDialogViewModel() as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}