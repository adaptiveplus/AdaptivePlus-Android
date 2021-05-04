package plus.adaptive.sdk.ui.stories

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APSnap
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ext.show
import plus.adaptive.sdk.ui.stories.data.APSnapEvent
import plus.adaptive.sdk.ui.stories.data.APSnapEventInfo
import plus.adaptive.sdk.ui.stories.vm.APSnapViewModel
import plus.adaptive.sdk.ui.stories.vm.APSnapViewModelFactory
import plus.adaptive.sdk.ui.stories.vm.APStoryViewModelDelegateProtocol
import plus.adaptive.sdk.utils.drawAPLayersOnLayout
import plus.adaptive.sdk.utils.drawAPSnapActionArea
import plus.adaptive.sdk.utils.safeRun
import kotlinx.android.synthetic.main.ap_fragment_snap.*
import kotlin.math.abs


internal class APSnapFragment :
    Fragment(), View.OnTouchListener {

    companion object {
        private const val EXTRA_SNAP = "extra_snap"
        private const val EXTRA_SCALE_FACTOR = "extra_scale_factor"

        private const val CLICK_EVENT_THRESHOLD = 250 // time in milliseconds
        private const val SWIPE_EVENT_THRESHOLD = 100 // time in milliseconds
        private const val SWIPE_MIN_DISTANCE = 120
        private const val SWIPE_THRESHOLD_VELOCITY = 200

        @JvmStatic
        fun newInstance(
            snap: APSnap,
            storyViewModelDelegate: APStoryViewModelDelegateProtocol,
            scaleFactor: Float
        ) =
            APSnapFragment().apply {
                arguments = bundleOf(
                    EXTRA_SNAP to snap,
                    EXTRA_SCALE_FACTOR to scaleFactor)
                this.storyViewModelDelegate = storyViewModelDelegate
            }
    }


    private lateinit var snap: APSnap
    private var storyViewModelDelegate: APStoryViewModelDelegateProtocol? = null
    private lateinit var viewModel: APSnapViewModel
    private var scaleFactor: Float = 1f

    private var gestureDetector: GestureDetector? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (arguments?.get(EXTRA_SNAP) as? APSnap)?.let {
            snap = it
        } ?: run {
            storyViewModelDelegate?.onSnapEvent(
                APSnapEventInfo("", APSnapEvent.CLOSE_STORIES))
            return
        }

        val snaViewModelFactory = APSnapViewModelFactory(snap, storyViewModelDelegate)
        viewModel = ViewModelProvider(this, snaViewModelFactory).get(APSnapViewModel::class.java)

        scaleFactor = arguments?.getFloat(EXTRA_SCALE_FACTOR, 1f) ?: 1f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_snap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnTouchListener(this)
        gestureDetector = GestureDetector(context, SwipeDetector())

        drawSnap()

        apSnapLayout.addOnLayoutChangeListener(apSnapLayoutChangeListener)

        apSnapRetryBtnImageView.setOnClickListener {
            viewModel.prepare()
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.isSnapReadyLiveData.observe(viewLifecycleOwner, isSnapReadyObserver)
        viewModel.snapLoadingProgressLiveData.observe(viewLifecycleOwner, snapLoadingProgressObserver)
        viewModel.isErrorStateLiveData.observe(viewLifecycleOwner, isErrorStateObserver)
    }

    private val isSnapReadyObserver = Observer<Boolean> { isReady ->
        if (isReady) {
            apSnapLoadingLayout.hide()
        } else {
            apSnapLoadingLayout.show()
        }
    }

    private val snapLoadingProgressObserver = Observer<Float> { progress ->
        apSnapLoadingProgressBar.progress = (progress * 100).toInt()
    }

    private val isErrorStateObserver = Observer<Boolean> { isErrorState ->
        if (isErrorState) {
            apSnapLoadingProgressBar.hide()
            apSnapRetryBtnImageView.show()
        } else {
            apSnapLoadingProgressBar.show()
            apSnapRetryBtnImageView.hide()
        }
    }

    private val apSnapLayoutChangeListener = View.OnLayoutChangeListener {
            v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            val newSnapHeight = (snap.height * scaleFactor).toInt()
            val oldActionAreaHeight = snap.actionAreaHeight ?: 0.0
            val newActionAreaHeight = (oldActionAreaHeight * scaleFactor).toInt()
            val newActionAreaBtmMargin = ((newActionAreaHeight - oldActionAreaHeight) / 2).toInt()

            val apContentCardViewConstraintSet = ConstraintSet()
            apContentCardViewConstraintSet.clone(apSnapLayout)

            if (newSnapHeight + newActionAreaHeight > apSnapLayout.height) {
                apContentCardViewConstraintSet.connect(
                    apContentCardView.id, ConstraintSet.TOP, apSnapLayout.id, ConstraintSet.TOP)
                apContentCardViewConstraintSet.connect(
                    apContentCardView.id, ConstraintSet.BOTTOM, apSnapLayout.id, ConstraintSet.BOTTOM)
                apContentCardViewConstraintSet.connect(
                    apActionAreaLayout.id, ConstraintSet.BOTTOM, apContentCardView.id, ConstraintSet.BOTTOM)
                apContentCardViewConstraintSet.clear(
                    apActionAreaLayout.id, ConstraintSet.TOP)
                apContentCardViewConstraintSet.setMargin(
                    apActionAreaLayout.id, ConstraintSet.BOTTOM, newActionAreaBtmMargin)
            } else {
                apContentCardViewConstraintSet.connect(
                    apContentCardView.id, ConstraintSet.TOP, apSnapLayout.id, ConstraintSet.TOP)
                apContentCardViewConstraintSet.clear(
                    apContentCardView.id, ConstraintSet.BOTTOM)
                apContentCardViewConstraintSet.connect(
                    apActionAreaLayout.id, ConstraintSet.TOP, apContentCardView.id, ConstraintSet.BOTTOM)
                apContentCardViewConstraintSet.connect(
                    apActionAreaLayout.id, ConstraintSet.BOTTOM, apSnapLayout.id, ConstraintSet.BOTTOM)
                apContentCardViewConstraintSet.setMargin(
                    apActionAreaLayout.id, ConstraintSet.BOTTOM, 0)
            }

            apContentCardViewConstraintSet.applyTo(apSnapLayout)
        }
    }

    private fun drawSnap() {
        val apSnapLayoutConstraintSet = ConstraintSet()
        apSnapLayoutConstraintSet.clone(apSnapLayout)
        apSnapLayoutConstraintSet.constrainHeight(
            apContentCardView.id, (snap.height * scaleFactor).toInt())
        apSnapLayoutConstraintSet.constrainWidth(
            apActionAreaLayout.id, snap.width.toInt())
        apSnapLayoutConstraintSet.constrainHeight(
            apActionAreaLayout.id, ((snap.actionAreaHeight ?: 0.0) * scaleFactor).toInt())
        apSnapLayoutConstraintSet.applyTo(apSnapLayout)

        val apContentLayoutConstraintSet = ConstraintSet()
        apContentLayoutConstraintSet.clone(apContentLayout)
        apContentLayoutConstraintSet.constrainWidth(apSnapLayersLayout.id, snap.width.toInt())
        apContentLayoutConstraintSet.constrainHeight(apSnapLayersLayout.id, snap.height.toInt())
        apContentLayoutConstraintSet.setScaleX(apSnapLayersLayout.id, scaleFactor)
        apContentLayoutConstraintSet.setScaleY(apSnapLayersLayout.id, scaleFactor)
        apContentLayoutConstraintSet.applyTo(apContentLayout)

        safeRun(
            executable = {
                drawAPLayersOnLayout(apSnapLayersLayout, snap.layers, viewModel)

                snap.actionArea?.let { actionArea ->
                    drawAPSnapActionArea(apActionAreaLayout, actionArea, viewModel)
                }
            },
            onExceptionCaught = {
                viewModel.onSnapEvent(APSnapEvent.CLOSE_STORIES)
            }
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (gestureDetector?.onTouchEvent(event) == true) return true

        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                viewModel.onSnapEvent(APSnapEvent.IS_UNDER_TOUCH)
            }
            MotionEvent.ACTION_UP -> {
                viewModel.onSnapEvent(APSnapEvent.IS_NOT_UNDER_TOUCH)

                if (event.eventTime - event.downTime <= CLICK_EVENT_THRESHOLD) {
                    if (view != null) {
                        if (event.x < resources.displayMetrics.widthPixels / 2) {
                            viewModel.onSnapEvent(APSnapEvent.GO_TO_PREV_SNAP)
                        } else {
                            viewModel.onSnapEvent(APSnapEvent.GO_TO_NEXT_SNAP)
                        }
                    }
                }

                v?.performClick()
            }
            MotionEvent.ACTION_CANCEL -> {
                viewModel.onSnapEvent(APSnapEvent.IS_NOT_UNDER_TOUCH)
            }
            else -> { }
        }

        return true
    }


    inner class SwipeDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (abs(e2.eventTime - e1.eventTime) > SWIPE_EVENT_THRESHOLD) {
                if (e2.y > e1.y) {
                    if (e2.y - e1.y > SWIPE_MIN_DISTANCE && abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        // Top -> Bottom
                        viewModel.onSnapEvent(APSnapEvent.IS_NOT_UNDER_TOUCH)
                        viewModel.onSnapEvent(APSnapEvent.CLOSE_STORIES)
                        return true
                    }
                }

                if (e1.y > e2.y) {
                    if (e1.y - e2.y > SWIPE_MIN_DISTANCE && abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        // Bottom -> Top
                        viewModel.onSnapEvent(APSnapEvent.IS_NOT_UNDER_TOUCH)
                        viewModel.runActionAreaActions()
                        return true
                    }
                }
            }
            return false
        }
    }

}