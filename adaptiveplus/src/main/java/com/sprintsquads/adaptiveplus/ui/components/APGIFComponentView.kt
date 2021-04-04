package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.APGIFComponent
import com.sprintsquads.adaptiveplus.extensions.loadGIF
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APGIFComponentViewModel
import com.sprintsquads.adaptiveplus.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_component_gif.view.*


internal class APGIFComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APGIFComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_gif, this)

        (component as? APGIFComponent)?.run {
            apComponentImageView.loadGIF(
                url,
                cornerRadius = cornerRadius?.toInt(),
                onResourceReady = {
                    (componentViewModel as? APGIFComponentViewModel)?.onImageResourceReady()
                },
                onLoadFailed = {
                    (componentViewModel as? APGIFComponentViewModel)?.onImageLoadFailed()
                }
            )

            updateImageBorder()
        }
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {
        updateImageBorder()
    }

    private fun updateImageBorder() {
        (component as? APGIFComponent)?.run {
            border?.let {
                val borderState =
                    if ((componentViewModel as? APGIFComponentViewModel)?.isActive() == false) {
                        it.inactive
                    } else {
                        it.active
                    }

                val constraintSet = ConstraintSet()
                constraintSet.clone(apComponentLayout)
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.START, borderState.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.END, borderState.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.TOP, borderState.padding.toInt())
                constraintSet.setMargin(apComponentImageView.id, ConstraintSet.BOTTOM, borderState.padding.toInt())
                constraintSet.applyTo(apComponentLayout)

                val borderDrawable = GradientDrawable().apply {
                    setStroke(borderState.width.toInt(), getColorFromHex(borderState.color.startColor))
                    cornerRadius = borderState.cornerRadius.toFloat()
                }
                apComponentBorderView.background = borderDrawable
            }
        }
    }
}