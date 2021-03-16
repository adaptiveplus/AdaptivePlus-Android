package com.sprintsquads.adaptiveplus.sdk.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction
import com.sprintsquads.adaptiveplus.ui.apview.APViewFragment


class AdaptivePlusView : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.AdaptivePlusView)
        val apViewId = arr.getString(R.styleable.AdaptivePlusView_apViewId) ?: ""

        init(apViewId)

        arr.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.AdaptivePlusView, defStyleAttr, 0)
        val apViewId = arr.getString(R.styleable.AdaptivePlusView_apViewId) ?: ""

        init(apViewId)

        arr.recycle()
    }


    private lateinit var apViewId: String

    private var apViewFragment: APViewFragment? = null
    private var apCustomAction: APCustomAction? = null


    /**
     * Setter of adaptive plus view id
     *
     * @param apViewId - id of adaptive plus view
     */
    fun setAdaptivePlusViewId(apViewId: String) {
        this.apViewId = apViewId
        apViewFragment?.setAPViewId(apViewId)
    }

    private fun init(
        apViewId: String = ""
    ) {
        this.apViewId = apViewId
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        getFragmentManager()?.let { fragmentManager ->
            if (fragmentManager.findFragmentById(id) == null) {
                apViewFragment =
                    APViewFragment.newInstance(
                        apViewId = apViewId
                    )
                apViewFragment?.let {
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
                    apViewFragment = it as? APViewFragment
                }
            }

            apCustomAction?.let { callback ->
                apViewFragment?.setAPCustomAction(callback)
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
        apViewFragment?.refresh()
    }

    /**
     * Setter of adaptive plus custom action
     *
     * @param apCustomAction - adaptive plus custom action
     * @see APCustomAction
     */
    fun setAPCustomAction(apCustomAction: APCustomAction) {
        this.apCustomAction = apCustomAction
        apViewFragment?.setAPCustomAction(apCustomAction)
    }
}