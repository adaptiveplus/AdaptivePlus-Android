package plus.adaptive.sdk.ui.components.poll

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll.view.*
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll_answer.view.*
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ext.show
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.utils.createDrawableFromColor
import plus.adaptive.sdk.utils.getColorFromHex


internal class APMultipleChoicePollComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APPollComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_multiple_choice_poll, this)
        prepare()
    }

    override fun prepare() {
        apPollCardView?.radius = 16f * BASE_SIZE_MULTIPLIER
        apPollLinearLayout?.apply {
            background = createDrawableFromColor(
                color = getColorFromHex("#00000000"),
                cornerRadius = 16 * BASE_SIZE_MULTIPLIER,
                strokeWidth = 1 * BASE_SIZE_MULTIPLIER,
                strokeColor = getColorFromHex("#DFE2E7")
            )
            setPadding(12 * BASE_SIZE_MULTIPLIER)
        }
        apQuestionTextView?.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, 18f * BASE_SIZE_MULTIPLIER)
        }
        apAnswersLinearLayout?.apply {
            (layoutParams as LayoutParams).apply {
                val margin = 8 * BASE_SIZE_MULTIPLIER
                setMargins(0, margin, 0, 0)
            }
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

        if (pollData.answers.isNotEmpty()) {
            apQuestionTextView?.text = pollData.question.text

            apAnswersLinearLayout?.removeAllViews()
            pollData.answers.forEach { answer ->
                val answerView = View.inflate(
                    context, R.layout.ap_component_multiple_choice_poll_answer, null
                ).apply {
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 40 * BASE_SIZE_MULTIPLIER).apply {
                        val margin = 4 * BASE_SIZE_MULTIPLIER
                        setMargins(0, margin, 0, margin)
                    }
                }

                val constraintSet = ConstraintSet()
                constraintSet.clone(answerView.apAnswerConstraintLayout)

                constraintSet.setMargin(R.id.apAnswerBarView, ConstraintSet.END, 50 * BASE_SIZE_MULTIPLIER)
                constraintSet.setMargin(R.id.apAnswerBarView, ConstraintSet.RIGHT, 50 * BASE_SIZE_MULTIPLIER)
                constraintSet.setMargin(R.id.apAnswerTextView, ConstraintSet.START, 8 * BASE_SIZE_MULTIPLIER)
                constraintSet.setMargin(R.id.apAnswerTextView, ConstraintSet.END, 8 * BASE_SIZE_MULTIPLIER)
                constraintSet.setHorizontalBias(R.id.apAnswerRatioGuidelineView, 0f)

                constraintSet.applyTo(answerView.apAnswerConstraintLayout)

                answerView.apAnswerTextView.apply {
                    text = answer.text
                    setTextColor(ContextCompat.getColor(context, R.color.apPrimaryCarbon))
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f * BASE_SIZE_MULTIPLIER)
                }
                answerView.apAnswerRatioBarView.apply {
                    background = createDrawableFromColor(
                        color = getColorFromHex("#DFE2E7"),
                        cornerRadius = 8 * BASE_SIZE_MULTIPLIER
                    )
                    hide()
                }
                answerView.apAnswerRatioTextView.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.apPrimaryCarbon))
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f * BASE_SIZE_MULTIPLIER)
                    hide()
                }
                answerView.apAnswerBarView.apply {
                    background = createDrawableFromColor(
                        color = getColorFromHex("#F6F7FB"),
                        cornerRadius = 8 * BASE_SIZE_MULTIPLIER
                    )
                    setOnClickListener {
                        val answerId = answer.id
                        viewModel.onAnswerChosen(answerId)
                        showChosenAnswer(answerId, pollData)
                    }
                }

                apAnswersLinearLayout?.addView(answerView)
            }

            viewModel.onPollBuildSuccess()

            val chosenAnswerId = viewModel.getChosenAnswerId()
            if (chosenAnswerId != null &&
                pollData.answers.find { it.id == chosenAnswerId } != null
            ) {
                showChosenAnswer(chosenAnswerId, pollData)
            }
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

        apAnswersLinearLayout?.let { answersLayout ->
            val totalCount = maxOf(pollData.totalResponseCount + 1, 1)
            var indexOfAnswer = 0

            for (answerView in answersLayout.children) {
                answerView.apAnswerBarView.isClickable = false

                if (pollData.answers[indexOfAnswer].id == answerId) {
                    answerView.apAnswerBarView.background = createDrawableFromColor(
                        color = getColorFromHex("#CEE5FF"),
                        cornerRadius = 8 * BASE_SIZE_MULTIPLIER
                    )

                    val leftColor = getColorFromHex("#70B2FF")
                    val rightColor = getColorFromHex("#5C9CE6")
                    if (leftColor != null && rightColor != null) {
                        answerView.apAnswerRatioBarView.background = GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            intArrayOf(leftColor, rightColor)
                        ).apply {
                            cornerRadius = 8f * BASE_SIZE_MULTIPLIER
                        }
                    }

                    getColorFromHex("#FFFFFF")?.let {
                        answerView.apAnswerTextView.setTextColor(it)
                    }
                    getColorFromHex("#097BFF")?.let {
                        answerView.apAnswerRatioTextView.setTextColor(it)
                    }
                }
                else {
                    answerView.apAnswerBarView.background = createDrawableFromColor(
                        color = getColorFromHex("#F6F7FB"),
                        cornerRadius = 8 * BASE_SIZE_MULTIPLIER
                    )
                    answerView.apAnswerRatioBarView.background = createDrawableFromColor(
                        color = getColorFromHex("#DFE2E7"),
                        cornerRadius = 8 * BASE_SIZE_MULTIPLIER
                    )

                    getColorFromHex("#6D7885")?.let {
                        answerView.apAnswerTextView.setTextColor(it)
                    }
                    getColorFromHex("#343B4C")?.let {
                        answerView.apAnswerRatioTextView.setTextColor(it)
                    }
                }

                val answerResponseCount = pollData.answers[indexOfAnswer].responseCount +
                    if (pollData.answers[indexOfAnswer].id == answerId) 1 else 0
                val answerRatio = minOf(answerResponseCount * 100 / totalCount, 100)

                answerView.apAnswerRatioTextView.text = "$answerRatio%"

                val constraintSet = ConstraintSet()
                constraintSet.clone(answerView.apAnswerConstraintLayout)

                constraintSet.setVisibility(R.id.apAnswerRatioBarView, View.VISIBLE)
                constraintSet.setVisibility(R.id.apAnswerRatioTextView, View.VISIBLE)
                constraintSet.setHorizontalBias(R.id.apAnswerRatioGuidelineView, answerRatio / 100f)

                TransitionManager.beginDelayedTransition(answerView.apAnswerConstraintLayout)
                constraintSet.applyTo(answerView.apAnswerConstraintLayout)

                indexOfAnswer++
            }
        }
    }
}