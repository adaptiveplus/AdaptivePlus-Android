package com.sprintsquads.adaptiveplus.ui.stories.data


internal data class APSnapEvent(
    val snapId: String,
    val event: Type
) {
    enum class Type {
        IS_UNDER_TOUCH,
        IS_NOT_UNDER_TOUCH,
        GO_TO_PREV_SNAP,
        GO_TO_NEXT_SNAP,
        CLOSE_STORIES,
        RESET_AND_PAUSE_SNAP_PROGRESS,
        NONE
    }
}