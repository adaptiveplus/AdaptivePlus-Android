package com.sprintsquads.adaptiveplus.ui.stories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoriesDialogViewModelDelegateProtocol


internal class APStoriesPagerAdapter(
    fragmentManager: FragmentManager,
    private val stories: List<APStory>,
    private val controller: APStoriesProgressController,
    private val storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegateProtocol
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
        APStoryFragment.newInstance(stories[position], controller, storiesDialogViewModelDelegate)

    override fun getCount(): Int = stories.size
}