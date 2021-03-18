package com.sprintsquads.adaptiveplus.ui.stories

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.APSnap
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEvent
import com.sprintsquads.adaptiveplus.ui.stories.data.APSnapEventInfo
import com.sprintsquads.adaptiveplus.ui.stories.vm.APSnapViewModel
import com.sprintsquads.adaptiveplus.ui.stories.vm.APSnapViewModelFactory
import com.sprintsquads.adaptiveplus.ui.stories.vm.APStoryViewModelDelegate
import com.sprintsquads.adaptiveplus.utils.drawAPLayersOnLayout
import kotlinx.android.synthetic.main.ap_fragment_snap.*
import kotlin.math.abs


internal class APSnapFragment :
    Fragment(), View.OnTouchListener {

    companion object {
        private const val EXTRA_SNAP = "extra_snap"

        private const val CLICK_EVENT_THRESHOLD = 250 // time in milliseconds
        private const val SWIPE_EVENT_THRESHOLD = 100 // time in milliseconds
        private const val SWIPE_MIN_DISTANCE = 120
        private const val SWIPE_THRESHOLD_VELOCITY = 200

        @JvmStatic
        fun newInstance(
            snap: APSnap,
            storyViewModelDelegate: APStoryViewModelDelegate
        ) =
            APSnapFragment().apply {
                arguments = bundleOf(EXTRA_SNAP to snap)
                this.storyViewModelDelegate = storyViewModelDelegate
            }
    }


    private lateinit var snap: APSnap
    private lateinit var storyViewModelDelegate: APStoryViewModelDelegate
    private lateinit var viewModel: APSnapViewModel

    private var gestureDetector: GestureDetector? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (arguments?.get(EXTRA_SNAP) as? APSnap)?.let {
            snap = it
        } ?: run {
            storyViewModelDelegate.onSnapEvent(
                APSnapEventInfo("", APSnapEvent.CLOSE_STORIES))
            return
        }

        val snaViewModelFactory = APSnapViewModelFactory(snap, storyViewModelDelegate)
        viewModel = ViewModelProvider(this, snaViewModelFactory).get(APSnapViewModel::class.java)
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

        redrawSnap()

        apContentCardView.addOnLayoutChangeListener(apSnapFragmentLayoutChangeListener)
    }

    private val apSnapFragmentLayoutChangeListener = View.OnLayoutChangeListener {
            v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            redrawSnap()
        }
    }

    private fun redrawSnap() {
        val apContentCardViewConstraintSet = ConstraintSet()
        apContentCardViewConstraintSet.clone(apSnapLayout)
        apContentCardViewConstraintSet.constrainHeight(
            apContentCardView.id, (apContentCardView.width * snap.height / snap.width).toInt())
        apContentCardViewConstraintSet.applyTo(apSnapLayout)

        val baseScreenWidth = maxOf(snap.width, 0.001)
        val scaleFactor = (apContentCardView.width / baseScreenWidth).toFloat()

        apContentLayout.removeAllViews()
        drawAPLayersOnLayout(apContentLayout, snap.layers, scaleFactor, viewModel)
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
                        viewModel.onSnapEvent(APSnapEvent.CLOSE_STORIES)
                        return true
                    }
                }

                if (e1.y > e2.y) {
                    if (e1.y - e2.y > SWIPE_MIN_DISTANCE && abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        // Bottom -> Top
                        // TODO: Run Button Layer Actions
                        return true
                    }
                }
            }
            return false
        }
    }

}