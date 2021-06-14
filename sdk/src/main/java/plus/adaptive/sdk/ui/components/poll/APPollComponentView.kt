package plus.adaptive.sdk.ui.components.poll

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll.view.*
import kotlinx.android.synthetic.main.ap_component_multiple_choice_poll_answer.view.*
import kotlinx.android.synthetic.main.ap_component_yes_no_poll.view.*
import kotlinx.android.synthetic.main.ap_component_yes_no_poll.view.apQuestionTextView
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel


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
            viewModel?.onPollBuildFail()
            return
        }

        if (pollType == APPollComponent.Type.YES_NO_POLL && pollData.answers.size == 2) {
            apQuestionTextView?.text = pollData.question.text

            apLeftAnswerTextView?.apply {
                text = pollData.answers[0].text
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f)
            }
            apLeftAnswerRatioTextView?.hide()

            apRightAnswerTextView?.apply {
                text = pollData.answers[1].text
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 16f)
            }
            apRightAnswerRatioTextView?.hide()

            viewModel.onPollBuildSuccess()
        }
        else if (pollType == APPollComponent.Type.MULTIPLE_CHOICE_POLL &&
            pollData.answers.isNotEmpty()
        ) {
            apQuestionTextView?.text = pollData.question.text

            pollData.answers.forEach { answer ->
                val answerView = View.inflate(
                    context, R.layout.ap_component_multiple_choice_poll_answer, null)
                answerView.apAnswerTextView.apply {
                    text = answer.text
                    setTextColor(ContextCompat.getColor(context, R.color.apPrimaryCarbon))
                }
                answerView.apAnswerRatioBarView.hide()
                answerView.apAnswerRatioTextView.hide()

                apAnswersLinearLayout?.addView(answerView)
            }

            viewModel.onPollBuildSuccess()
        }
        else {
            viewModel.onPollBuildFail()
        }
    }
}