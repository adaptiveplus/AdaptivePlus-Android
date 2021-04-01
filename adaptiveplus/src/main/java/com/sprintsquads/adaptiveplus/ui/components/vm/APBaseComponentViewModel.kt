package com.sprintsquads.adaptiveplus.ui.components.vm

import com.sprintsquads.adaptiveplus.ui.components.APComponentLifecycleListener
import com.sprintsquads.adaptiveplus.ui.components.APComponentViewController


internal abstract class APBaseComponentViewModel(
    protected val lifecycleListener: APComponentLifecycleListener
) : APComponentViewModel {

    private var componentViewController: APComponentViewController? = null


    fun setComponentViewController(
        controller: APComponentViewController?
    ) {
        this.componentViewController = controller
    }
}