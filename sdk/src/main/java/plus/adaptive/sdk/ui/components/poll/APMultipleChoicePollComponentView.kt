package plus.adaptive.sdk.ui.components.poll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll.view.*
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll_answer.view.*
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
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
                    showChosenAnswer(answerId, pollData)
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
        pollData: APPollData
    ) {
        if (context == null) return

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