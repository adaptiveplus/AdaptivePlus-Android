package plus.adaptive.sdk.ui.stories.actionarea

import plus.adaptive.sdk.data.models.actions.APAction


internal interface APActionAreaListener {
    fun runActions(actions: List<APAction>)
}