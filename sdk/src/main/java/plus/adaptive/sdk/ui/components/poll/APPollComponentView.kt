package plus.adaptive.sdk.ui.components.poll

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll.view.*
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll_answer.view.*
import kotlinx.android.synthetic.main.ap_component_yes_no_poll.view.*
import kotlinx.android.synthetic.main.ap_component_yes_no_poll.view.apQuestionTextView
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import plus.adaptive.sdk.utils.getColorFromHex


internal class APPollComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APPollComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        if ((component as? APPollComponent)?.type == APPollComponent.Type.YES_NO_POLL) {
            View.inflate(context, R.layout.ap_component_yes_no_poll, this)
        } else {
            View.inflate(context, R.layout.ap_component_multiple_choice_poll, this)
        }

        prepare()
    }

    override fun prepare() {
        reset()
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {
        val pollType = (component as? APPollComponent)?.type
        val viewModel = componentViewModel as? APPollComponentViewModel
        val pollData = viewModel?.getPollData()

        if (context == null || pollType == null || pollData == null) {
            hide()
            viewModel?.onPollBuildFail()
            return
        }

        if (pollType == APPollComponent.Type.YES_NO_POLL && pollData.answers.size == 2) {
            apQuestionTextView?.text = pollData.question.text

            apLeftAnswerTextView?.apply {
                text = pollData.answers[0].text
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    lineHeight = 20
                }
            }
            apLeftAnswerRatioTextView?.hide()
            apLeftAnswerLayer?.setOnClickListener {
                val answerId = pollData.answers[0].id
                viewModel.onAnswerChosen(answerId)
                showChosenAnswer(answerId, pollType, pollData)
            }

            apRightAnswerTextView?.apply {
                text = pollData.answers[1].text
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    lineHeight = 20
                }
            }
            apRightAnswerRatioTextView?.hide()
            apRightAnswerLayer?.setOnClickListener {
                val answerId = pollData.answers[1].id
                viewModel.onAnswerChosen(answerId)
                showChosenAnswer(answerId, pollType, pollData)
            }

            viewModel.onPollBuildSuccess()
        }
        else if (pollType == APPollComponent.Type.MULTIPLE_CHOICE_POLL &&
            pollData.answers.isNotEmpty()
        ) {
            apQuestionTextView?.text = pollData.question.text

            apAnswersLinearLayout?.removeAllViews()
            pollData.answers.forEach { answer ->
                val answerView = View.inflate(
                    context, R.layout.ap_component_multiple_choice_poll_answer, null)
                answerView.apAnswerTextView.apply {
                    text = answer.text
                    setTextColor(ContextCompat.getColor(context, R.color.apPrimaryCarbon))
                }
                answerView.apAnswerRatioBarView.hide()
                answerView.apAnswerRatioTextView.hide()
                answerView.apAnswerBarView.setOnClickListener {
                    val answerId = answer.id
                    viewModel.onAnswerChosen(answerId)
                    showChosenAnswer(answerId, pollType, pollData)
                }

                apAnswersLinearLayout?.addView(answerView)
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
        pollType: APPollComponent.Type,
        pollData: APPollData
    ) {
        if (context == null) return

        if (pollType == APPollComponent.Type.YES_NO_POLL) {
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

            apLeftAnswerTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14f)
            apRightAnswerTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14f)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                apLeftAnswerTextView?.lineHeight = 15
                apRightAnswerTextView?.lineHeight = 15
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
        else if (pollType == APPollComponent.Type.MULTIPLE_CHOICE_POLL) {
            apAnswersLinearLayout?.let { answersLayout ->
                val totalCount = maxOf(pollData.totalResponseCount + 1, 1)
                var indexOfAnswer = 0

                for (answerView in answersLayout.children) {
                    answerView.isClickable = false

                    if (pollData.answers[indexOfAnswer].id == answerId) {
                        answerView.apAnswerBarView.setBackgroundResource(
                            R.drawable.ap_bg_multi_choice_poll_answer_bar_chosen)
                        answerView.apAnswerRatioBarView.setBackgroundResource(
                            R.drawable.ap_bg_multi_choice_poll_answer_ratio_bar_chosen)
                        getColorFromHex("#097BFF")?.let {
                            answerView.apAnswerRatioTextView.setTextColor(it)
                        }
                    }
                    else {
                        answerView.apAnswerBarView.setBackgroundResource(
                            R.drawable.ap_bg_multi_choice_poll_answer_bar)
                        answerView.apAnswerRatioBarView.setBackgroundResource(
                            R.drawable.ap_bg_multi_choice_poll_answer_ratio_bar)
                        getColorFromHex("#343B4C")?.let {
                            answerView.apAnswerRatioTextView.setTextColor(it)
                        }
                    }

                    val answerResponseCount = pollData.answers[indexOfAnswer].responseCount
                    val answerRatio = minOf(answerResponseCount * 100 / totalCount, 100)

                    answerView.apAnswerRatioTextView.text = "$answerRatio%"

                    val constraintSet = ConstraintSet()
                    constraintSet.clone(answerView.apAnswerConstraintLayout)

                    constraintSet.setVisibility(R.id.apAnswerRatioBarView, View.VISIBLE)
                    constraintSet.setVisibility(R.id.apAnswerRatioTextView, View.VISIBLE)
                    constraintSet.setGuidelinePercent(R.id.apAnswerRatioGuideline, answerRatio / 100f)

                    TransitionManager.beginDelayedTransition(answerView.apAnswerConstraintLayout)
                    constraintSet.applyTo(answerView.apAnswerConstraintLayout)

                    indexOfAnswer++
                }
            }
        }
    }
}