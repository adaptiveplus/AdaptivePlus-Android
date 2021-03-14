package com.sprintsquads.adaptiveplus.sdk.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.sdk.data.AdaptiveCustomAction
import com.sprintsquads.adaptiveplus.ui.tag.AdaptiveTagFragment


class AdaptiveTag : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.AdaptiveTag)
        val tagId = arr.getString(R.styleable.AdaptiveTag_apTagId) ?: ""

        init(tagId)

        arr.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.AdaptiveTag, defStyleAttr, 0)
        val tagId = arr.getString(R.styleable.AdaptiveTag_apTagId) ?: ""

        init(tagId)

        arr.recycle()
    }


    private lateinit var tagId: String

    private var adaptiveTagFragment: AdaptiveTagFragment? = null
    private var customActionCallback: AdaptiveCustomAction? = null


    /**
     * Setter of adaptive tag id
     *
     * @param tagId - id of adaptive tag
     */
    fun setAdaptiveTagId(tagId: String) {
        this.tagId = tagId
        adaptiveTagFragment?.setTagId(tagId)
    }

    private fun init(
        tagId: String = ""
    ) {
        this.tagId = tagId
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        getFragmentManager()?.let { fragmentManager ->
            if (fragmentManager.findFragmentById(id) == null) {
                adaptiveTagFragment =
                    AdaptiveTagFragment.newInstance(
                        tagId = tagId
                    )
                adaptiveTagFragment?.let {
                    try {
                        fragmentManager
                            .beginTransaction()
                            .replace(id, it)
                            .commit()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            } else {
                fragmentManager.findFragmentById(id)?.let {
                    adaptiveTagFragment = it as? AdaptiveTagFragment
                }
            }

            customActionCallback?.let { callback ->
                adaptiveTagFragment?.setAdaptiveCustomActionCallback(callback)
            }
        }
    }

    private fun getFragmentManager() : FragmentManager? {
        return try {
            FragmentManager.findFragment<Fragment>(this).childFragmentManager
        } catch (e: IllegalStateException) {
            (context as? FragmentActivity)?.supportFragmentManager
        }
    }

    /**
     * Method to launch force update
     */
    fun refresh() {
        adaptiveTagFragment?.refresh()
    }

    /**
     * Setter of adaptive custom action callback for the given container
     *
     * @param callback - adaptive custom action callback
     * @see AdaptiveCustomAction
     */
    fun setAdaptiveCustomActionCallback(callback: AdaptiveCustomAction) {
        customActionCallback = callback
        adaptiveTagFragment?.setAdaptiveCustomActionCallback(callback)
    }
}