package com.sprintsquads.adaptiveplusqaapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.sprintsquads.adaptiveplusqaapp.R
import com.sprintsquads.adaptiveplusqaapp.data.APSdkEnvironment
import com.sprintsquads.adaptiveplusqaapp.utils.addNewAPView
import kotlinx.android.synthetic.main.add_new_ap_view_dialog.*


class AddNewAPViewDialog : DialogFragment() {

    companion object {
        private const val EXTRA_ENV_NAME = "extra_env_name"

        @JvmStatic
        fun newInstance(
            envName: String,
            interactor: InteractionInterface
        ) = AddNewAPViewDialog().apply {
            arguments = bundleOf(EXTRA_ENV_NAME to envName)
            this.interactor = interactor
        }
    }


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
        return inflater.inflate(R.layout.add_new_ap_view_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val envName = arguments?.getString(EXTRA_ENV_NAME)

        if (envName == null || context == null) {
            dismiss()
            return
        }

        val loadingTypes = APSdkEnvironment.APView.LoadingType.values().map { it.name }
        loadingTypeSpinner.adapter = ArrayAdapter<String>(
            context!!, android.R.layout.simple_spinner_dropdown_item, loadingTypes)

        addAPViewBtn.setOnClickListener {
            if (apViewIdEditText.text.toString().isNotEmpty()) {
                val apViewId = apViewIdEditText.text.toString()
                val loadingType = APSdkEnvironment.APView.LoadingType.values().firstOrNull {
                    it.name == loadingTypeSpinner.selectedItem.toString()
                } ?: APSdkEnvironment.APView.LoadingType.EMPTY
                val isInstructions = isInstructionsCheckBox.isChecked
                val isOnboarding = isOnboardingCheckBox.isChecked
                val hasBookmarks = hasBookmarksCheckBox.isChecked

                context?.let { ctx ->
                    addNewAPView(
                        context = ctx,
                        envName = envName,
                        apViewId = apViewId,
                        loadingType = loadingType,
                        isInstructions = isInstructions,
                        isOnboarding = isOnboarding,
                        hasBookmarks = hasBookmarks
                    )
                }

                dismiss()
            }
        }
    }


    interface InteractionInterface {
        fun onDismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        interactor?.onDismiss()
    }
}