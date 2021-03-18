package com.sprintsquads.adaptiveplus.ui.apview.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.core.providers.provideAPCacheManager
import com.sprintsquads.adaptiveplus.core.providers.provideAPViewRepository


internal class APViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APViewModel::class.java)) {
            return APViewModel(
                provideAPViewRepository(context),
                provideAPCacheManager(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}