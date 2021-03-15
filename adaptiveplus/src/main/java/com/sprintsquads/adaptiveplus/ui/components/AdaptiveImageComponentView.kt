package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.AdaptiveImageComponent
import com.sprintsquads.adaptiveplus.extensions.loadImage
import kotlinx.android.synthetic.main.ap_component_image.view.*


internal class AdaptiveImageComponentView : AdaptiveBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: AdaptiveImageComponent,
    ) : super(context, component)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_image, this)

        (component as? AdaptiveImageComponent)?.run {
            apComponentImageView.loadImage(url)
        }
    }
}