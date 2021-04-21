package plus.adaptive.sdk.ui.stories

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import plus.adaptive.sdk.data.models.APSnap
import plus.adaptive.sdk.ui.stories.vm.APStoryViewModelDelegateProtocol


internal class APSnapsPagerAdapter(
    fragmentManager: FragmentManager,
    private val snaps: List<APSnap>,
    private val storyViewModelDelegate: APStoryViewModelDelegateProtocol,
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