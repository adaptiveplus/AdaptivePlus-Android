package plus.adaptive.sdk.ui.components.core.vm

import plus.adaptive.sdk.ui.components.core.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.core.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.core.APComponentViewController


internal abstract class APBaseComponentViewModel(
    protected val containerViewModel: APComponentContainerViewModel,
    protected val lifecycleListener: APComponentLifecycleListener
) : APComponentViewModel {

    protected var mComponentViewController: APComponentViewController? = null


    fun setComponentViewController(
        controller: APComponentViewController?
    ) {
        this.mComponentViewController = controller
    }

    override fun hasPreparationProgressUpdates(): Boolean = false
}