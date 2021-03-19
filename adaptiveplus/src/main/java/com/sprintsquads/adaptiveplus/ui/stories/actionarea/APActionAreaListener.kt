package com.sprintsquads.adaptiveplus.ui.stories.actionarea

import com.sprintsquads.adaptiveplus.data.models.APAction


internal interface APActionAreaListener {
    fun runActions(actions: List<APAction>)
}