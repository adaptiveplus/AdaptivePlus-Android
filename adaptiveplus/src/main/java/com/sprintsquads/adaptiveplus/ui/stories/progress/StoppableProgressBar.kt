package com.sprintsquads.adaptiveplus.ui.stories.progress

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.extensions.hide
import com.sprintsquads.adaptiveplus.extensions.show
import kotlinx.android.synthetic.main.ap_stoppable_progress_bar.view.*


internal class StoppableProgressBar : FrameLayout {

    companion object {
        private const val DEFAULT_PROGRESS_DURATION = 2000L
    }


    private var animation: StoppableScaleAnimation? = null
    private var duration = DEFAULT_PROGRESS_DURATION
    private var callback: Callback? = null


    interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        View.inflate(context, R.layout.ap_stoppable_progress_bar, this)
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setMax() {
        finishProgress(true)
    }

    fun setMin() {
        finishProgress(false)
    }

    fun setMinWithoutCallback() {
        apMaxProgressView?.setBackgroundResource(R.color.apWhite50)
        apMaxProgressView?.show()

        animation?.setAnimationListener(null)
        animation?.cancel()
    }

    fun setMaxWithoutCallback() {
        apMaxProgressView?.setBackgroundResource(R.color.apWhite)
        apMaxProgressView?.show()

        animation?.setAnimationListener(null)
        animation?.cancel()
    }

    private fun finishProgress(isMax: Boolean) {
        if (isMax)  {
            apMaxProgressView?.setBackgroundResource(R.color.apWhite)
            apMaxProgressView.show()
        }
        else {
            apMaxProgressView?.hide()
        }

        animation?.setAnimationListener(null)
        animation?.resume()
        animation?.cancel()

        callback?.onFinishProgress()
    }

    fun startProgress() {
        apMaxProgressView?.hide()

        animation = StoppableScaleAnimation(
            0f, 1f, 1f, 1f,
            Animation.ABSOLUTE, 0f,
            Animation.RELATIVE_TO_SELF, 0f)
        animation!!.duration = duration
        animation!!.interpolator = LinearInterpolator()
        animation!!.setAnimationListener(object: Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                apFrontProgressView.show()
                callback?.onStartProgress()
            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                callback?.onFinishProgress()
            }
        })
        animation!!.fillAfter = true

        apFrontProgressView?.startAnimation(animation)
    }

    fun pauseProgress() {
        animation?.pause()
    }

    fun resumeProgress() {
        animation?.resume()
    }

    fun resetAnimationProgress() {
        startProgress()
    }

    fun clear() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        animation = null
    }

    private class StoppableScaleAnimation(
        fromX: Float, toX: Float,
        fromY: Float, toY: Float,
        pivotXType: Int, pivotXValue: Float,
        pivotYType: Int, pivotYValue: Float
    ) : ScaleAnimation(
        fromX, toX, fromY, toY,
        pivotXType, pivotXValue,
        pivotYType, pivotYValue
    ) {

        private var mElapsedAtPause = 0L
        private var mPaused = false

        override fun getTransformation(
            currentTime: Long, outTransformation: Transformation, scale: Float
        ) : Boolean {
            if (mPaused && mElapsedAtPause == 0L) {
                mElapsedAtPause = currentTime - startTime
            }

            if (mPaused) {
                startTime = currentTime - mElapsedAtPause
            }

            return super.getTransformation(currentTime, outTransformation, scale)
        }

        fun pause() {
            if (mPaused) return
            mElapsedAtPause = 0
            mPaused = true
        }

        fun resume() {
            mPaused = false
        }
    }
}