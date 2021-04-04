package com.sprintsquads.adaptiveplus.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.models.APSnap
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.components.APBackgroundComponent
import com.sprintsquads.adaptiveplus.data.models.components.APGIFComponent
import com.sprintsquads.adaptiveplus.data.models.components.APImageComponent
import com.sprintsquads.adaptiveplus.data.models.components.APTextComponent
import com.sprintsquads.adaptiveplus.ui.components.APBackgroundComponentView
import com.sprintsquads.adaptiveplus.ui.components.APGIFComponentView
import com.sprintsquads.adaptiveplus.ui.components.APImageComponentView
import com.sprintsquads.adaptiveplus.ui.components.APTextComponentView
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider
import com.sprintsquads.adaptiveplus.ui.stories.actionarea.APActionAreaButtonView
import com.sprintsquads.adaptiveplus.ui.stories.actionarea.APActionAreaListener


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
        else -> null
    }
}

internal fun drawAPLayersOnLayout(
    layout: ConstraintLayout,
    layers: List<APLayer>,
    scaleFactor: Float = 1f,
    componentViewModelProvider: APComponentViewModelProvider?
) {
    layout.removeAllViews()

    layers.forEachIndexed { index, layer ->
        val componentViewModel = componentViewModelProvider?.getAPComponentViewModel(index)

        buildComponentView(layout.context, layer, componentViewModel)?.let { componentView ->
            componentView.id = ViewCompat.generateViewId()

            layout.addView(componentView)

            val xDisplacement = layer.options.position.width * (scaleFactor - 1f) / 2
            val yDisplacement = layer.options.position.height * (scaleFactor - 1f) / 2

            val componentConstraintSet = ConstraintSet()
            componentConstraintSet.clone(layout)
            componentConstraintSet.connect(
                componentView.id, ConstraintSet.START,
                layout.id, ConstraintSet.START,
                (layer.options.position.x * scaleFactor + xDisplacement).toInt()
            )
            componentConstraintSet.connect(
                componentView.id, ConstraintSet.TOP,
                layout.id, ConstraintSet.TOP,
                (layer.options.position.y * scaleFactor + yDisplacement).toInt()
            )
            componentConstraintSet.constrainWidth(
                componentView.id, layer.options.position.width.toInt())
            componentConstraintSet.constrainHeight(
                componentView.id, layer.options.position.height.toInt())
            componentConstraintSet.setScaleX(componentView.id, scaleFactor)
            componentConstraintSet.setScaleY(componentView.id, scaleFactor)
            componentConstraintSet.setAlpha(
                componentView.id, layer.options.opacity.toFloat())
            componentConstraintSet.setRotation(
                componentView.id, layer.options.position.angle.toFloat())
            componentConstraintSet.applyTo(layout)
        }
    }
}

internal fun isAPViewDataModelNullOrEmpty(dataModel: APViewDataModel?): Boolean {
    return dataModel?.entryPoints.isNullOrEmpty()
}

internal fun getAPStoriesList(dataModel: APViewDataModel?) : List<APStory>? {
    return dataModel?.run {
        val stories = mutableListOf<APStory>()

        entryPoints.forEach { entryPoint ->
            entryPoint.actions.forEach { action ->
                if (action.type == APAction.Type.SHOW_STORY) {
                    deserializeAPActionParams(action)
                    (action.parameters?.get("story") as? APStory)?.let { story ->
                        stories.add(story)
                    }
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
    scaleFactor: Float = 1f,
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
                actionAreaView.apply {
                    doOnPreDraw {
                        pivotX = width.toFloat() / 2
                        pivotY = height.toFloat()
                    }
                }

                constraintSet.connect(
                    actionAreaView.id, ConstraintSet.START, layout.id, ConstraintSet.START)
                constraintSet.connect(
                    actionAreaView.id, ConstraintSet.END, layout.id, ConstraintSet.END)
                constraintSet.connect(
                    actionAreaView.id, ConstraintSet.BOTTOM,
                    layout.id, ConstraintSet.BOTTOM,
                    (2 * scaleFactor).toInt())

                constraintSet.setScaleX(actionAreaView.id, scaleFactor)
                constraintSet.setScaleY(actionAreaView.id, scaleFactor)
            }
        }

        constraintSet.applyTo(layout)
    }
}