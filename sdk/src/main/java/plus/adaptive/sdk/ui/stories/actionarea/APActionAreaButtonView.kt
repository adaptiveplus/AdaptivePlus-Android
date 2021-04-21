package plus.adaptive.sdk.ui.stories.actionarea

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.animation.addListener
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APSnap
import plus.adaptive.sdk.ext.applyAPFont
import plus.adaptive.sdk.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_action_area_button.view.*


internal class APActionAreaButtonView : LinearLayout {

    private var data: APSnap.ButtonActionArea? = null
    private var listener: APActionAreaListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        data: APSnap.ButtonActionArea,
        listener: APActionAreaListener?
    ) : super(context) {
        this.data = data
        this.listener = listener
        this.initElement()
    }

    private fun initElement() {
        View.inflate(context, R.layout.ap_action_area_button, this)

        data?.let { data ->
            setOnClickListener {
                listener?.runActions(data.actions)
            }

            val bgDrawable = GradientDrawable().apply {
                setColor(getColorFromHex(data.backgroundColor))
                cornerRadius = data.cornerRadius?.toFloat() ?: 0f

                data.border?.let { border ->
                    setStroke(border.width.toInt(), getColorFromHex(border.color.startColor))
                }
            }
            apButtonTextView.background = bgDrawable

            apButtonTextView.text = data.text.value
            data.text.font?.let {
                apButtonTextView.applyAPFont(apFont = it)
            }
        }

        val animY1 = ObjectAnimator.ofFloat(apArrowUpImageView, "translationY", -2f, 2f)
        val animY2 = ObjectAnimator.ofFloat(apArrowUpImageView, "translationY", 2f, -2f)
        val animSet = AnimatorSet()
        animSet.playSequentially(animY1, animY2)
        animSet.duration = 500
        animSet.interpolator = AccelerateDecelerateInterpolator()
        animSet.addListener(onEnd = { animSet.start() })
        animSet.start()
    }
}