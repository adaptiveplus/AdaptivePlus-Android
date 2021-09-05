package plus.adaptive.sdk.ui.stories

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.R
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.ext.setTransitionDuration
import plus.adaptive.sdk.ui.stories.data.APSnapEvent
import plus.adaptive.sdk.ui.stories.data.APSnapEventInfo
import plus.adaptive.sdk.ui.stories.data.APSnapState
import plus.adaptive.sdk.ui.stories.progress.APStoryProgressView
import plus.adaptive.sdk.ui.stories.vm.APStoriesDialogViewModelDelegateProtocol
import plus.adaptive.sdk.ui.stories.vm.APStoryViewModel
import plus.adaptive.sdk.ui.stories.vm.APStoryViewModelFactory
import kotlinx.android.synthetic.main.ap_fragment_story.*


internal class APStoryFragment :
    Fragment(), APStoryProgressView.LifecycleListener {

    companion object {
        private const val EXTRA_STORY = "extra_story"

        @JvmStatic
        fun newInstance(
            story: APStory,
            controller: APStoriesProgressController,
            storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegateProtocol
        ) = APStoryFragment().apply {
            arguments = bundleOf(EXTRA_STORY to story)
            this.storiesProgressController = controller
            this.storiesDialogViewModelDelegate = storiesDialogViewModelDelegate
        }
    }


    private lateinit var story: APStory
    private lateinit var storiesProgressController: APStoriesProgressController
    private lateinit var storiesDialogViewModelDelegate: APStoriesDialogViewModelDelegateProtocol

    private lateinit var viewModel: APStoryViewModel

    private var snapsAdapter: APSnapsPagerAdapter? = null

    private var isUnderTouch = false
    private var isFragmentStateResumed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (arguments?.get(EXTRA_STORY) as? APStory)?.let { story ->
            this.story = story
        } ?: run {
            storiesProgressController.closeStories()
            return
        }

        activity?.let {
            val viewModelFactory = APStoryViewModelFactory(it, story, storiesDialogViewModelDelegate)
            val viewModelProvider = ViewModelProvider(this, viewModelFactory)
            viewModel = viewModelProvider.get(APStoryViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snapsAdapter = APSnapsPagerAdapter(
            fragmentManager = childFragmentManager,
            snaps = story.snaps,
            viewModel,
            1f
        )

        apSnapsViewPager.offscreenPageLimit = 1
        apSnapsViewPager.adapter = snapsAdapter
        apSnapsViewPager.setTransitionDuration(0)

        apStoryProgressView.setSnapsDurations(story.snaps.map { (it.showTime * 1000).toLong() })
        apStoryProgressView.setStoriesListener(this)

        apCloseButtonImageView.setOnClickListener {
            storiesProgressController.closeStories()
        }

        updateStoryScaleFactor()

        apStoryFragmentLayout.addOnLayoutChangeListener(apStoryFragmentLayoutChangeListener)

        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        isFragmentStateResumed = true
        updateStoryProgressState()
    }

    override fun onPause() {
        super.onPause()
        isFragmentStateResumed = false
        updateStoryProgressState()
    }

    override fun onDestroyView() {
        apSnapsViewPager?.currentItem?.let { index ->
            apStoryProgressView?.getElapsedTime(index)?.let { elapsedTime ->
                logCurrentSnapShownEvent(elapsedTime)
            }
        }
        apStoryProgressView?.destroy()
        super.onDestroyView()
    }

    private val apStoryFragmentLayoutChangeListener = View.OnLayoutChangeListener {
            v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            updateStoryScaleFactor()
        }
    }

    private fun updateStoryScaleFactor() {
        story.snaps.firstOrNull()?.let { snap ->
            val baseScreenWidth = maxOf(snap.width, 0.001)
            val scaleFactor = (apStoryFragmentLayout.width / baseScreenWidth).toFloat()

            snapsAdapter?.updateScaleFactor(scaleFactor)

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
            constraintSet.setMargin(apStoryProgressView.id, ConstraintSet.TOP, marginTop)
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
        updateStoryProgressState()
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
                    apStoryProgressView?.resetCurrentSnap()
                    apStoryProgressView?.pause()
                }
                else -> { }
            }
        }
    }

    override fun onComplete(elapsedTime: Long) {
        logCurrentSnapShownEvent(elapsedTime)
        resetCurrentSnap()
        storiesProgressController.moveToNextStory(story.id)
        updateStoryProgressState()
    }

    override fun onPrev(elapsedTime: Long) {
        if (apSnapsViewPager == null) return

        logCurrentSnapShownEvent(elapsedTime)
        resetCurrentSnap()

        if (apSnapsViewPager.currentItem == 0) {
            storiesProgressController.moveToPrevStory(story.id)
        }
        else {
            apSnapsViewPager.currentItem = maxOf(0, apSnapsViewPager.currentItem - 1)
        }

        updateStoryProgressState()
    }

    override fun onNext(elapsedTime: Long) {
        if (apSnapsViewPager == null) return

        logCurrentSnapShownEvent(elapsedTime)
        resetCurrentSnap()

        if (apSnapsViewPager.currentItem < story.snaps.size - 1) {
            apSnapsViewPager.currentItem++
        }

        updateStoryProgressState()
    }

    private fun updateStoryProgressState() {
        if (view == null) return

        val snapIndex = apSnapsViewPager.currentItem
        val snapId = story.snaps.getOrNull(snapIndex)?.id ?: ""

        if (viewModel.isSnapReady(snapId)
            && isFragmentStateResumed
            && !viewModel.isStoriesPaused()
            && !isUnderTouch
        ) {
            if (!apStoryProgressView.hasStarted()) {
                apStoryProgressView.startStories(snapIndex)
            } else {
                apStoryProgressView.resume()
            }

            viewModel.updateSnapProgressState(snapId = snapId, state = APSnapState.RESUMED)

            if (snapIndex == story.snaps.lastIndex) {
                viewModel.saveWatchedStoryId(story.id)
            }
        }
        else {
            apStoryProgressView.pause()
            viewModel.updateSnapProgressState(snapId = snapId, state = APSnapState.PAUSED)
        }
    }

    private fun goToNextSnap() {
        apStoryProgressView?.skip()
    }

    private fun goToPrevSnap() {
        apStoryProgressView?.reverse()
    }

    private fun resetCurrentSnap() {
        val snapId = story.snaps.getOrNull(apSnapsViewPager.currentItem)?.id ?: ""
        viewModel.updateSnapProgressState(snapId = snapId, state = APSnapState.RESET)
    }

    private fun logCurrentSnapShownEvent(elapsedTime: Long) {
        if (apSnapsViewPager == null || elapsedTime == 0L) return

        val snapId = story.snaps.getOrNull(apSnapsViewPager.currentItem)?.id ?: ""

        APAnalytics.logEvent(
            APAnalyticsEvent(
                name = "shown-snap",
                campaignId = story.campaignId,
                apViewId = storiesDialogViewModelDelegate.getAPViewId(),
                params = mapOf(
                    "snapId" to snapId,
                    "watchedTime" to (elapsedTime.toDouble() / 1000)
                )
            )
        )
    }
}