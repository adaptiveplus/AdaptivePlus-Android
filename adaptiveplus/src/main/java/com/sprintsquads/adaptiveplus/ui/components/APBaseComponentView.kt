package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sprintsquads.adaptiveplus.data.models.components.APComponent
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel


internal abstract class APBaseComponentView : LinearLayout {

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
        this.initElement()
    }

    /**
     * Method called on the initialization of element
     */
    protected abstract fun initElement()
}