package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.models.components.APBackgroundComponent
import com.sprintsquads.adaptiveplus.ui.components.vm.APBaseComponentViewModel
import com.sprintsquads.adaptiveplus.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_component_background.view.*


internal class APBackgroundComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APBackgroundComponent,
        componentViewModel: APBaseComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_background, this)

        (component as? APBackgroundComponent)?.run {
            apComponentView.setBackgroundColor(getColorFromHex(color))
        }
    }
}