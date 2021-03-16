package com.sprintsquads.adaptiveplus.ui.stories.data


internal data class APSnapStatus(
    val snapId: String,
    val state: State
) {
    enum class State {
        RESET, PAUSED, RESUMED
    }
}