package com.sprintsquads.adaptiveplus.ui.stories

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.extensions.setTransitionDuration
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegate
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoriesDialogViewModel
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoriesDialogViewModelFactory
import com.sprintsquads.adaptiveplus.utils.restrictToRange
import kotlinx.android.synthetic.main.ap_fragment_ap_stories_dialog.*


internal class APStoriesDialog :
    DialogFragment(), APStoriesProgressController {

    companion object {
        const val EXTRA_STORIES = "extra_stories"
        const val EXTRA_START_INDEX = "extra_start_index"

        @JvmStatic
        fun newInstance(
            stories: List<APStory>,
            startIndex: Int,
            apViewModelDelegate: APViewModelDelegate
        ) = APStoriesDialog().apply {
            arguments = bundleOf(
                EXTRA_STORIES to ArrayList(stories),
                EXTRA_START_INDEX to startIndex
            )

            this.apViewModelDelegate = apViewModelDelegate
        }
    }


    private lateinit var stories: List<APStory>

    private lateinit var viewModel: APStoriesDialogViewModel
    private lateinit var apViewModelDelegate: APViewModelDelegate


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.APStoriesDialogTheme)

        (arguments?.getSerializable(EXTRA_STORIES) as? ArrayList<APStory>)?.let {
            this.stories = it
        }

        if (!::stories.isInitialized || stories.isEmpty()) {
            dismiss()
            return
        }

        val viewModelFactory = APStoriesDialogViewModelFactory(apViewModelDelegate)
        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(APStoriesDialogViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_ap_stories_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startIndex = restrictToRange(
            value = arguments?.getInt(EXTRA_START_INDEX, 0) ?: 0,
            minValue = 0,
            maxValue = stories.size - 1)

        apStoriesViewPager.offscreenPageLimit = 1
        apStoriesViewPager.adapter = APStoriesPagerAdapter(
            childFragmentManager, stories, this, viewModel)
        apStoriesViewPager.currentItem = startIndex
        apStoriesViewPager.setPageTransformer(true, CubePageTransformer())
        apStoriesViewPager.setTransitionDuration(1000)
        apStoriesViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                viewModel.setStateIsIdle(state == ViewPager.SCROLL_STATE_IDLE)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) { }
        })
    }

    override fun moveToNextStory(storyId: String) {
        if (storyId == stories[apStoriesViewPager.currentItem].id) {
            when (apStoriesViewPager.currentItem) {
                stories.size - 1 -> closeStories()
                else -> apStoriesViewPager.currentItem++
            }
        }
    }

    override fun moveToPrevStory(storyId: String) {
        if (storyId == stories[apStoriesViewPager.currentItem].id) {
            if (apStoriesViewPager.currentItem > 0) {
                apStoriesViewPager.currentItem--
            }
        }
    }

    override fun closeStories() {
        dismiss()
        apViewModelDelegate.onAPStoriesDismissed()
    }
}