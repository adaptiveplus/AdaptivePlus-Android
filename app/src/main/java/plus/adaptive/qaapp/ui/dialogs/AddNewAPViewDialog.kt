package plus.adaptive.qaapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import plus.adaptive.qaapp.R
import plus.adaptive.qaapp.utils.addNewAPView
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

        addAPViewBtn.setOnClickListener {
            if (apViewIdEditText.text.toString().isNotEmpty()) {
                val apViewId = apViewIdEditText.text.toString()

                context?.let { ctx ->
                    addNewAPView(
                        context = ctx,
                        envName = envName,
                        apViewId = apViewId
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