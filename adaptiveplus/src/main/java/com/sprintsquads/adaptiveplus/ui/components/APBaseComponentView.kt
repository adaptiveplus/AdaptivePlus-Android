package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sprintsquads.adaptiveplus.data.models.components.APComponent
import com.sprintsquads.adaptiveplus.ui.components.vm.APBaseComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel


internal abstract class APBaseComponentView : LinearLayout, APComponentViewController {

    protected var component: APComponent? = null
    protected var componentViewModel: APComponentViewModel? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context) {
        this.component = component
        this.componentViewModel = componentViewModel

        componentViewModel?.prepare()
        this.initElement()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (componentViewModel as? APBaseComponentViewModel)?.setComponentViewController(this)
    }

    override fun onDetachedFromWindow() {
        (componentViewModel as? APBaseComponentViewModel)?.setComponentViewController(null)
        super.onDetachedFromWindow()
    }

    protected abstract fun initElement()
}