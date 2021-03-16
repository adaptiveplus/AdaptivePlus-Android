package com.sprintsquads.adaptiveplus.ui.stories.progress

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout


internal class APStoriesProgressView : LinearLayout {

    companion object {
        private val PROGRESS_BAR_LAYOUT_PARAM = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        private val SPACE_LAYOUT_PARAM = LayoutParams(5, LayoutParams.WRAP_CONTENT)
    }

    private var progressBars = mutableListOf<StoppableProgressBar>()

    private var storiesCount = 0

    private var current = -1
    private var storiesListener: StoriesListener? = null
    var isComplete: Boolean = false

    private var isSkipStart: Boolean = false
    private var isReverseStart: Boolean = false

    interface StoriesListener {
        fun onNext()
        fun onPrev()
        fun onComplete()
    }

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        orientation = HORIZONTAL
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()

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
            layoutParams = PROGRESS_BAR_LAYOUT_PARAM
        }

    private fun createSpace() =
        View(context).apply {
            layoutParams = SPACE_LAYOUT_PARAM
        }

    fun setStoriesCount(storiesCount: Int) {
        this.storiesCount = storiesCount
        bindViews()
    }

    fun setStoriesListener(storiesListener: StoriesListener) {
        this.storiesListener = storiesListener
    }

    fun skip() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return

        val p = progressBars[current]
        isSkipStart = true
        p.setMax()
    }

    fun reverse() {
        if (isSkipStart || isReverseStart) return
        if (isComplete) return
        if (current < 0) return

        val p = progressBars[current]
        isReverseStart = true
        p.setMin()
    }

    fun setStoryDuration(duration: Long) {
        for (i in 0 until progressBars.size) {
            progressBars[i].setDuration(duration)
            progressBars[i].setCallback(buildCallback(i))
        }
    }

    fun setStoryDurations(durations: List<Long>) {
        if (durations.size != progressBars.size) {
            setStoryDuration(durations.getOrNull(0) ?: 3000)
        }
        else {
            for (i in 0 until progressBars.size) {
                progressBars[i].setDuration(durations[i])
                progressBars[i].setCallback(buildCallback(i))
            }
        }
    }

    private fun buildCallback(index: Int) = object : StoppableProgressBar.Callback {

        override fun onStartProgress() {
            current = index
        }

        override fun onFinishProgress() {
            if (isReverseStart) {
                storiesListener?.onPrev()

                if (0 <= (current - 1)) {
                    val p = progressBars[current - 1]
                    p.setMinWithoutCallback()
                    progressBars[--current].startProgress()
                } else {
                    progressBars[current].startProgress()
                }

                isReverseStart = false

                return
            }

            val next = current + 1

            if (next <= (progressBars.size - 1)) {
                storiesListener?.onNext()
                progressBars[next].startProgress()
            } else {
                isComplete = true
                storiesListener?.onComplete()
            }

            isSkipStart = false
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
        progressBars[0].startProgress()
    }

    fun startStories(from: Int) {
        reset()

        for (i in 0 until from) {
            progressBars[i].setMaxWithoutCallback()
        }
        progressBars[from].startProgress()
    }

    fun getCurrentIndex() = current

    fun destroy() {
        for (p in progressBars) {
            p.clear()
        }
    }

    fun pause() {
        if (current < 0) return
        progressBars[current].pauseProgress()
    }

    fun resume() {
        if (current < 0) return
        progressBars[current].resumeProgress()
    }

    fun hasStarted(): Boolean {
        return !isComplete && current >= 0
    }
}