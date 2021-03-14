package com.sprintsquads.adaptiveplus.extensions

import android.content.Context
import android.widget.Toast


internal fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}