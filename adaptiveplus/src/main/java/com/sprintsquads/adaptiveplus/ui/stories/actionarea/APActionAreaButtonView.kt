package com.sprintsquads.adaptiveplus.ui.stories.actionarea

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.APSnap
import com.sprintsquads.adaptiveplus.extensions.applyAPFont
import com.sprintsquads.adaptiveplus.utils.getColorFromHex
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
            apButtonTextView.setOnClickListener {
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
    }
}