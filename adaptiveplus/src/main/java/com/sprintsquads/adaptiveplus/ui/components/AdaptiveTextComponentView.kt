package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.AdaptiveTextComponent
import kotlinx.android.synthetic.main.ap_component_text.view.*


internal class AdaptiveTextComponentView : AdaptiveBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: AdaptiveTextComponent,
    ) : super(context, component)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_text, this)

        (component as? AdaptiveTextComponent)?.run {
            apComponentTextView.text = value
        }
    }
}