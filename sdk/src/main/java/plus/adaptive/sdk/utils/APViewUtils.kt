package plus.adaptive.sdk.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import androidx.core.view.ViewCompat
import plus.adaptive.sdk.data.models.*
import plus.adaptive.sdk.data.models.actions.APShowStoryAction
import plus.adaptive.sdk.data.models.components.*
import plus.adaptive.sdk.data.models.story.APOuterStyles
import plus.adaptive.sdk.data.models.story.APTemplateDataModel
import plus.adaptive.sdk.ui.apview.StoriesAdapter
import plus.adaptive.sdk.ui.apview.newVm.CampaignViewModel
import plus.adaptive.sdk.ui.apview.vm.APEntryPointViewModel
import plus.adaptive.sdk.ui.components.background.APBackgroundComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModelProvider
import plus.adaptive.sdk.ui.components.gif.APGIFComponentView
import plus.adaptive.sdk.ui.components.image.APImageComponentView
import plus.adaptive.sdk.ui.components.poll.APMultipleChoicePollComponentView
import plus.adaptive.sdk.ui.components.poll.APYesNoPollComponentView
import plus.adaptive.sdk.ui.components.story.StoryCircleComponentView
import plus.adaptive.sdk.ui.components.story.StoryComponentView
import plus.adaptive.sdk.ui.components.story.StorySquereComponentView
import plus.adaptive.sdk.ui.components.text.APTextComponentView
import plus.adaptive.sdk.ui.splashscreen.vm.APSplashScreenDialogViewModel
import plus.adaptive.sdk.ui.stories.actionarea.APActionAreaButtonView
import plus.adaptive.sdk.ui.stories.actionarea.APActionAreaListener
import plus.adaptive.sdk.ui.stories.vm.APSnapViewModel


internal fun buildComponentView(
    context: Context,
    layer: APLayer,
    viewModel: APComponentViewModel?
): View? {
    return when (layer.component) {
        is APBackgroundComponent -> APBackgroundComponentView(context, layer.component, viewModel)
        is APImageComponent -> APImageComponentView(context, layer.component, viewModel)
        is APTextComponent -> APTextComponentView(context, layer.component, viewModel)
        is APGIFComponent -> APGIFComponentView(context, layer.component, viewModel)
        is APPollComponent -> {
            when (layer.component.type) {
                APPollComponent.Type.YES_NO_POLL ->
                    APYesNoPollComponentView(context, layer.component, viewModel)
                APPollComponent.Type.MULTIPLE_CHOICE_POLL ->
                    APMultipleChoicePollComponentView(context, layer.component, viewModel)
            }
        }
        else -> null
    }
}

internal fun drawStoryOnLayout(
    layout: ConstraintLayout,
    component: StoriesAdapter.StoryComponent,
    componentViewModelProvider: APComponentViewModelProvider?,
    isSquere: Boolean = false
){
    layout.removeAllViews()
    component.let {
        val viewModel = componentViewModelProvider?.getAPComponentViewModel(0)
        val language = getLanguageFromViewModel(componentViewModelProvider)
        component.outerText.locale = language
        if(isSquere){
            StorySquereComponentView(layout.context, component, viewModel)?.let { componentView ->
                componentView.id = ViewCompat.generateViewId()
                layout.addView(componentView)
                val componentConstraintSet = ConstraintSet()
                componentConstraintSet.clone(layout)
                componentConstraintSet.constrainWidth(
                    componentView.id, component.outerStyles.width.toInt())
                componentConstraintSet.constrainHeight(
                    componentView.id, component.outerStyles.height.toInt())
                componentConstraintSet.applyTo(layout)
            }
        } else {
            StoryComponentView(layout.context, component, viewModel)?.let { componentView ->
                componentView.id = ViewCompat.generateViewId()
                layout.addView(componentView)
                val componentConstraintSet = ConstraintSet()
                componentConstraintSet.clone(layout)
                componentConstraintSet.constrainWidth(
                    componentView.id, component.outerStyles.width.toInt())
                componentConstraintSet.constrainHeight(
                    componentView.id, component.outerStyles.height.toInt())
                componentConstraintSet.applyTo(layout)
            }
        }
    }
}

