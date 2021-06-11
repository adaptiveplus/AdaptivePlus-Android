package plus.adaptive.sdk.ui.components.poll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel


internal class APPollComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APPollComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        if ((component as? APPollComponent)?.type == APPollComponent.Type.YES_NO_POLL) {
            View.inflate(context, R.layout.ap_component_yes_no_poll, this)
        } else {
            View.inflate(context, R.layout.ap_component_multiple_choice_poll, this)
        }
    }

    override fun prepare() {}

    override fun resume() {}

    override fun pause() {}

    override fun reset() {}
}