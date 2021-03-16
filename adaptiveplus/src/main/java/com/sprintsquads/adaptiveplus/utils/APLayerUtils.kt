package com.sprintsquads.adaptiveplus.utils

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import com.sprintsquads.adaptiveplus.data.models.APEntry
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.models.components.APBackgroundComponent
import com.sprintsquads.adaptiveplus.data.models.components.APImageComponent
import com.sprintsquads.adaptiveplus.data.models.components.APTextComponent
import com.sprintsquads.adaptiveplus.ui.components.APBackgroundComponentView
import com.sprintsquads.adaptiveplus.ui.components.APImageComponentView
import com.sprintsquads.adaptiveplus.ui.components.APTextComponentView


internal fun buildComponentView(context: Context, layer: APLayer): View? {
    return when (layer.component) {
        is APBackgroundComponent -> APBackgroundComponentView(context, layer.component)
        is APImageComponent -> APImageComponentView(context, layer.component)
        is APTextComponent -> APTextComponentView(context, layer.component)
        else -> null
    }
}

internal fun drawEntry(
    layout: ConstraintLayout,
    entry: APEntry,
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