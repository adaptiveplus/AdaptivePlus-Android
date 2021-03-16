package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.view.setPadding
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
                apComponentLayout.setPadding(it.padding.toInt())

                val borderDrawable = GradientDrawable().apply {
                    setStroke(it.width.toInt(), getColorFromHex(it.activeColor.startColor))
                    cornerRadius = it.cornerRadius.toFloat()
                }
                apComponentLayout.background = borderDrawable
            }
        }
    }
}