package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.APImageComponent
import com.sprintsquads.adaptiveplus.extensions.loadImage
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_component_image.view.*


internal class APImageComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APImageComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_image, this)

        (component as? APImageComponent)?.run {
            apComponentImageView.loadImage(url, cornerRadius = cornerRadius?.toInt())

            border?.let {
                val constraintSet = ConstraintSet()
                constraintSet.clone(apComponentLayout)
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.START, it.active.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.END, it.active.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.TOP, it.active.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.BOTTOM, it.active.padding.toInt())
                constraintSet.applyTo(apComponentLayout)

                val borderDrawable = GradientDrawable().apply {
                    setStroke(it.active.width.toInt(), getColorFromHex(it.active.color.startColor))
                    cornerRadius = it.active.cornerRadius.toFloat()
                }
                apComponentBorderView.background = borderDrawable
            }
        }
    }
}