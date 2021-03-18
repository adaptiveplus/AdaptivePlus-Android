package com.sprintsquads.adaptiveplus.ui.stories.vm

import androidx.lifecycle.ViewModel
import com.sprintsquads.adaptiveplus.data.models.APSnap
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEvent
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEventInfo


internal class APSnapViewModel(
    private val snap: APSnap,
    private val storyViewModelDelegate: APStoryViewModelDelegate?
) : ViewModel(), APComponentViewModelProvider {

    override fun getAPComponentViewModel(index: Int): APComponentViewModel? {
        // TODO("Not yet implemented")
        return null
    }

    fun onSnapEvent(event: APSnapEvent) {
        storyViewModelDelegate?.onSnapEvent(
            APSnapEventInfo(snap.id, event)
        )
    }
}