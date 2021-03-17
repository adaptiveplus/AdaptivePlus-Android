package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.APImageComponent
import com.sprintsquads.adaptiveplus.extensions.loadImage
import com.sprintsquads.adaptiveplus.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_component_image.view.*


internal class APImageComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APImageComponent,
    ) : super(context, component)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_image, this)

        (component as? APImageComponent)?.run {
            apComponentImageView.loadImage(url, cornerRadius = cornerRadius?.toInt())

            border?.let {
                val constraintSet = ConstraintSet()
                constraintSet.clone(apComponentLayout)
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.START, it.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.END, it.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.TOP, it.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.BOTTOM, it.padding.toInt())
                constraintSet.applyTo(apComponentLayout)

                val borderDrawable = GradientDrawable().apply {
                    setStroke(it.width.toInt(), getColorFromHex(it.activeColor.startColor))
                    cornerRadius = it.cornerRadius.toFloat()
                }
                apComponentBorderView.background = borderDrawable
            }
        }
    }
}