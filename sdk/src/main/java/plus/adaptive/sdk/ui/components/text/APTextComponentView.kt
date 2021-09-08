package plus.adaptive.sdk.ui.components.text

import android.content.Context
import android.util.AttributeSet
import android.view.View
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.components.APTextComponent
import plus.adaptive.sdk.ext.applyAPFont
import plus.adaptive.sdk.ui.components.core.APBaseComponentView
import plus.adaptive.sdk.ui.components.core.vm.APComponentViewModel
import kotlinx.android.synthetic.main.ap_component_text.view.*
import plus.adaptive.sdk.ui.components.story.StoryComponentViewModel
import plus.adaptive.sdk.utils.magnifyAPFont


internal class APTextComponentView : APBaseComponentView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        component: APTextComponent,
        componentViewModel: APComponentViewModel?
    ) : super(context, component, componentViewModel)


    override fun initElement() {
        View.inflate(context, R.layout.ap_component_text, this)
        prepare()
    }

    override fun prepare() {
        if(component is APTextComponent)
            (component as? APTextComponent)?.run {
                font?.let {
                    val newFont = magnifyAPFont(it)
                    apComponentTextView.applyAPFont(
                        apFont = newFont,
                        onSuccess = {
                            val text = when(value.locale){
                                "ru" -> value.RU
                                "kk" -> value.KZ
                                "en" -> value.EN
                                else -> value.RU
                            }
                            apComponentTextView.text = text
                            (componentViewModel as? APTextComponentViewModel)?.onTextResourceReady()
                            (componentViewModel as? StoryComponentViewModel)?.onTextResourceReady()
                        },
                        onError = {
                            apComponentTextView.text = value.RU
                            (componentViewModel as? APTextComponentViewModel)?.onError()
                            (componentViewModel as? StoryComponentViewModel)?.onError()
                        }
                    )
                }
            }
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {}
}