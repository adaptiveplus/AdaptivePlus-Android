package com.sprintsquads.adaptiveplus.ui.stories.progress

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sprintsquads.adaptiveplus.R


internal class APStoriesProgressView : LinearLayout {

    interface LifecycleListener {
        fun onNext(elapsedTime: Long)
        fun onPrev(elapsedTime: Long)
        fun onComplete(elapsedTime: Long)
    }


    private var progressBars = mutableListOf<StoppableProgressBar>()

    private var storiesCount = 0

    private var current = -1
    private var lifecycleListener: LifecycleListener? = null

    private var isComplete: Boolean = false
    private var isSkipStart: Boolean = false
    private var isReverseStart: Boolean = false


    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        orientation = HORIZONTAL
        bindViews()
    }

    private fun bindViews() {
        reset()
        removeAllViews()
        progressBars.clear()

        for (i in 0 until storiesCount) {
            val p = createProgressBar()
            progressBars.add(p)
            addView(p)

            if ((i + 1) < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar() =
        StoppableProgressBar(context).apply {
            val height = resources.getDimension(R.dimen.ap_stories_progress_bar_height).toInt()
            layoutParams = LayoutParams(0, height, 1f)
        }

    private fun createSpace() =
        View(context).apply {
            val height = resources.getDimension(R.dimen.ap_stories_progress_bar_height).toInt()
            val width = resources.getDimension(R.dimen.ap_stories_progress_bar_space_width).toInt()
            layoutParams = LayoutParams(width, height)
        }

    fun setStoriesListener(listener: LifecycleListener) {
        this.lifecycleListener = listener
    }

    fun skip() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return

        progressBars.getOrNull(current)?.let {
            isSkipStart = true
            it.setMax()
        }
    }

    fun reverse() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return

        progressBars.getOrNull(current)?.let {
            isReverseStart = true
            it.setMin()
        }
    }

    fun setSnapsDurations(durations: List<Long>) {
        if (durations.size != storiesCount) {
            storiesCount = durations.size
            bindViews()
        }

        for (i in 0 until progressBars.size) {
            progressBars[i].setDuration(durations.getOrNull(i) ?: 3000)
            progressBars[i].setCallback(buildStoppableProgressBarCallback(i))
        }
    }

    private fun buildStoppableProgressBarCallback(index: Int) =
        object : StoppableProgressBar.Callback {

            override fun onStartProgress() {
                current = index
            }

            override fun onFinishProgress(elapsedTime: Long) {
                if (isReverseStart) {
                    isReverseStart = false

                    if (current - 1 >= 0) {
                        val p = progressBars[current - 1]
                        p.setMinWithoutCallback()
                        progressBars[--current].startProgress()
                    } else {
                        progressBars[current].startProgress()
                    }

                    lifecycleListener?.onPrev(elapsedTime)
                } else {
                    isSkipStart = false

                    val next = current + 1

                    if (next < progressBars.size) {
                        progressBars[next].startProgress()
                        lifecycleListener?.onNext(elapsedTime)
                    } else {
                        isComplete = true
                        lifecycleListener?.onComplete(elapsedTime)
                    }
                }
            }
        }

    private fun reset() {
        destroy()

        current = -1
        isComplete = false

        isSkipStart = false
        isReverseStart = false
    }

    fun resetCurrentSnap() {
        progressBars.getOrNull(current)?.resetAnimationProgress()
    }

    fun startStories() {
        reset()
        progressBars.getOrNull(0)?.startProgress()
    }

    fun startStories(from: Int) {
        reset()

        for (i in 0 until from) {
            progressBars.getOrNull(i)?.setMaxWithoutCallback()
        }
        progressBars.getOrNull(from)?.startProgress()
    }

    fun destroy() {
        for (p in progressBars) {
            p.setMinWithoutCallback()
        }
    }

    fun pause() {
        progressBars.getOrNull(current)?.pauseProgress()
    }

    fun resume() {
        progressBars.getOrNull(current)?.resumeProgress()
    }

    fun hasStarted(): Boolean {
        return !isComplete && current >= 0
    }
}