package plus.adaptive.sdk.ui.components.poll

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.ap_component_yes_no_poll.view.*
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.utils.createDrawableFromColor
import plus.adaptive.sdk.utils.getColorFromHex


internal class APYesNoPollComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APPollComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_yes_no_poll, this)
        prepare()
    }

    override fun prepare() {
        apPollCardView?.radius = 16f * BASE_SIZE_MULTIPLIER
        apPollLinearLayout?.background = createDrawableFromColor(
            color = getColorFromHex("#0000"),
            cornerRadius = 16 * BASE_SIZE_MULTIPLIER,
            strokeWidth = 1 * BASE_SIZE_MULTIPLIER,
            strokeColor = getColorFromHex("#DFE2E7")
        )
        apQuestionTextView?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 18f * BASE_SIZE_MULTIPLIER)
            (layoutParams as LayoutParams).apply {
                val margin = 12 * BASE_SIZE_MULTIPLIER
                setMargins(margin, margin, margin, 0)
            }
        }
        apAnswersCardView?.apply {
            radius = 8f * BASE_SIZE_MULTIPLIER
            cardElevation = 8f * BASE_SIZE_MULTIPLIER
            (layoutParams as LayoutParams).apply {
                val marginTop = 16 * BASE_SIZE_MULTIPLIER
                val margin = 12 * BASE_SIZE_MULTIPLIER
                setMargins(margin, marginTop, margin, margin)
            }
        }
        apAnswersConstraintLayout?.run {
            background = createDrawableFromColor(
                color = getColorFromHex("#0000"),
                cornerRadius = 8 * BASE_SIZE_MULTIPLIER,
                strokeWidth = 1 * BASE_SIZE_MULTIPLIER,
                strokeColor = getColorFromHex("#DFE2E7")
            )

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            constraintSet.constrainWidth(R.id.apAnswerDivider, 1 * BASE_SIZE_MULTIPLIER)
            constraintSet.setMargin(R.id.apLeftAnswerTextView, ConstraintSet.START, 4 * BASE_SIZE_MULTIPLIER)
            constraintSet.setMargin(R.id.apLeftAnswerTextView, ConstraintSet.END, 4 * BASE_SIZE_MULTIPLIER)
            constraintSet.setMargin(R.id.apLeftAnswerRatioTextView, ConstraintSet.TOP, 2 * BASE_SIZE_MULTIPLIER)
            constraintSet.setVisibility(R.id.apLeftAnswerRatioTextView, View.GONE)
            constraintSet.setMargin(R.id.apRightAnswerTextView, ConstraintSet.START, 4 * BASE_SIZE_MULTIPLIER)
            constraintSet.setMargin(R.id.apRightAnswerTextView, ConstraintSet.END, 4 * BASE_SIZE_MULTIPLIER)
            constraintSet.setMargin(R.id.apRightAnswerRatioTextView, ConstraintSet.TOP, 2 * BASE_SIZE_MULTIPLIER)
            constraintSet.setVisibility(R.id.apRightAnswerRatioTextView, View.GONE)
            constraintSet.setGuidelineBegin(R.id.apTopGuideline, 4 * BASE_SIZE_MULTIPLIER)
            constraintSet.setGuidelineEnd(R.id.apBottomGuideline, 4 * BASE_SIZE_MULTIPLIER)

            constraintSet.applyTo(this)
        }
        apLeftAnswerLayer?.background = GradientDrawable().apply {
            getColorFromHex("#FFFFFF")?.let { setColor(it) }
            shape = GradientDrawable.RECTANGLE
            val radius = 8f * BASE_SIZE_MULTIPLIER
            cornerRadii = floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, radius, radius)
        }
        apRightAnswerLayer?.background = GradientDrawable().apply {
            getColorFromHex("#FFFFFF")?.let { setColor(it) }
            shape = GradientDrawable.RECTANGLE
            val radius = 8f * BASE_SIZE_MULTIPLIER
            cornerRadii = floatArrayOf(0f, 0f, radius, radius, radius, radius, 0f, 0f)
        }
        apLeftAnswerTextView?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f * BASE_SIZE_MULTIPLIER)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lineHeight = 20 * BASE_SIZE_MULTIPLIER
            }
        }
        apLeftAnswerRatioTextView?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 20f * BASE_SIZE_MULTIPLIER)
        }
        apRightAnswerTextView?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f * BASE_SIZE_MULTIPLIER)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lineHeight = 20 * BASE_SIZE_MULTIPLIER
            }
        }
        apRightAnswerRatioTextView?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 20f * BASE_SIZE_MULTIPLIER)
        }

        reset()
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {
        val viewModel = componentViewModel as? APPollComponentViewModel
        val pollData = viewModel?.getPollData()

        if (context == null || pollData == null) {
            hide()
            viewModel?.onPollBuildFail()
            return
        }

        if (pollData.answers.size == 2) {
            apQuestionTextView?.text = pollData.question.text

            apLeftAnswerTextView?.apply {
                text = pollData.answers[0].text
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f * BASE_SIZE_MULTIPLIER)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    lineHeight = 20 * BASE_SIZE_MULTIPLIER
                }
            }
            apLeftAnswerRatioTextView?.hide()
            apLeftAnswerLayer?.setOnClickListener {
                val answerId = pollData.answers[0].id
                viewModel.onAnswerChosen(answerId)
                showChosenAnswer(answerId, pollData)
            }

            apRightAnswerTextView?.apply {
                text = pollData.answers[1].text
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f * BASE_SIZE_MULTIPLIER)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    lineHeight = 20 * BASE_SIZE_MULTIPLIER
                }
            }
            apRightAnswerRatioTextView?.hide()
            apRightAnswerLayer?.setOnClickListener {
                val answerId = pollData.answers[1].id
                viewModel.onAnswerChosen(answerId)
                showChosenAnswer(answerId, pollData)
            }

            viewModel.onPollBuildSuccess()
        }
        else {
            hide()
            viewModel.onPollBuildFail()
        }
    }

    private fun showChosenAnswer(
        answerId: String,
        pollData: APPollData
    ) {
        if (context == null) return

        apLeftAnswerLayer?.isClickable = false
        apRightAnswerLayer?.isClickable = false

        var leftAnswerCount = pollData.answers[0].responseCount

        if (pollData.answers[0].id == answerId) {
            getColorFromHex("#CEE5FF")?.let { color ->
                apLeftAnswerLayer?.background?.colorFilter =
                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
            leftAnswerCount++
        }
        else if (pollData.answers[1].id == answerId) {
            getColorFromHex("#CEE5FF")?.let { color ->
                apRightAnswerLayer?.background?.colorFilter =
                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }

        val totalCount = maxOf(pollData.totalResponseCount + 1, 1)
        val leftAnswerRatio = minOf(leftAnswerCount * 100 / totalCount, 100)
        val rightAnswerRatio = 100 - leftAnswerRatio

        apLeftAnswerRatioTextView?.text = "$leftAnswerRatio%"
        apRightAnswerRatioTextView?.text = "$rightAnswerRatio%"

        apLeftAnswerTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14f * BASE_SIZE_MULTIPLIER)
        apRightAnswerTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14f * BASE_SIZE_MULTIPLIER)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            apLeftAnswerTextView?.lineHeight = 15 * BASE_SIZE_MULTIPLIER
            apRightAnswerTextView?.lineHeight = 15 * BASE_SIZE_MULTIPLIER
        }

        apAnswersConstraintLayout?.run {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            constraintSet.setVisibility(R.id.apLeftAnswerRatioTextView, View.VISIBLE)
            constraintSet.setVisibility(R.id.apRightAnswerRatioTextView, View.VISIBLE)
            constraintSet.setHorizontalBias(R.id.apAnswerDivider, leftAnswerRatio.toFloat() / 100)

            TransitionManager.beginDelayedTransition(this)
            constraintSet.applyTo(this)
        }
    }
}