package plus.adaptive.sdk.ui.splashscreen.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.data.models.APSplashScreen


internal class APSplashScreenDialogViewModelFactory(
    private val splashScreen: APSplashScreen
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APSplashScreenDialogViewModel::class.java)) {
            return APSplashScreenDialogViewModel(splashScreen) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}