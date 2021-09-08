package plus.adaptive.sdk.utils

import plus.adaptive.sdk.data.models.APFont
import plus.adaptive.sdk.data.models.APLayer
import plus.adaptive.sdk.data.models.APSnap
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.data.models.actions.*
import plus.adaptive.sdk.data.models.components.*
import plus.adaptive.sdk.data.models.story.*


internal fun createStoryAction(campaign: Campaign) : APAction?{
    var story:APStory? = null
    campaign.body.story?.let {
        story = createAPStoryFromStory(it)
    }
    return if(story!=null){
        APShowStoryAction(story!!)
    } else {
        null
    }
}

internal fun createAPStoryFromStory(story: Story): APStory{
    return APStory(id = story.id, campaignId = story.body.campaignId, getAPSnapFromSnap(story.body.snaps))
}

internal fun getAPSnapFromSnap(snaps: List<Snap>): List<APSnap> {
    val snapList = ArrayList<APSnap>()
    snaps.forEach{ snap ->
            snapList.add(
                APSnap(
                    id = snap.id,
                    width = snap.body.width,
                    height = snap.body.height,
                    actionAreaHeight = snap.body.actionAreaHeight ,
                    actionArea = toAPActionArea(snap.body.actionArea),
                    showTime = snap.body.showTime,
                    layers = getAPLayersFromLayers(snap.body.layers)
                )
            )
    }
    return snapList
}

private fun getAPLayersFromLayers(layers: List<Layer>): List<APLayer> {
    val layerList = ArrayList<APLayer>()
    layers.forEach{ layer ->
            layerList.add(APLayer(
                type = layer.type,
                options = layer.options,
                component = createComponent(layer)
            )
        )
    }
    return layerList
}

private fun createComponent(layer: Layer): APComponent? {
    var component: APComponent? = null
    layer.component?.run {
        when(layer.type){
            APLayer.Type.BACKGROUND -> {
                component = APBackgroundComponent(layer.component.color!!)
            }
            APLayer.Type.IMAGE -> {
                component = APImageComponent(
                    url = layer.component.url!!,
                    border = null,
                    cornerRadius,
                    loadingColor
                )
            }
            APLayer.Type.GIF -> {
                component = APGIFComponent(
                    url = layer.component.url!!,
                    border = null,
                    cornerRadius,
                    loadingColor
                )
            }
            APLayer.Type.TEXT -> {
                var bigFont = font?.let {
                    APFont(
                        family = it.family,
                        style = it.style,
                        size = it.size,
                        color = it.color,
                        align = it.align,
                        letterSpacing = it.letterSpacing,
                        lineHeight = it.lineHeight
                    )
                } ?: layer.component.font

                component = APTextComponent(
                    layer.component.value!!,
                    bigFont
                )
            }
            APLayer.Type.BUTTON -> {
                component = APButtonComponent(
                    cornerRadius = cornerRadius!!,
                    backgroundColor = color!!,
                    text = getText(text!!),
                    actions = listOf()
                )
            }
            APLayer.Type.POLL -> {
                layer.component.type?.apply {
                    layer.component.id?.let {
                        component = APPollComponent(it, this)
                    }
                }
            }
        }
    }
    return component
}

private fun toAPActionArea(actionArea: ActionArea?): APSnap.ActionArea? {
    var area : APSnap.ButtonActionArea? = null
    actionArea?.body?.let {
        area = APSnap.ButtonActionArea(
            text = getText(it.text),
            actions = getActions(it.actions),
            border = it.border,
            cornerRadius = it.cornerRadius,
            backgroundColor = it.backgroundColor,
        )
    }
    return area
}

private fun getActions(actions: List<Action>): List<APAction> {
    val newAPActions = ArrayList<APAction>()
    actions.forEach{action ->
        when(action.type){
            Action.Type.CALL -> {
                newAPActions.add(createCallAction(action))
            }
            Action.Type.SEND_SMS -> {
                newAPActions.add(createSMSAction(action))
            }
            Action.Type.SHOW_STORY -> {
//                newAPActions.add(createShowStoryAction(action))
            }
            Action.Type.CUSTOM -> {
                newAPActions.add(createCustomAction(action))
            }
            Action.Type.OPEN_WEB_LINK -> {
                newAPActions.add(createWebLinkAction(action))
            }
            else -> {}
        }
    }
    return newAPActions
}

private fun createCustomAction(action: Action): APCustomAction {
    return APCustomAction(action.parameters)
}

private fun createWebLinkAction(action: Action): APOpenWebLinkAction {
    return APOpenWebLinkAction(
        action.parameters!!["url"].toString(),
        action.parameters["isWebView"].toString().toBoolean()
    )
}

//private fun createShowStoryAction(action: Action): APAction? {
//    return createStoryAction(action)
//}

private fun createSMSAction(action: Action): APSendSMSAction {
    return APSendSMSAction(
        action.parameters!!["phoneNumber"].toString(),
        action.parameters["message"].toString()
    )
}

private fun createCallAction(action: Action): APCallPhoneAction {
    return APCallPhoneAction(action.parameters!!["phoneNumber"].toString())
}

private fun getText(text: Text): APSnap.ButtonActionArea.Text {
    return APSnap.ButtonActionArea.Text(text.value.RU, text.font)
}
