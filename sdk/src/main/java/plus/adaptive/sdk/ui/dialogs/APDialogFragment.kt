package plus.adaptive.sdk.ui.dialogs


internal interface APDialogFragment {

    fun interface OnDismissListener {
        fun onDismiss()
    }


    fun addOnDismissListener(listener: OnDismissListener)

    fun removeOnDismissListener(listener: OnDismissListener)

    fun clearAllOnDismissListeners()
}