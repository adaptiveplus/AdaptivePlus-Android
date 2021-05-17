package plus.adaptive.sdk.ui.launchscreen

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.ap_fragment_ap_launch_screen_dialog.*
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER
import plus.adaptive.sdk.data.models.APLaunchScreen
import plus.adaptive.sdk.ui.launchscreen.vm.APLaunchScreenDialogViewModel
import plus.adaptive.sdk.ui.launchscreen.vm.APLaunchScreenDialogViewModelFactory
import plus.adaptive.sdk.utils.drawAPLayersOnLayout
import plus.adaptive.sdk.utils.safeRun


internal class APLaunchScreenDialog : DialogFragment() {

    companion object {
        private const val EXTRA_LAUNCH_SCREEN_WIDTH = 375 * BASE_SIZE_MULTIPLIER
        private const val EXTRA_LAUNCH_SCREEN_HEIGHT = 667 * BASE_SIZE_MULTIPLIER

        private const val EXTRA_SCREEN_WIDTH = "extra_screen_width"
        private const val EXTRA_LAUNCH_SCREEN = "extra_launch_screen"

        @JvmStatic
        fun newInstance(
            baseScreenWidth: Double,
            launchScreen: APLaunchScreen
        ) = APLaunchScreenDialog().apply {
            arguments = bundleOf(
                EXTRA_SCREEN_WIDTH to baseScreenWidth,
                EXTRA_LAUNCH_SCREEN to launchScreen
            )
        }
    }


    private var baseScreenWidth: Double? = null
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
                this.baseScreenWidth = it
            }
        }

        if (!::launchScreen.isInitialized || baseScreenWidth == null) {
            dismiss()
            return
        }

        val viewModelFactory = APLaunchScreenDialogViewModelFactory(launchScreen)
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

        drawLaunchScreen()

        apLaunchScreenLayout.addOnLayoutChangeListener(apLaunchScreenLayoutChangeListener)
    }

    private val apLaunchScreenLayoutChangeListener = View.OnLayoutChangeListener {
        v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            updateScaleFactor()
        }
    }

    private fun drawLaunchScreen() {
        val apContentLayoutConstraintSet = ConstraintSet()
        apContentLayoutConstraintSet.clone(apContentLayout)
        apContentLayoutConstraintSet.constrainWidth(
            apLaunchScreenLayersLayout.id, EXTRA_LAUNCH_SCREEN_WIDTH)
        apContentLayoutConstraintSet.constrainHeight(
            apLaunchScreenLayersLayout.id, EXTRA_LAUNCH_SCREEN_HEIGHT)
        apContentLayoutConstraintSet.applyTo(apContentLayout)

        safeRun(
            executable = {
                drawAPLayersOnLayout(apLaunchScreenLayersLayout, launchScreen.layers, viewModel)
            },
            onExceptionCaught = {
                dismiss()
            }
        )
    }

    private fun updateScaleFactor() {
        if (apLaunchScreenLayout.width == 0 || baseScreenWidth == null) {
            return
        }
        val scaleFactor = baseScreenWidth?.let {
            (apLaunchScreenLayout.width / it).toFloat()
        } ?: 1f

        val apSnapLayoutConstraintSet = ConstraintSet()
        apSnapLayoutConstraintSet.clone(apLaunchScreenLayout)
        apSnapLayoutConstraintSet.constrainWidth(
            apContentCardView.id, (EXTRA_LAUNCH_SCREEN_WIDTH * scaleFactor).toInt())
        apSnapLayoutConstraintSet.constrainHeight(
            apContentCardView.id, (EXTRA_LAUNCH_SCREEN_HEIGHT * scaleFactor).toInt())
        apSnapLayoutConstraintSet.applyTo(apLaunchScreenLayout)

        val apContentLayoutConstraintSet = ConstraintSet()
        apContentLayoutConstraintSet.clone(apContentLayout)
        apContentLayoutConstraintSet.setScaleX(apLaunchScreenLayersLayout.id, scaleFactor)
        apContentLayoutConstraintSet.setScaleY(apLaunchScreenLayersLayout.id, scaleFactor)
        apContentLayoutConstraintSet.applyTo(apContentLayout)
    }
}