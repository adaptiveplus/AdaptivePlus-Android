package com.sprintsquads.adaptiveplus.ui.apview

import android.content.Intent
import androidx.fragment.app.DialogFragment


internal interface APViewDelegateProtocol {

    fun showDialog(dialogFragment: DialogFragment)

    fun startActivity(intent: Intent)

    fun dismissAllDialogs()

}