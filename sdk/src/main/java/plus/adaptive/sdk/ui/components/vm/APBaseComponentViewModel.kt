package plus.adaptive.sdk.ui.components.vm

import plus.adaptive.sdk.ui.components.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.APComponentViewController


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