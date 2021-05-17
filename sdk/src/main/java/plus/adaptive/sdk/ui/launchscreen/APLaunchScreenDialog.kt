package plus.adaptive.sdk.ui.launchscreen

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APLaunchScreen
import plus.adaptive.sdk.ui.launchscreen.vm.APLaunchScreenDialogViewModel
import plus.adaptive.sdk.ui.launchscreen.vm.APLaunchScreenDialogViewModelFactory


internal class APLaunchScreenDialog : DialogFragment() {

    companion object {
        private const val EXTRA_SCREEN_WIDTH = "extra_screen_width"
        private const val EXTRA_LAUNCH_SCREEN = "extra_launch_screen"

        @JvmStatic
        fun newInstance(
            screenWidth: Double,
            launchScreen: APLaunchScreen
        ) = APLaunchScreenDialog().apply {
            arguments = bundleOf(
                EXTRA_SCREEN_WIDTH to screenWidth,
                EXTRA_LAUNCH_SCREEN to launchScreen
            )
        }
    }


    private var screenWidth: Double? = null
    private lateinit var launchScreen: APLaunchScreen
    private lateinit var viewModel: APLaunchScreenDialogViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.APLaunchScreenDialogTheme)

        (arguments?.getSerializable(EXTRA_LAUNCH_SCREEN) as? APLaunchScreen)?.let {
            this.launchScreen = it
        }
        (arguments?.getDouble(EXTRA_SCREEN_WIDTH, -1.0))?.let {
            if (it >= 0.0) {
                this.screenWidth = it
            }
        }

        if (!::launchScreen.isInitialized || screenWidth == null) {
            dismiss()
            return
        }

        val viewModelFactory = APLaunchScreenDialogViewModelFactory()
        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(APLaunchScreenDialogViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_ap_launch_screen_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: implement
    }
}