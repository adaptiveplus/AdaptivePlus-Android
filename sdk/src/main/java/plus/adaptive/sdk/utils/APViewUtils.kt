package plus.adaptive.sdk.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import plus.adaptive.sdk.data.models.APLayer
import plus.adaptive.sdk.data.models.APSnap
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.data.models.APCarouselViewDataModel
import plus.adaptive.sdk.data.models.actions.APShowStoryAction
import plus.adaptive.sdk.data.models.components.APBackgroundComponent
import plus.adaptive.sdk.data.models.components.APGIFComponent
import plus.adaptive.sdk.data.models.components.APImageComponent
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.data.models.components.APTextComponent
import plus.adaptive.sdk.ui.components.background.APBackgroundComponentView
import plus.adaptive.sdk.ui.components.gif.APGIFComponentView
import plus.adaptive.sdk.ui.components.image.APImageComponentView
import plus.adaptive.sdk.ui.components.text.APTextComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModelProvider
import plus.adaptive.sdk.ui.components.poll.APPollComponentView
import plus.adaptive.sdk.ui.stories.actionarea.APActionAreaButtonView
import plus.adaptive.sdk.ui.stories.actionarea.APActionAreaListener


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
        is APPollComponent -> APPollComponentView(context, layer.component, viewModel)
        else -> null
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
            componentConstraintSet.constrainHeight(
                componentView.id, layer.options.position.height.toInt())
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