package plus.adaptive.sdk.ui.launchscreen.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.data.models.APLaunchScreen


internal class APLaunchScreenDialogViewModelFactory(
    private val launchScreen: APLaunchScreen
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APLaunchScreenDialogViewModel::class.java)) {
            return APLaunchScreenDialogViewModel(launchScreen) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}