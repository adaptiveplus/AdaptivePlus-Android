package com.sprintsquads.adaptiveplus.ui.stories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sprintsquads.adaptiveplus.data.models.APSnap
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoryViewModelDelegate


internal class APSnapsPagerAdapter(
    fragmentManager: FragmentManager,
    private val snaps: List<APSnap>,
    private val storyViewModelDelegate: APStoryViewModelDelegate,
    private var scaleFactor: Float
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment =
        APSnapFragment.newInstance(snaps[position], storyViewModelDelegate, scaleFactor)

    override fun getCount(): Int = snaps.size

    fun updateScaleFactor(scaleFactor: Float) {
        this.scaleFactor = scaleFactor
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}