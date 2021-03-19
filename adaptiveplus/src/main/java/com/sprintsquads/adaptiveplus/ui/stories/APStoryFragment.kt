package com.sprintsquads.adaptiveplus.ui.stories

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEvent
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEventInfo
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapState
import com.sprintsquads.adaptiveplus.ui.stories.progress.APStoriesProgressView
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoriesDialogViewModelDelegate
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoryViewModel
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoryViewModelFactory
import com.sprintsquads.adaptiveplus.utils.runDelayedTask
import kotlinx.android.synthetic.main.ap_fragment_story.*


internal class APStoryFragment :
    Fragment(), APStoriesProgressView.StoriesListener {

    companion object {
        private const val PRE_ANIMATION_DELAY = 5L

        private const val EXTRA_STORY = "extra_story"

        @JvmStatic
        fun newInstance(
            story: APStory,
            controller: APStoriesProgressController,
            storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegate
        ) = APStoryFragment().apply {
            arguments = bundleOf(EXTRA_STORY to story)
            this.storiesProgressController = controller
            this.storiesDialogViewModelDelegate = storiesDialogViewModelDelegate
        }
    }


    private lateinit var story: APStory
    private lateinit var storiesProgressController: APStoriesProgressController
    private lateinit var storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegate

    private lateinit var viewModel: APStoryViewModel

    private var isUnderTouch = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (arguments?.get(EXTRA_STORY) as? APStory)?.let { story ->
            this.story = story
        } ?: run {
            storiesProgressController.closeStories()
            return
        }

        val viewModelFactory = APStoryViewModelFactory(story, storiesDialogViewModelDelegate)
        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(APStoryViewModel::class.java)
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
            snaps = story.snaps,
            viewModel
        )

        apStoriesProgressView.setStoriesCount(story.snaps.size)
        apStoriesProgressView.setStoryDurations(story.snaps.map { (it.showTime * 1000).toLong() })
        apStoriesProgressView.setStoriesListener(this)

        apCloseButtonImageView.setOnClickListener {
            storiesProgressController.closeStories()
        }

        updateTopMargin()

        apStoryFragmentLayout.addOnLayoutChangeListener(apStoryFragmentLayoutChangeListener)

        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        if (!apStoriesProgressView.hasStarted()) {
            val snapIndex = apSnapsViewPager.currentItem
            apStoriesProgressView.startStories(snapIndex)
        }

        runDelayedTask({
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

    private val apStoryFragmentLayoutChangeListener = View.OnLayoutChangeListener {
            v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            updateTopMargin()
        }
    }

    private fun updateTopMargin() {
        story.snaps.firstOrNull()?.let { snap ->
            val baseScreenWidth = maxOf(snap.width, 0.001)
            val scaleFactor = (apStoryFragmentLayout.width / baseScreenWidth).toFloat()
            val newSnapHeight = (snap.height * scaleFactor).toInt()
            val newActionAreaHeight = ((snap.actionAreaHeight ?: 0.0) * scaleFactor).toInt()

            val defaultMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
            val marginTop =
                if (newSnapHeight + newActionAreaHeight > apStoryFragmentLayout.height) {
                    (apStoryFragmentLayout.height - newSnapHeight) / 2 + defaultMargin
                } else {
                    defaultMargin
                }

            val constraintSet = ConstraintSet()
            constraintSet.clone(apStoryFragmentLayout)
            constraintSet.setMargin(apStoriesProgressView.id, ConstraintSet.TOP, marginTop)
            constraintSet.applyTo(apStoryFragmentLayout)
        }
    }

    private fun setupObservers() {
        viewModel.snapReadinessUpdatedEventLiveData.observe(
            viewLifecycleOwner, snapReadinessUpdatedEventObserver)
        viewModel.isStoriesPausedLiveData.observe(
            viewLifecycleOwner, isStoriesPausedObserver)
        viewModel.snapEventInfoLiveData.observe(
            viewLifecycleOwner, snapEventObserver)
    }

    private val snapReadinessUpdatedEventObserver =
        Observer<String> {
            updateStoryProgressState()
        }

    private val isStoriesPausedObserver = Observer<Boolean> {
        runDelayedTask({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    private val snapEventObserver = Observer<APSnapEventInfo> { snapEvent ->
        if (snapEvent.event != APSnapEvent.NONE &&
            story.snaps.any { it.id == snapEvent.snapId }
        ) {
            viewModel.onSnapEvent(
                APSnapEventInfo(snapEvent.snapId, APSnapEvent.NONE)
            )

            when (snapEvent.event) {
                APSnapEvent.IS_UNDER_TOUCH -> {
                    isUnderTouch = true
                    updateStoryProgressState()
                }
                APSnapEvent.IS_NOT_UNDER_TOUCH -> {
                    isUnderTouch = false
                    updateStoryProgressState()
                }
                APSnapEvent.GO_TO_PREV_SNAP -> {
                    goToPrevSnap()
                }
                APSnapEvent.GO_TO_NEXT_SNAP -> {
                    goToNextSnap()
                }
                APSnapEvent.CLOSE_STORIES -> {
                    storiesProgressController.closeStories()
                }
                APSnapEvent.RESET_AND_PAUSE_SNAP_PROGRESS -> {
                    apStoriesProgressView?.resetCurrentSnap()
                    apStoriesProgressView?.pause()
                }
                else -> { }
            }
        }
    }

    override fun onComplete() {
        resetLastSnap()

        storiesProgressController.moveToNextStory(story.id)

        runDelayedTask({
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

        if (apSnapsViewPager.currentItem < story.snaps.size - 1) {
            apSnapsViewPager.currentItem++
        }

        runDelayedTask({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    private fun showPrev() {
        if (apSnapsViewPager == null) return

        resetLastSnap()

        if (apSnapsViewPager.currentItem == 0) {
            storiesProgressController.moveToPrevStory(story.id)
        }
        else {
            apSnapsViewPager.currentItem = maxOf(0, apSnapsViewPager.currentItem - 1)
        }

        runDelayedTask({
            updateStoryProgressState()
        }, PRE_ANIMATION_DELAY)
    }

    private fun updateStoryProgressState() {
        if (view == null) return

        val snapIndex = apSnapsViewPager.currentItem
        val snapId = story.snaps.getOrNull(snapIndex)?.id ?: ""

        if (viewModel.isSnapReady(snapId)) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                && !viewModel.isStoriesPaused()
                && !isUnderTouch
            ) {
                apStoriesProgressView.resume()

                viewModel.updateStoryProgressState(snapId = snapId, state = APSnapState.RESUMED)
            }
            else {
                apStoriesProgressView.pause()
                viewModel.updateStoryProgressState(snapId = snapId, state = APSnapState.PAUSED)
            }
        }
        else {
            apStoriesProgressView.pause()
            viewModel.updateStoryProgressState(snapId = snapId, state = APSnapState.PAUSED)
        }
    }

    private fun goToNextSnap() {
        apStoriesProgressView?.skip()
    }

    private fun goToPrevSnap() {
        apStoriesProgressView?.reverse()
    }

    private fun resetLastSnap() {
        val snapId = story.snaps.getOrNull(apSnapsViewPager.currentItem)?.id ?: ""
        viewModel.updateStoryProgressState(snapId = snapId, state = APSnapState.RESET)
    }

}