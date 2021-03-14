package com.sprintsquads.adaptiveplus.ui.tag.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


internal class AdaptiveTagViewModelFactory(
        private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdaptiveTagViewModel::class.java)) {
            return AdaptiveTagViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}