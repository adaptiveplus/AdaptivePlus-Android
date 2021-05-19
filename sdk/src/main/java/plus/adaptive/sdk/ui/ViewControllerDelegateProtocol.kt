package plus.adaptive.sdk.ui

import android.content.Intent
import androidx.fragment.app.DialogFragment


internal interface ViewControllerDelegateProtocol {

    fun showDialog(dialogFragment: DialogFragment)

    fun startActivity(intent: Intent)

    fun dismissAllDialogs()

}