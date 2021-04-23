package plus.adaptive.sdk.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.components.APBackgroundComponent
import plus.adaptive.sdk.ui.components.vm.APComponentViewModel
import plus.adaptive.sdk.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_component_background.view.*


internal class APBackgroundComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APBackgroundComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_background, this)

        (component as? APBackgroundComponent)?.run {
            getColorFromHex(color)?.let { apComponentView.setBackgroundColor(it) }
        }
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {}
}