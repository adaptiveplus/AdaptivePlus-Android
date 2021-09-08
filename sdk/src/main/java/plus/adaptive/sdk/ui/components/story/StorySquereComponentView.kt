package plus.adaptive.sdk.ui.components.story

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setPadding
import kotlinx.android.synthetic.main.ap_component_image.view.apComponentBorderView
import kotlinx.android.synthetic.main.ap_component_image.view.apComponentImageView
import kotlinx.android.synthetic.main.ap_component_image.view.apComponentLayout
import kotlinx.android.synthetic.main.ap_component_circle_story.view.*
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER_NEW
import plus.adaptive.sdk.data.models.APFont
import plus.adaptive.sdk.data.models.story.APOuterStyles
import plus.adaptive.sdk.ext.applyAPFont
import plus.adaptive.sdk.ext.loadImage
import plus.adaptive.sdk.ui.apview.StoriesAdapter
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.utils.StorySizeConst
import plus.adaptive.sdk.utils.StorySizeConst.TEXT_SIZE_L
import plus.adaptive.sdk.utils.StorySizeConst.TEXT_SIZE_M
import plus.adaptive.sdk.utils.StorySizeConst.TEXT_SIZE_S
import plus.adaptive.sdk.utils.createDrawableFromColor
import plus.adaptive.sdk.utils.getColorFromHex


internal class StorySquereComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: StoriesAdapter.StoryComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_squere_story, this)
        prepare()
    }

    override fun prepare() {
        (component as? StoriesAdapter.StoryComponent)?.run {
            val defaultDrawable = createDrawableFromColor(
                color = getColorFromHex(outerStyles.outerImageLoadingColor),
                cornerRadius = outerStyles.cornerRadius.toInt() * BASE_SIZE_MULTIPLIER_NEW
            )
            apComponentImageView.loadImage(
                outerImageUrl!!,
                defaultDrawable = defaultDrawable,
                cornerRadius = outerStyles.cornerRadius.toInt() * BASE_SIZE_MULTIPLIER_NEW,
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
            val textSize = when(outerStyles.outerSize){
                APOuterStyles.OuterSize.S -> TEXT_SIZE_S
                APOuterStyles.OuterSize.M -> TEXT_SIZE_M
                APOuterStyles.OuterSize.L -> TEXT_SIZE_L
                else -> 10.0 * BASE_SIZE_MULTIPLIER_NEW
            }
            val textPadding = when(outerStyles.outerSize){
                APOuterStyles.OuterSize.S -> StorySizeConst.TEXT_PADDING_S
                APOuterStyles.OuterSize.M -> StorySizeConst.TEXT_PADDING_M
                APOuterStyles.OuterSize.L -> StorySizeConst.TEXT_PADDING_L
                else -> 6 * BASE_SIZE_MULTIPLIER_NEW
            }
            appStoryComponentText.setPadding(textPadding)
            val font = APFont(
                family ="Roboto",
                style = APFont.Style.BOLD,
                size = textSize,
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
                    apComponentImageView.id, ConstraintSet.START, 2)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.END, 2)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.TOP, 2)
                constraintSet.setMargin(
                    apComponentImageView.id, ConstraintSet.BOTTOM, 2)

                constraintSet.applyTo(apComponentLayout)
                val borderDrawable =
                    GradientDrawable().apply {
                        outerStyles.cornerRadius.let { radius ->
                            cornerRadius = (radius + 2).toFloat() * BASE_SIZE_MULTIPLIER_NEW
                        }
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