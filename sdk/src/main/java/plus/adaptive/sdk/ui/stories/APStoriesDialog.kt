package plus.adaptive.sdk.ui.stories

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.ext.setTransitionDuration
import plus.adaptive.sdk.ui.apview.vm.APViewVMDelegateProtocol
import plus.adaptive.sdk.ui.dialogs.APDialogFragment
import plus.adaptive.sdk.ui.stories.vm.APStoriesDialogViewModel
import plus.adaptive.sdk.ui.stories.vm.APStoriesDialogViewModelFactory
import plus.adaptive.sdk.utils.restrictToRange
import kotlinx.android.synthetic.main.ap_fragment_ap_stories_dialog.*


internal class APStoriesDialog :
    DialogFragment(), APStoriesProgressController, APDialogFragment {

    companion object {
        const val EXTRA_STORIES = "extra_stories"
        const val EXTRA_START_INDEX = "extra_start_index"

        @JvmStatic
        fun newInstance(
            stories: List<APStory>,
            startIndex: Int,
            apViewVMDelegate: APViewVMDelegateProtocol
        ) = APStoriesDialog().apply {
            arguments = bundleOf(
                EXTRA_STORIES to ArrayList(stories),
                EXTRA_START_INDEX to startIndex
            )

            this.apViewVMDelegate = apViewVMDelegate
        }
    }


    private lateinit var stories: List<APStory>

    private lateinit var viewModel: APStoriesDialogViewModel
    private lateinit var apViewVMDelegate: APViewVMDelegateProtocol

    private val onDismissListeners = mutableSetOf<APDialogFragment.OnDismissListener>()


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

        val viewModelFactory = APStoriesDialogViewModelFactory(apViewVMDelegate)
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

        val lastShownStory = stories.getOrNull(apStoriesViewPager?.currentItem ?: -1)
        val campaignId = lastShownStory?.campaignId
        apViewVMDelegate.onAPStoriesFinished(campaignId)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListeners.forEach { it.onDismiss() }
    }

    override fun addOnDismissListener(listener: APDialogFragment.OnDismissListener) {
        this.onDismissListeners.add(listener)
    }

    override fun removeOnDismissListener(listener: APDialogFragment.OnDismissListener) {
        this.onDismissListeners.remove(listener)
    }

    override fun clearAllOnDismissListeners() {
        this.onDismissListeners.clear()
    }
}