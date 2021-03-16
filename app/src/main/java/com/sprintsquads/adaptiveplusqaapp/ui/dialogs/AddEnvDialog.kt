package com.sprintsquads.adaptiveplusqaapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.fragment.app.DialogFragment
import com.sprintsquads.adaptiveplusqaapp.R
import com.sprintsquads.adaptiveplusqaapp.data.APSdkEnvironment
import com.sprintsquads.adaptiveplusqaapp.utils.addNewEnv
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
                companySecretEditText.text.toString().isNotEmpty() &&
                appSecretEditText.text.toString().isNotEmpty()
            ) {
                val newEnv = APSdkEnvironment(
                    name = envNameEditText.text.toString(),
                    baseApiUrl = baseUrlEditText.text.toString(),
                    appId = appIdEditText.text.toString(),
                    companySecret = companySecretEditText.text.toString(),
                    appSecret = appSecretEditText.text.toString(),
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