package com.sprintsquads.adaptiveplus.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import com.sprintsquads.adaptiveplus.data.models.AdaptiveEntry
import com.sprintsquads.adaptiveplus.data.models.AdaptiveLayer
import java.util.*


internal fun buildComponentView(context: Context, layer: AdaptiveLayer): View? {
    return View(context).apply {
        val rnd = Random()
        setBackgroundColor(
            Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        )
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