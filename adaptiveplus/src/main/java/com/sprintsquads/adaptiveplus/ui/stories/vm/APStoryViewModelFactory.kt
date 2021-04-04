package com.sprintsquads.adaptiveplus.ui.stories.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.core.providers.provideAPSharedPreferences
import com.sprintsquads.adaptiveplus.core.providers.provideAPUserRepository
import com.sprintsquads.adaptiveplus.data.models.APStory


internal class APStoryViewModelFactory(
    private val context: Context,
    private val story: APStory,
    private val storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegateProtocol
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APStoryViewModel::class.java)) {
            return APStoryViewModel(
                story,
                storiesDialogViewModelDelegate,
                provideAPSharedPreferences(context),
                provideAPUserRepository(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}