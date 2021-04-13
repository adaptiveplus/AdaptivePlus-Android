package com.sprintsquads.adaptiveplus.ui.components.vm

import com.sprintsquads.adaptiveplus.ui.components.APComponentContainerViewModel
import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener
import com.sprintsquads.adaptiveplus.ui.components.APComponentViewController


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