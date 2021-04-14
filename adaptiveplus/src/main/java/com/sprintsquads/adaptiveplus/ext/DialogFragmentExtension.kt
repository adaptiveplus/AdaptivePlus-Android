package com.sprintsquads.adaptiveplus.ext

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Extension function that displays DialogFragment instance
 *
 * @param fragmentManager - FragmentManager instance
 */
internal fun DialogFragment.show(fragmentManager: FragmentManager) {
    if (fragmentManager.findFragmentByTag(this::javaClass.name) == null) {
        this.show(fragmentManager, this::javaClass.name)
    }
}