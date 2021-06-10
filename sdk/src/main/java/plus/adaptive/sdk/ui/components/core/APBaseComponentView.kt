package plus.adaptive.sdk.ui.components.core

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import plus.adaptive.sdk.data.models.components.APComponent
import plus.adaptive.sdk.ui.components.core.vm.APBaseComponentViewModel
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel


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
        bindViewToViewModel(doBind = true)
    }

    override fun onDetachedFromWindow() {
        bindViewToViewModel(doBind = false)
        super.onDetachedFromWindow()
    }

    private fun bindViewToViewModel(doBind: Boolean) {
        val viewController: APComponentViewController? = if (doBind) this else null
        val viewModel = componentViewModel as? APBaseComponentViewModel
        viewModel?.setComponentViewController(viewController)
    }

    protected abstract fun initElement()
}