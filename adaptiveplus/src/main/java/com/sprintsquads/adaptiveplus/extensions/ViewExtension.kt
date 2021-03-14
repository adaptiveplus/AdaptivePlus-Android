package com.sprintsquads.adaptiveplus.extensions

import android.view.View


internal fun View.show() {
    visibility = View.VISIBLE
}

internal fun View.hide() {
    visibility = View.GONE
}