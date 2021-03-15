package com.sprintsquads.adaptiveplus.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import com.sprintsquads.adaptiveplus.data.models.AdaptiveEntry
import com.sprintsquads.adaptiveplus.data.models.AdaptiveLayer
import com.sprintsquads.adaptiveplus.data.models.components.AdaptiveBackgroundComponent
import com.sprintsquads.adaptiveplus.data.models.components.AdaptiveImageComponent
import com.sprintsquads.adaptiveplus.data.models.components.AdaptiveTextComponent
import com.sprintsquads.adaptiveplus.ui.components.AdaptiveBackgroundComponentView
import com.sprintsquads.adaptiveplus.ui.components.AdaptiveImageComponentView
import com.sprintsquads.adaptiveplus.ui.components.AdaptiveTextComponentView


internal fun buildComponentView(context: Context, layer: AdaptiveLayer): View? {
    return when (layer.component) {
        is AdaptiveBackgroundComponent -> AdaptiveBackgroundComponentView(context, layer.component)
        is AdaptiveImageComponent -> AdaptiveImageComponentView(context, layer.component)
        is AdaptiveTextComponent -> AdaptiveTextComponentView(context, layer.component)
        else -> null
    }
}

internal fun drawEntry(
    layout: ConstraintLayout,
    entry: AdaptiveEntry,
    scaleFactor: Float = 1f
) {
    entry.layers.forEach { layer ->
        buildComponentView(layout.context, layer)?.let { componentView ->
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