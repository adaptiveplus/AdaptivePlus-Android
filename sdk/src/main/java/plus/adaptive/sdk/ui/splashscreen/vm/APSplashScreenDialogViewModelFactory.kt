package plus.adaptive.sdk.ui.splashscreen.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.core.providers.provideAPSharedPreferences
import plus.adaptive.sdk.core.providers.provideAPUserRepository
import plus.adaptive.sdk.data.models.APSplashScreen


internal class APSplashScreenDialogViewModelFactory(
    private val context: Context?,
    private val splashScreen: APSplashScreen
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APSplashScreenDialogViewModel::class.java)) {
            return APSplashScreenDialogViewModel(
                splashScreen,
                context?.let { provideAPSharedPreferences(it) },
                provideAPUserRepository(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}