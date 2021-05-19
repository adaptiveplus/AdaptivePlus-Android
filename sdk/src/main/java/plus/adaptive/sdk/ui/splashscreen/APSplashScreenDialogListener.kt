package plus.adaptive.sdk.ui.splashscreen

import plus.adaptive.sdk.data.models.actions.APAction


internal interface APSplashScreenDialogListener {
    /**
     * Callback to run/execute adaptive plus actions
     *
     * @param actions - list of adaptive plus actions to execute
     * @see APAction
     */
    fun onRunActions(actions: List<APAction>)

    /**
     * Callback called on splash screen dialog dismissed
     */
    fun onSplashScreenDialogDismissed()
}