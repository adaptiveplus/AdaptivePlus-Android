package com.sprintsquads.adaptiveplus.ext

import android.view.View


internal fun View.show() {
    visibility = View.VISIBLE
}

internal fun View.hide() {
    visibility = View.GONE
}