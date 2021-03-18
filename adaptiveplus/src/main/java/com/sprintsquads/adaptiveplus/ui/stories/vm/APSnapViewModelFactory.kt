package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.data.models.APSnap


internal class APSnapViewModelFactory(
    private val snap: APSnap,
    private val storyViewModelDelegate: APStoryViewModelDelegate?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(APSnapViewModel::class.java)) {
            return APSnapViewModel(snap, storyViewModelDelegate) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}