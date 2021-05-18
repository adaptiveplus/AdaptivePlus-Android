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
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER
import plus.adaptive.sdk.data.models.APSplashScreen
import plus.adaptive.sdk.ui.splashscreen.vm.APSplashScreenDialogViewModel
import plus.adaptive.sdk.ui.splashscreen.vm.APSplashScreenDialogViewModelFactory
import plus.adaptive.sdk.utils.drawAPLayersOnLayout
import plus.adaptive.sdk.utils.safeRun


internal class APSplashScreenDialog : DialogFragment() {

    companion object {
        private const val EXTRA_SPLASH_SCREEN_WIDTH = 375 * BASE_SIZE_MULTIPLIER
        private const val EXTRA_SPLASH_SCREEN_HEIGHT = 667 * BASE_SIZE_MULTIPLIER

        private const val EXTRA_SCREEN_WIDTH = "extra_screen_width"
        private const val EXTRA_SPLASH_SCREEN = "extra_splash_screen"

        @JvmStatic
        fun newInstance(
            baseScreenWidth: Double,
            splashScreen: APSplashScreen,
            listener: APSplashScreenDialogListener? = null
        ) = APSplashScreenDialog().apply {
            arguments = bundleOf(
                EXTRA_SCREEN_WIDTH to baseScreenWidth,
                EXTRA_SPLASH_SCREEN to splashScreen
            )
            this.listener = listener
        }
    }


    private var baseScreenWidth: Double? = null
    private var listener: APSplashScreenDialogListener? = null
    private lateinit var splashScreen: APSplashScreen
    private lateinit var viewModel: APSplashScreenDialogViewModel

    private var countDownTimer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.APSplashScreenDialogTheme)

        (arguments?.getSerializable(EXTRA_SPLASH_SCREEN) as? APSplashScreen)?.let {
            this.splashScreen = it
        }
        (arguments?.getDouble(EXTRA_SCREEN_WIDTH, -1.0))?.let {
            if (it >= 0.0) {
                this.baseScreenWidth = it
            }
        }

        if (!::splashScreen.isInitialized || baseScreenWidth == null) {
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
        apSkipTextView.setOnClickListener {
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
            apSplashScreenLayersLayout.id, EXTRA_SPLASH_SCREEN_WIDTH)
        apContentLayoutConstraintSet.constrainHeight(
            apSplashScreenLayersLayout.id, EXTRA_SPLASH_SCREEN_HEIGHT)
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
        if (apSplashScreenLayout.width == 0 || baseScreenWidth == null) {
            return
        }
        val scaleFactor = baseScreenWidth?.let {
            (apSplashScreenLayout.width / it).toFloat()
        } ?: 1f

        val apSnapLayoutConstraintSet = ConstraintSet()
        apSnapLayoutConstraintSet.clone(apSplashScreenLayout)
        apSnapLayoutConstraintSet.constrainWidth(
            apContentCardView.id, (EXTRA_SPLASH_SCREEN_WIDTH * scaleFactor).toInt())
        apSnapLayoutConstraintSet.constrainHeight(
            apContentCardView.id, (EXTRA_SPLASH_SCREEN_HEIGHT * scaleFactor).toInt())
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