private fun getLanguageFromViewModel(viewModel: APComponentViewModelProvider?): String? {
    return when (viewModel){
        is APEntryPointViewModel -> viewModel.getLang()
        is CampaignViewModel -> viewModel.getLang()
        is APSnapViewModel -> viewModel.getLang()
        is APSplashScreenDialogViewModel -> viewModel.getLang()
        else -> null
    }
}

internal fun drawCircleStoryOnLayout(
    layout: ConstraintLayout,
    component: StoriesAdapter.StoryComponent,
    componentViewModelProvider: APComponentViewModelProvider?
){
    layout.removeAllViews()
    component.let {
        val viewModel = componentViewModelProvider?.getAPComponentViewModel(0)
        val language = getLanguageFromViewModel(componentViewModelProvider)
        component.outerText.locale = language
        var imgViewId = 0
        StoryCircleComponentView(layout.context, component, viewModel)?.let { componentView ->
            componentView.id = ViewCompat.generateViewId()
            imgViewId = componentView.id
            layout.addView(componentView)
            val componentConstraintSet = ConstraintSet()
            componentConstraintSet.clone(layout)
            componentConstraintSet.constrainWidth(
                componentView.id, component.outerStyles.width.toInt())
            componentConstraintSet.constrainHeight(
                componentView.id, component.outerStyles.height.toInt())
            componentConstraintSet.applyTo(layout)
        }
        val textSize = when(component.outerStyles.outerSize){
            APOuterStyles.OuterSize.S -> StorySizeConst.TEXT_SIZE_S
            APOuterStyles.OuterSize.M -> StorySizeConst.TEXT_SIZE_M
            APOuterStyles.OuterSize.L -> StorySizeConst.TEXT_SIZE_L
            else -> 10.0
        }
        val storyTextComponent = APTextComponent(
            font = APFont(
                family = "Roboto",
                style = APFont.Style.BOLD,
                size = textSize.toDouble(),
                color = "#000000",
                align = APFont.Align.CENTER,
                letterSpacing = 0.0,
                lineHeight = null),
            value = component.outerText
        )
        val margin = when(component.outerStyles.outerSize){
            APOuterStyles.OuterSize.S -> StorySizeConst.STORY_PADDING_TEXT_S
            APOuterStyles.OuterSize.M -> StorySizeConst.STORY_PADDING_TEXT_M
            APOuterStyles.OuterSize.L -> StorySizeConst.STORY_PADDING_TEXT_L
            else -> -66
        }
        APTextComponentView(layout.context, storyTextComponent, viewModel)?.let { componentTextView ->
            componentTextView.id = ViewCompat.generateViewId()
            layout.addView(componentTextView)
            val textComponentConstraintSet = ConstraintSet()
            textComponentConstraintSet.clone(layout)
            textComponentConstraintSet.constrainWidth(
                componentTextView.id, component.outerStyles.width.toInt())
            textComponentConstraintSet.constrainHeight(
                componentTextView.id, component.outerStyles.height.toInt())
            textComponentConstraintSet.connect(
                componentTextView.id, ConstraintSet.BOTTOM,
                imgViewId, ConstraintSet.BOTTOM, margin
            )
            textComponentConstraintSet.applyTo(layout)
        }
    }
}

