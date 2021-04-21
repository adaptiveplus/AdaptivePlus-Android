package plus.adaptive.qaapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.fragment.app.DialogFragment
import plus.adaptive.qaapp.R
import plus.adaptive.qaapp.data.APSdkEnvironment
import plus.adaptive.qaapp.utils.addNewEnv
import kotlinx.android.synthetic.main.add_env_dialog.*


class AddEnvDialog : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(interactor: InteractionInterface) = AddEnvDialog().apply {
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
        return inflater.inflate(R.layout.add_env_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addEnvBtn.setOnClickListener {
            if (envNameEditText.text.toString().isNotEmpty() &&
                appIdEditText.text.toString().isNotEmpty() &&
                baseUrlEditText.text.toString().isNotEmpty() &&
                URLUtil.isValidUrl(baseUrlEditText.text.toString()) &&
                channelSecretEditText.text.toString().isNotEmpty()
            ) {
                val newEnv = APSdkEnvironment(
                    name = envNameEditText.text.toString(),
                    baseApiUrl = baseUrlEditText.text.toString(),
                    appId = appIdEditText.text.toString(),
                    channelSecret = channelSecretEditText.text.toString(),
                    apViews = listOf()
                )

                context?.let { ctx -> addNewEnv(ctx, newEnv) }

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