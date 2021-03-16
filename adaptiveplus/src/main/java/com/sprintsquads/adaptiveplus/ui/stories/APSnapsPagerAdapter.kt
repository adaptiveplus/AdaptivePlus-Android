package com.sprintsquads.adaptiveplus.ui.stories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sprintsquads.adaptiveplus.data.models.APStory


internal class APSnapsPagerAdapter(
    fragmentManager: FragmentManager,
    private val snaps: List<APStory.Snap>,
    private val storyId: String
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
        APSnapFragment.newInstance(snaps[position], storyId = storyId)

    override fun getCount(): Int = snaps.size
}