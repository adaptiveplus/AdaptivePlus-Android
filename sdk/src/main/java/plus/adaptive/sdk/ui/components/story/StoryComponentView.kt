package plus.adaptive.sdk.ui.components.story

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.ap_component_image.view.apComponentBorderView
import kotlinx.android.synthetic.main.ap_component_image.view.apComponentImageView
import kotlinx.android.synthetic.main.ap_component_image.view.apComponentLayout
import kotlinx.android.synthetic.main.ap_component_circle_story.view.*
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APFont
import plus.adaptive.sdk.data.models.story.APOuterStyles
import plus.adaptive.sdk.ext.applyAPFont
import plus.adaptive.sdk.ext.loadImage
import plus.adaptive.sdk.ui.apview.StoriesAdapter
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.utils.StorySizeConst.TEXT_SIZE_L
import plus.adaptive.sdk.utils.StorySizeConst.TEXT_SIZE_M
import plus.adaptive.sdk.utils.StorySizeConst.TEXT_SIZE_S
import plus.adaptive.sdk.utils.createDrawableFromColor
import plus.adaptive.sdk.utils.getColorFromHex


internal class StoryComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: StoriesAdapter.StoryComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_rectangle_story, this)
        prepare()
    }

    override fun prepare() {
        (component as? StoriesAdapter.StoryComponent)?.run {
            val defaultDrawable = createDrawableFromColor(
                color = getColorFromHex(outerStyles.outerImageLoadingColor),
                cornerRadius = outerStyles.cornerRadius.toInt()
            )
            val textSize = when(outerStyles.outerSize){
                APOuterStyles.OuterSize.S -> TEXT_SIZE_S
                APOuterStyles.OuterSize.M -> TEXT_SIZE_M
                APOuterStyles.OuterSize.L -> TEXT_SIZE_L
                else -> 10.0
            }
            val font = APFont(
                family ="Roboto",
                style = APFont.Style.BOLD,
                size = textSize.toDouble(),
                color ="#ffffff",
                align = APFont.Align.BOTTOM,
                letterSpacing=0.0,
                lineHeight=null)
            appStoryComponentText.applyAPFont(
                apFont = font,
                onSuccess = {
                    var text = outerText.RU
                    outerText.locale?.let {
                        text = when(it){
                            "ru" -> outerText.RU
                            "kk" -> outerText.KZ
                            "en" -> outerText.EN
                            else -> outerText.RU
                        }
                    }
                    appStoryComponentText.text = text
                    (componentViewModel as? StoryComponentViewModel)?.onTextResourceReady()
                },
                onError = {
                    appStoryComponentText.text = outerText.RU
                    (componentViewModel as? StoryComponentViewModel)?.onError()
                }
            )
            apComponentImageView.loadImage(
                outerImageUrl!!,
                defaultDrawable = defaultDrawable,
                cornerRadius = outerStyles.cornerRadius.toInt(),
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
                    apComponentImageView.id, ConstraintSet.START, 5)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.END, 5)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.TOP, 5)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.BOTTOM, 5)

                constraintSet.applyTo(apComponentLayout)
                val borderDrawable =
                    GradientDrawable().apply {
                        outerStyles.cornerRadius.let { radius ->
                            cornerRadius = radius.toFloat()
                        }
                        getColorFromHex(outerBorderColor)?.let {
                            setStroke(2, it)
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