internal fun drawAPLayersOnLayout(
    layout: ConstraintLayout,
    layers: List<APLayer>,
    componentViewModelProvider: APComponentViewModelProvider?
) {
    layout.removeAllViews()

    layers.forEachIndexed { index, layer ->
        val componentViewModel = componentViewModelProvider?.getAPComponentViewModel(index)
        val language = getLanguageFromViewModel(componentViewModelProvider)
        if(layer.component is APTextComponent){
            layer.component.value.locale = language
        }
        buildComponentView(layout.context, layer, componentViewModel)?.let { componentView ->
            componentView.id = ViewCompat.generateViewId()

            layout.addView(componentView)

            val componentConstraintSet = ConstraintSet()
            componentConstraintSet.clone(layout)
            componentConstraintSet.connect(
                componentView.id, ConstraintSet.START,
                layout.id, ConstraintSet.START,
                layer.options.position.x.toInt()
            )
            componentConstraintSet.connect(
                componentView.id, ConstraintSet.TOP,
                layout.id, ConstraintSet.TOP,
                layer.options.position.y.toInt()
            )
            componentConstraintSet.constrainWidth(
                componentView.id, layer.options.position.width.toInt())

            if (layer.type == APLayer.Type.POLL) {
                componentConstraintSet.constrainHeight(
                    componentView.id, WRAP_CONTENT)
            } else {
                componentConstraintSet.constrainHeight(
                    componentView.id, layer.options.position.height.toInt())
            }

            componentConstraintSet.setAlpha(
                componentView.id, layer.options.opacity.toFloat())
            componentConstraintSet.setRotation(
                componentView.id, layer.options.position.angle.toFloat())
            componentConstraintSet.applyTo(layout)
        }
    }
}

internal fun isAPCarouselViewDataModelNullOrEmpty(dataModel: APCarouselViewDataModel?): Boolean {
    return dataModel?.entryPoints.isNullOrEmpty()
}

internal fun isStoriesDataModelNullOrEmpty(dataModel: APTemplateDataModel?): Boolean {
    return dataModel?.campaigns.isNullOrEmpty()
}

internal fun getAPStoriesList(dataModel: APCarouselViewDataModel?) : List<APStory>? {
    return dataModel?.run {
        val stories = mutableListOf<APStory>()
        entryPoints.forEach { entryPoint ->
            entryPoint.actions.forEach { action ->
                if (action is APShowStoryAction) {
                    stories.add(action.story)
                }
            }
        }
        stories
    }
}

internal fun getAPStoriesList(dataModel: APTemplateDataModel?) : List<APStory>? {
    return dataModel?.run {
        val stories = mutableListOf<APStory>()
        campaigns.forEach { campaign ->
            campaign.body.story?.let {
                stories.add(
                    APStory(
                        id = it.id,
                        campaignId = campaign.id,
                        getAPSnapFromSnap(it.body.snaps)
                    )
                )
            }
        }
        stories
    }
}

internal fun buildActionAreaView(
    context: Context,
    actionArea: APSnap.ActionArea,
    actionAreaListener: APActionAreaListener?
): View? {
    return when (actionArea) {
        is APSnap.ButtonActionArea ->
            APActionAreaButtonView(context, actionArea, actionAreaListener)
        else -> null
    }
}

internal fun drawAPSnapActionArea(
    layout: ConstraintLayout,
    actionArea: APSnap.ActionArea,
    actionAreaListener: APActionAreaListener?
) {
    layout.removeAllViews()

    buildActionAreaView(layout.context, actionArea, actionAreaListener)?.let { actionAreaView ->
        actionAreaView.id = ViewCompat.generateViewId()
        layout.addView(actionAreaView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        when (actionArea) {
            is APSnap.ButtonActionArea -> {
                constraintSet.connect(
                    actionAreaView.id, ConstraintSet.START, layout.id, ConstraintSet.START)
                constraintSet.connect(
                    actionAreaView.id, ConstraintSet.END, layout.id, ConstraintSet.END)
                constraintSet.connect(
                    actionAreaView.id, ConstraintSet.BOTTOM,
                    layout.id, ConstraintSet.BOTTOM)
                constraintSet.connect(
                    actionAreaView.id, ConstraintSet.TOP,
                    layout.id, ConstraintSet.TOP)
            }
        }

        constraintSet.applyTo(layout)
    }
}