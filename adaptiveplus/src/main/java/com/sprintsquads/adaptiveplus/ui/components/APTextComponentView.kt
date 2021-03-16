package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.APTextComponent
import com.sprintsquads.adaptiveplus.extensions.applyAPFont
import kotlinx.android.synthetic.main.ap_component_text.view.*


internal class APTextComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APTextComponent,
    ) : super(context, component)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_text, this)

        (component as? APTextComponent)?.run {
            apComponentTextView.text = value
            font?.let { apComponentTextView.applyAPFont(it) }
        }
    }
}