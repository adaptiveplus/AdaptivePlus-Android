package com.sprintsquads.adaptiveplus.ui.apview.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.core.providers.provideAPViewRepository


internal class APViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APViewModel::class.java)) {
            return APViewModel(
                application,
                provideAPViewRepository(application)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}