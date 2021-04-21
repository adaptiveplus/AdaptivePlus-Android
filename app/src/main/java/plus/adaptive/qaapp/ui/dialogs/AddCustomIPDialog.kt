package plus.adaptive.qaapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import plus.adaptive.qaapp.R
import plus.adaptive.qaapp.data.AdaptiveCustomIP
import plus.adaptive.qaapp.utils.addCustomIP
import kotlinx.android.synthetic.main.add_custom_ip_dialog.*


class AddCustomIPDialog : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(interactor: InteractionInterface) = AddCustomIPDialog().apply {
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
        return inflater.inflate(R.layout.add_custom_ip_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addIpBtn.setOnClickListener {
            if (ipNameEditText.text.toString().isNotEmpty() &&
                ipValueEditText.text.toString().isNotEmpty()
            ) {
                val newIP = AdaptiveCustomIP(
                    name = ipNameEditText.text.toString(),
                    ip = ipValueEditText.text.toString()
                )

                context?.let { ctx -> addCustomIP(ctx, newIP) }

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