package plus.adaptive.sdk.ui.stories.progress

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import plus.adaptive.sdk.R
import kotlinx.android.synthetic.main.ap_stoppable_progress_bar.view.*


internal class APStoppableProgressBar : FrameLayout {

    companion object {
        private const val DEFAULT_PROGRESS_DURATION = 2000L
    }


    private var animation: StoppableScaleAnimation? = null
    private var duration = DEFAULT_PROGRESS_DURATION
    private var callback: Callback? = null


    interface Callback {
        fun onStartProgress()
        fun onFinishProgress(elapsedTime: Long)
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
        val elapsedTime = animation?.getElapsedTime() ?: 0L
        setMaxWithoutCallback()
        callback?.onFinishProgress(elapsedTime)
    }

    fun setMin() {
        val elapsedTime = animation?.getElapsedTime() ?: 0L
        setMinWithoutCallback()
        callback?.onFinishProgress(elapsedTime)
    }

    fun setMinWithoutCallback() {
        clearProgressAnimation()
        apFrontProgressView.visibility = View.INVISIBLE
    }

    fun setMaxWithoutCallback() {
        clearProgressAnimation()
        apFrontProgressView.visibility = View.VISIBLE
    }

    fun startProgress() {
        setMinWithoutCallback()

        animation = StoppableScaleAnimation(
            0f, 1f, 1f, 1f,
            Animation.ABSOLUTE, 0f,
            Animation.RELATIVE_TO_SELF, 0f)
        animation?.duration = duration
        animation?.interpolator = LinearInterpolator()
        animation?.setAnimationListener(
            object: Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    apFrontProgressView.visibility = View.VISIBLE
                    callback?.onStartProgress()
                }

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    val elapsedTime = (animation as? StoppableScaleAnimation)?.getElapsedTime() ?: 0L
                    callback?.onFinishProgress(elapsedTime)
                }
            }
        )
        animation?.fillAfter = true

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

    private fun clearProgressAnimation() {
        apFrontProgressView?.clearAnimation()
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

        private var mElapsedTime = 0L
        private var mElapsedAtPause = 0L
        private var mPaused = false

        override fun getTransformation(
            currentTime: Long, outTransformation: Transformation, scale: Float
        ) : Boolean {
            mElapsedTime = currentTime - startTime

            if (mPaused && startTime != -1L && mElapsedAtPause == 0L) {
                mElapsedAtPause = mElapsedTime
            }

            if (mPaused && startTime != -1L) {
                startTime = currentTime - mElapsedAtPause
            }

            return super.getTransformation(currentTime, outTransformation, scale)
        }

        fun pause() {
            if (mPaused) return
            mElapsedAtPause = 0L
            mPaused = true
        }

        fun resume() {
            mPaused = false
        }

        fun getElapsedTime() : Long {
            return mElapsedTime
        }
    }
}