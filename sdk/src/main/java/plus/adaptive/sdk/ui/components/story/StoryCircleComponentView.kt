package plus.adaptive.sdk.ui.components.story

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.ap_component_image.view.*
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER_NEW
import plus.adaptive.sdk.ext.loadCircleImage
import plus.adaptive.sdk.ui.apview.StoriesAdapter
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.utils.createCircleDrawableFromColor
import plus.adaptive.sdk.utils.getColorFromHex


internal class StoryCircleComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: StoriesAdapter.StoryComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_circle_story, this)
        prepare()
    }

    override fun prepare() {
        (component as? StoriesAdapter.StoryComponent)?.run {
            val defaultDrawable = createCircleDrawableFromColor(
                color = getColorFromHex(outerStyles.outerImageLoadingColor)
            )
            apComponentImageView.loadCircleImage(
                outerImageUrl!!,
                defaultDrawable = defaultDrawable,
                onResourceReady = {
                    (componentViewModel as? StoryComponentViewModel)?.onImageResourceReady()
                },
                onLoadFailed = {
                    (componentViewModel as? StoryComponentViewModel)?.onImageLoadFailed()
                },
                onLoadProgressUpdate = {
                    (componentViewModel as? StoryComponentViewModel)?.onImageLoadProgressUpdate(it)
                }
            )

            updateImageBorder()
        }
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {
        updateImageBorder()
    }

    private fun updateImageBorder() {
        (component as? StoriesAdapter.StoryComponent)?.run {
            if ((componentViewModel as? StoryComponentViewModel)?.showBorder() != false) {
                val constraintSet = ConstraintSet()
                constraintSet.clone(apComponentLayout)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.START, 2 * BASE_SIZE_MULTIPLIER_NEW
                )
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.END, 2 * BASE_SIZE_MULTIPLIER_NEW)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.TOP, 2 * BASE_SIZE_MULTIPLIER_NEW)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.BOTTOM, 2 * BASE_SIZE_MULTIPLIER_NEW)

                constraintSet.applyTo(apComponentLayout)
                val borderDrawable =
                    GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        getColorFromHex(outerBorderColor)?.let {
                            setStroke(2 * BASE_SIZE_MULTIPLIER_NEW, it)
                        }
                    }


                apComponentBorderView.background = borderDrawable
            } else {
                val constraintSet = ConstraintSet()
                constraintSet.clone(apComponentLayout)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.START, 0)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.END, 0)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.TOP, 0)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.BOTTOM, 0)
                constraintSet.applyTo(apComponentLayout)

                apComponentBorderView.background = null
            }
        }
    }
}