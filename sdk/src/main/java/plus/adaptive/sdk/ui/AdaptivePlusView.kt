package plus.adaptive.sdk.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.listeners.APCustomActionListener
import plus.adaptive.sdk.ui.apview.APViewFragment


class AdaptivePlusView : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.AdaptivePlusView)
        val apViewId = arr.getString(R.styleable.AdaptivePlusView_apViewId) ?: ""
        val apHasDrafts = arr.getBoolean(R.styleable.AdaptivePlusView_apHasDrafts, false)

        init(apViewId, apHasDrafts)

        arr.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.AdaptivePlusView, defStyleAttr, 0)
        val apViewId = arr.getString(R.styleable.AdaptivePlusView_apViewId) ?: ""
        val apHasDrafts = arr.getBoolean(R.styleable.AdaptivePlusView_apHasDrafts, false)

        init(apViewId, apHasDrafts)

        arr.recycle()
    }


    private lateinit var apViewId: String
    private var apHasDrafts: Boolean = false

    private var apViewFragment: APViewFragment? = null
    private var apCustomActionListener: APCustomActionListener? = null


    /**
     * Setter of adaptive plus view id
     *
     * @param apViewId - id of adaptive plus view
     */
    fun setAdaptivePlusViewId(apViewId: String) {
        this.apViewId = apViewId
        apViewFragment?.setAPViewId(apViewId)
    }

    /**
     * Setter of has AdaptivePlusView drafts or not
     *
     * @param hasDrafts - true if draft campaigns should be also shown
     */
    fun setHasDrafts(hasDrafts: Boolean) {
        this.apHasDrafts = hasDrafts
        apViewFragment?.setHasDrafts(hasDrafts)
    }

    private fun init(
        apViewId: String = "",
        apHasDrafts: Boolean = false
    ) {
        this.apViewId = apViewId
        this.apHasDrafts = apHasDrafts
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        getFragmentManager()?.let { fragmentManager ->
            if (fragmentManager.findFragmentById(id) == null) {
                apViewFragment =
                    APViewFragment.newInstance(
                        apViewId = apViewId,
                        apHasDrafts = apHasDrafts
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

            apCustomActionListener?.let {
                apViewFragment?.setAPCustomActionListener(it)
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
     * Setter of adaptive plus custom action listener
     *
     * @param listener - adaptive plus custom action listener
     * @see APCustomActionListener
     */
    fun setAPCustomActionListener(listener: APCustomActionListener) {
        this.apCustomActionListener = listener
        apViewFragment?.setAPCustomActionListener(listener)
    }

    /**
     * Method to scroll to the start of entry point list
     */
    fun scrollToStart() {
        apViewFragment?.scrollToStart()
    }
}