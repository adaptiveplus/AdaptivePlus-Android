package plus.adaptive.sdk.ui.splashscreen

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.ap_fragment_splash_screen_dialog.*
import kotlinx.android.synthetic.main.ap_fragment_splash_screen_dialog.apContentCardView
import kotlinx.android.synthetic.main.ap_fragment_splash_screen_dialog.apContentLayout
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.models.APSplashScreen
import plus.adaptive.sdk.data.models.APSplashScreenTemplate
import plus.adaptive.sdk.ui.splashscreen.vm.APSplashScreenDialogViewModel
import plus.adaptive.sdk.ui.splashscreen.vm.APSplashScreenDialogViewModelFactory
import plus.adaptive.sdk.utils.drawAPLayersOnLayout
import plus.adaptive.sdk.utils.safeRun


internal class APSplashScreenDialog : DialogFragment() {

    companion object {
        private const val EXTRA_SPLASH_SCREEN = "extra_splash_screen"
        private const val EXTRA_OPTIONS = "extra_options"

        @JvmStatic
        fun newInstance(
            splashScreen: APSplashScreen,
            options: APSplashScreenTemplate.Options,
            listener: APSplashScreenDialogListener? = null
        ) = APSplashScreenDialog().apply {
            arguments = bundleOf(
                EXTRA_SPLASH_SCREEN to splashScreen,
                EXTRA_OPTIONS to options
            )
            this.listener = listener
        }
    }


    private lateinit var splashScreen: APSplashScreen
    private lateinit var options: APSplashScreenTemplate.Options
    private lateinit var viewModel: APSplashScreenDialogViewModel

    private var listener: APSplashScreenDialogListener? = null
    private var countDownTimer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.APSplashScreenDialogTheme)

        (arguments?.getSerializable(EXTRA_SPLASH_SCREEN) as? APSplashScreen)?.let {
            this.splashScreen = it
        }
        (arguments?.getSerializable(EXTRA_OPTIONS) as? APSplashScreenTemplate.Options)?.let {
            this.options = it
        }

        if (!::splashScreen.isInitialized || !::options.isInitialized) {
            dismiss()
            return
        }

        val viewModelFactory = APSplashScreenDialogViewModelFactory(context, splashScreen)
        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(APSplashScreenDialogViewModel::class.java)
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
        return inflater.inflate(R.layout.ap_fragment_splash_screen_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showTime = splashScreen.showTime?.toInt() ?: 3
        apSkipTextView.text = getString(R.string.ap_skip, showTime)
        apSkipBtnLayout.setOnClickListener {
            dismiss()
        }

        countDownTimer = object: CountDownTimer(showTime * 1000L, 100L) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = ((millisUntilFinished + 900) / 1000).toInt()
                apSkipTextView?.text = getString(R.string.ap_skip, timeLeft)
            }

            override fun onFinish() {
                dismiss()
            }
        }

        drawSplashScreen()

        apSplashScreenLayout.addOnLayoutChangeListener(apSplashScreenLayoutChangeListener)

        setupObservers()
    }

    private val apSplashScreenLayoutChangeListener = View.OnLayoutChangeListener {
        v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            updateScaleFactor()
        }
    }

    private fun drawSplashScreen() {
        val apContentLayoutConstraintSet = ConstraintSet()
        apContentLayoutConstraintSet.clone(apContentLayout)
        apContentLayoutConstraintSet.constrainWidth(
            apSplashScreenLayersLayout.id, options.width.toInt())
        apContentLayoutConstraintSet.constrainHeight(
            apSplashScreenLayersLayout.id, options.height.toInt())
        apContentLayoutConstraintSet.applyTo(apContentLayout)

        safeRun(
            executable = {
                drawAPLayersOnLayout(apSplashScreenLayersLayout, splashScreen.layers, viewModel)
            },
            onExceptionCaught = {
                dismiss()
            }
        )
    }

    private fun updateScaleFactor() {
        if (apSplashScreenLayout.width == 0) {
            return
        }

        val scaleFactor = (apSplashScreenLayout.width / options.screenWidth).toFloat()

        val apSnapLayoutConstraintSet = ConstraintSet()
        apSnapLayoutConstraintSet.clone(apSplashScreenLayout)
        apSnapLayoutConstraintSet.constrainWidth(
            apContentCardView.id, (options.width * scaleFactor).toInt())
        apSnapLayoutConstraintSet.constrainHeight(
            apContentCardView.id, (options.height * scaleFactor).toInt())
        apSnapLayoutConstraintSet.applyTo(apSplashScreenLayout)

        val apContentLayoutConstraintSet = ConstraintSet()
        apContentLayoutConstraintSet.clone(apContentLayout)
        apContentLayoutConstraintSet.setScaleX(apSplashScreenLayersLayout.id, scaleFactor)
        apContentLayoutConstraintSet.setScaleY(apSplashScreenLayersLayout.id, scaleFactor)
        apContentLayoutConstraintSet.applyTo(apContentLayout)
    }

    private fun setupObservers() {
        viewModel.isSplashScreenReadyLiveData.observe(viewLifecycleOwner, isSplashScreenReadyObserver)
    }

    private val isSplashScreenReadyObserver = Observer<Boolean> { isReady ->
        if (isReady) {
            countDownTimer?.start()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        countDownTimer?.cancel()
        viewModel.increaseSplashScreenWatchedCount()
        super.onDismiss(dialog)
        listener?.onDismiss()
    }
}