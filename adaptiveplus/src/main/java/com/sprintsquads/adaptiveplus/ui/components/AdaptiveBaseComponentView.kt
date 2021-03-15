package com.sprintsquads.adaptiveplus.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sprintsquads.adaptiveplus.data.models.components.AdaptiveComponent


internal abstract class AdaptiveBaseComponentView : LinearLayout {

    protected var component: AdaptiveComponent? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: AdaptiveComponent
    ) : super(context) {
        this.component = component
        this.initElement()
    }

    /**
     * Method called on the initialization of element
     */
    protected abstract fun initElement()
}