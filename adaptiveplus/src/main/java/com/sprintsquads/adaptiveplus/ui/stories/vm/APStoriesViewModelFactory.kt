package com.sprintsquads.adaptiveplus.ui.stories.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.core.providers.provideAPSharedPreferences
import com.sprintsquads.adaptiveplus.core.providers.provideAPStoriesRepository


internal class APStoriesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APStoriesViewModel::class.java)) {
            return APStoriesViewModel(
                provideAPStoriesRepository(context),
                provideAPSharedPreferences(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}