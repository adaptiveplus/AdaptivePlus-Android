package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.AdaptiveBackgroundComponent
import com.sprintsquads.adaptiveplus.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_component_background.view.*


internal class AdaptiveBackgroundComponentView : AdaptiveBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: AdaptiveBackgroundComponent,
    ) : super(context, component)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_background, this)

        (component as? AdaptiveBackgroundComponent)?.run {
            apComponentView.setBackgroundColor(getColorFromHex(color))
        }
    }
}