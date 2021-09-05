package plus.adaptive.sdk.utils

import plus.adaptive.sdk.data.models.APLayer
import plus.adaptive.sdk.data.models.APSnap
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.data.models.actions.*
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.actions.APCallPhoneAction
import plus.adaptive.sdk.data.models.actions.APCustomAction
import plus.adaptive.sdk.data.models.actions.APSendSMSAction
import plus.adaptive.sdk.data.models.actions.APShowStoryAction
import plus.adaptive.sdk.data.models.components.*
import plus.adaptive.sdk.data.models.components.APBackgroundComponent
import plus.adaptive.sdk.data.models.components.APComponent
import plus.adaptive.sdk.data.models.components.APGIFComponent
import plus.adaptive.sdk.data.models.components.APImageComponent
import plus.adaptive.sdk.data.models.components.APTextComponent
import plus.adaptive.sdk.data.models.story.*
import plus.adaptive.sdk.data.models.story.ActionArea
import plus.adaptive.sdk.data.models.story.Campaign
import plus.adaptive.sdk.data.models.story.Layer
import plus.adaptive.sdk.data.models.story.Snap
import plus.adaptive.sdk.data.models.story.Text
import plus.adaptive.sdk.ui.components.poll.APYesNoPollComponentView


internal fun createStoryAction(campaign: Campaign) : APAction?{
    var story:APStory? = null
    campaign.body.story?.let {
        story = APStory(id = it.id, campaignId = it.body.campaignId, getAPSnapFromSnap(it.body.snaps))
    }
    return if(story!=null){
        APShowStoryAction(story!!)
    } else {
        null
    }
}

internal fun getAPSnapFromSnap(snaps: List<Snap>): List<APSnap> {
    val snapList = ArrayList<APSnap>()
    snaps.forEach{ snap ->
            snapList.add(
                APSnap(
                    id = snap.id,
                    width = snap.body.width,
                    height = snap.body.height,
                    actionAreaHeight = snap.body.actionAreaHeight,
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
                component = APTextComponent(
                    layer.component.value!!,
                    layer.component.font
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
                    component = APPollComponent(layer.component.id, this)
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
