package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.data.models.APStory


internal class APStoryViewModelFactory(
    private val story: APStory,
    private val storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegate
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APStoryViewModel::class.java)) {
            return APStoryViewModel(story, storiesDialogViewModelDelegate) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}