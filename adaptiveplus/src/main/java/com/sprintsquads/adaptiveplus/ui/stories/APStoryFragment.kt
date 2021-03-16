package com.sprintsquads.adaptiveplus.ui.stories

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEvent
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapStatus
import com.sprintsquads.adaptiveplus.ui.stories.progress.APStoriesProgressView
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoriesViewModel
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoriesViewModelFactory
import kotlinx.android.synthetic.main.ap_fragment_story.*


internal class APStoryFragment :
    Fragment(), APStoriesProgressView.StoriesListener {

    companion object {
        private const val PRE_ANIMATION_DELAY = 5L

        private const val EXTRA_STORY = "extra_story"

        @JvmStatic
        fun newInstance(
            story: APStory,
            controller: APStoriesProgressController? = null
        ) = APStoryFragment().apply {
            arguments = bundleOf(EXTRA_STORY to story)
            this.storiesProgressController = controller
        }
    }


    private var storiesProgressController: APStoriesProgressController? = null

    private lateinit var story: APStory
    private lateinit var snaps: List<APStory.Snap>

    private lateinit var viewModel: APStoriesViewModel

    private var isUnderTouch = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (arguments?.get(EXTRA_STORY) as? APStory)?.let { story ->
            this.story = story
            this.snaps = story.snaps
        } ?: run {
            activity?.finish()
        }

        activity?.let {
            val viewModelFactory = APStoriesViewModelFactory(it)
            viewModel = ViewModelProvider(it, viewModelFactory).get(APStoriesViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apSnapsViewPager.offscreenPageLimit = 1
        apSnapsViewPager.adapter = APSnapsPagerAdapter(
            fragmentManager = childFragmentManager,
            snaps = snaps,
            storyId = story.id
        )

        apStoriesProgressView.setStoriesCount(snaps.size)
        apStoriesProgressView.setStoryDurations(snaps.map { (it.showTime * 1000).toLong() })
        apStoriesProgressView.setStoriesListener(this)

        apCloseButtonImageView.setOnClickListener {
            storiesProgressController?.closeStories()
        }

        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        if (!apStoriesProgressView.hasStarted()) {
            val snapIndex = apSnapsViewPager.currentItem
            apStoriesProgressView.startStories(snapIndex)
        }

        Handler().postDelayed({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    override fun onPause() {
        super.onPause()

        updateStoryProgressState()
    }

    override fun onDestroy() {
        apStoriesProgressView?.destroy()
        super.onDestroy()
    }

    private fun setupObservers() {
        viewModel.snapReadinessUpdatedEventLiveData.observe(
            viewLifecycleOwner, snapReadinessUpdatedEventObserver)
        viewModel.isStoriesExternallyPausedLiveData?.observe(
            viewLifecycleOwner, isStoriesExternallyPausedObserver)
        viewModel.isIdleStateLiveData.observe(
            viewLifecycleOwner, isIdleStateObserver)
        viewModel.snapEventLiveData.observe(
            viewLifecycleOwner, snapEventObserver)
    }

    private val snapReadinessUpdatedEventObserver =
        Observer<String> {
            updateStoryProgressState()
        }

    private val isStoriesExternallyPausedObserver = Observer<Boolean> {
        updateStoryProgressState()
    }

    private val isIdleStateObserver = Observer<Boolean> {
        Handler().postDelayed({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    private val snapEventObserver = Observer<APSnapEvent> { snapEvent ->
        if (snapEvent.event != APSnapEvent.Type.NONE &&
            snaps.any { it.id == snapEvent.snapId }
        ) {
            viewModel.onSnapEvent(snapEvent.snapId, APSnapEvent.Type.NONE)

            when (snapEvent.event) {
                APSnapEvent.Type.IS_UNDER_TOUCH -> {
                    isUnderTouch = true
                    updateStoryProgressState()
                }
                APSnapEvent.Type.IS_NOT_UNDER_TOUCH -> {
                    isUnderTouch = false
                    updateStoryProgressState()
                }
                APSnapEvent.Type.GO_TO_PREV_SNAP -> {
                    goToPrevSnap()
                }
                APSnapEvent.Type.GO_TO_NEXT_SNAP -> {
                    goToNextSnap()
                }
                APSnapEvent.Type.CLOSE_STORIES -> {
                    storiesProgressController?.closeStories()
                }
                APSnapEvent.Type.RESET_AND_PAUSE_SNAP_PROGRESS -> {
                    apStoriesProgressView?.resetCurrentSnap()
                    apStoriesProgressView?.pause()
                }
                else -> { }
            }
        }
    }

    override fun onComplete() {
        resetLastSnap()

        storiesProgressController?.moveToNextStory(story.id)

        Handler().postDelayed({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    override fun onPrev() {
        showPrev()
    }

    override fun onNext() {
        showNext()
    }

    private fun showNext() {
        if (apSnapsViewPager == null) return

        resetLastSnap()

        if (apSnapsViewPager.currentItem < snaps.size - 1) {
            apSnapsViewPager.currentItem++
        }

        Handler().postDelayed({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    private fun showPrev() {
        if (apSnapsViewPager == null) return

        resetLastSnap()

        if (apSnapsViewPager.currentItem == 0) {
            storiesProgressController?.moveToPrevStory(story.id)
        }
        else {
            apSnapsViewPager.currentItem = maxOf(0, apSnapsViewPager.currentItem - 1)
        }

        Handler().postDelayed({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    private fun updateStoryProgressState() {
        if (view == null) return

        val snapIndex = apSnapsViewPager.currentItem
        val snapId = snaps.getOrNull(snapIndex)?.id ?: ""

        if (viewModel.isSnapReady(snapId)) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                && viewModel.isStateIdle()
                && !viewModel.isStoriesExternallyPaused()
                && !isUnderTouch
            ) {
                apStoriesProgressView.resume()

                viewModel.updateStoryProgressState(snapId = snapId, state = APSnapStatus.State.RESUMED)
            }
            else {
                apStoriesProgressView.pause()
                viewModel.updateStoryProgressState(snapId = snapId, state = APSnapStatus.State.PAUSED)
            }
        }
        else {
            apStoriesProgressView.pause()
            viewModel.updateStoryProgressState(snapId = snapId, state = APSnapStatus.State.PAUSED)
        }
    }

    private fun goToNextSnap() {
        apStoriesProgressView?.skip()
    }

    private fun goToPrevSnap() {
        apStoriesProgressView?.reverse()
    }

    private fun resetLastSnap() {
        val snapId = snaps.getOrNull(apSnapsViewPager.currentItem)?.id ?: ""
        viewModel.updateStoryProgressState(snapId = snapId, state = APSnapStatus.State.RESET)
    }

}