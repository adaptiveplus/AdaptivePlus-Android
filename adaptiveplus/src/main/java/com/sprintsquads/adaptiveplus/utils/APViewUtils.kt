package com.sprintsquads.adaptiveplus.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.components.APBackgroundComponent
import com.sprintsquads.adaptiveplus.data.models.components.APImageComponent
import com.sprintsquads.adaptiveplus.data.models.components.APTextComponent
import com.sprintsquads.adaptiveplus.ui.components.APBackgroundComponentView
import com.sprintsquads.adaptiveplus.ui.components.APImageComponentView
import com.sprintsquads.adaptiveplus.ui.components.APTextComponentView
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModel
import com.sprintsquads.adaptiveplus.ui.components.vm.APComponentViewModelProvider


internal fun buildComponentView(
    context: Context,
    layer: APLayer,
    viewModel: APComponentViewModel?
): View? {
    return when (layer.component) {
        is APBackgroundComponent -> APBackgroundComponentView(context, layer.component, viewModel)
        is APImageComponent -> APImageComponentView(context, layer.component, viewModel)
        is APTextComponent -> APTextComponentView(context, layer.component, viewModel)
        else -> null
    }
}

internal fun drawAPLayersOnLayout(
    layout: ConstraintLayout,
    layers: List<APLayer>,
    scaleFactor: Float = 1f,
    componentViewModelProvider: APComponentViewModelProvider?
) {
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
                    (action.params?.get("story") as? APStory)?.let { story ->
                        stories.add(story)
                    }
                }
            }
        }

        stories
    }
}