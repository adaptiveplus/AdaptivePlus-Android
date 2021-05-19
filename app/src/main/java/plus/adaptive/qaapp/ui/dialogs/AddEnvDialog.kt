package plus.adaptive.qaapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import plus.adaptive.qaapp.R
import plus.adaptive.qaapp.data.APSdkEnvironment
import plus.adaptive.qaapp.utils.addNewEnv
import kotlinx.android.synthetic.main.add_env_dialog.*


class AddEnvDialog : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = AddEnvDialog()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.AdaptiveQaAppDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_env_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addEnvBtn.setOnClickListener {
            if (envNameEditText.text.toString().isNotEmpty() &&
                appIdEditText.text.toString().isNotEmpty() &&
                apiKeyEditText.text.toString().isNotEmpty()
            ) {
                val newEnv = APSdkEnvironment(
                    name = envNameEditText.text.toString(),
                    appId = appIdEditText.text.toString(),
                    apiKey = apiKeyEditText.text.toString(),
                    apViews = listOf()
                )

                context?.let { ctx -> addNewEnv(ctx, newEnv) }

                dismiss()
            }
        }
    }


    private var onDismissListener: OnDismissListener? = null


    fun interface OnDismissListener {
        fun onDismiss()
    }

    fun setOnDismissListener(listener: OnDismissListener?) {
        this.onDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss()
    }
}