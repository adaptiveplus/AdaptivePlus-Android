package com.sprintsquads.adaptiveplusqaapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.sprintsquads.adaptiveplus.sdk.data.APOnboardingItem
import com.sprintsquads.adaptiveplusqaapp.R
import kotlinx.android.synthetic.main.onboarding_dialog.*


class OnboardingDialog : DialogFragment() {

    companion object {
        private const val EXTRA_ONBOARDING = "extra_onboarding"

        @JvmStatic
        fun newInstance(
            onboardingItems: List<APOnboardingItem>,
            interactor: InteractionInterface
        ) = OnboardingDialog().apply {
            arguments = bundleOf(EXTRA_ONBOARDING to ArrayList(onboardingItems))
            this.interactor = interactor
        }
    }


    private lateinit var onboardingItems: List<APOnboardingItem>
    private var interactor: InteractionInterface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.AdaptiveQaAppDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.onboarding_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (arguments?.getSerializable(EXTRA_ONBOARDING) as? ArrayList<APOnboardingItem>)?.let {
            this.onboardingItems = it
        } ?: run {
            dismiss()
            return
        }

        onboardingItemsTextView.text = onboardingItems.fold("") { res, item ->
            res + "${convertOnboardingItemToString(item)}\n" }

        itemsSpinner.adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            onboardingItems.indices.map { it.toString() }
        )

        runActionButton.setOnClickListener {
            interactor?.onRunActions(itemsSpinner.selectedItem.toString().toIntOrNull() ?: 0)
        }
    }

    private fun convertOnboardingItemToString(item: APOnboardingItem): String {
        return "{\n\ttitle: ${item.title},\n\tsubtitle: ${item.subtitle}," +
                "\n\timageUrl: ${item.imageUrl},\n\timageType: ${item.imageType}," +
                "\n\thasActions: ${item.hasActions}\n},"
    }


    interface InteractionInterface {
        fun onRunActions(onboardingItemIndex: Int)
        fun onDismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        interactor?.onDismiss()
    }
}