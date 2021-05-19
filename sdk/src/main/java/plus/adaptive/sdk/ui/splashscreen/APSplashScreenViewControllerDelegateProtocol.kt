package plus.adaptive.sdk.ui.splashscreen

import plus.adaptive.sdk.data.models.actions.APAction


internal interface APSplashScreenViewControllerDelegateProtocol {
    /**
     * Method to run/execute adaptive plus actions
     *
     * @param actions - list of adaptive plus actions to execute
     * @see APAction
     */
    fun runActions(actions: List<APAction>)
}