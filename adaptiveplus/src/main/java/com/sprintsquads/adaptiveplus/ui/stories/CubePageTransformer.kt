package com.sprintsquads.adaptiveplus.ui.stories

import android.view.View
import androidx.viewpager.widget.ViewPager


internal class CubePageTransformer: ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val clampedPosition = clampPosition(position)
        onPreTransform(page, clampedPosition)
        onTransform(page, clampedPosition)
    }

    private fun clampPosition(position: Float): Float {
        return when {
            position < -1f -> -1f
            position > 1f -> 1f
            position.isNaN() -> 0f
            else -> position
        }
    }

    private fun onPreTransform(page: View, position: Float) {
        page.rotationX = 0f
        page.rotationY = 0f
        page.rotation = 0f
        page.scaleX = 1f
        page.scaleY = 1f
        page.pivotX = 0f
        page.pivotY = 0f
        page.translationY = 0f
        page.translationX = 0f

        page.alpha = if (position <= -1f || position >= 1f) 0f else 1f
        page.isEnabled = false
    }

    private fun onTransform(page: View, position: Float) {
        val distanceMultiplier = 20
        page.cameraDistance = (page.width * distanceMultiplier).toFloat()
        page.pivotX = if (position < 0f) page.width.toFloat() else 0f
        page.pivotY = page.height * 0.5f
        page.rotationY = 90f * position
    }
}