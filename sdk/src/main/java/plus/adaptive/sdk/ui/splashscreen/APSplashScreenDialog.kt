package plus.adaptive.sdk.ui.splashscreen

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.view.KeyEvent.KEYCODE_BACK
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.ap_fragment_splash_screen_dialog.*
import kotlinx.android.synthetic.main.ap_fragment_splash_screen_dialog.apContentCardView
import kotlinx.android.synthetic.main.ap_fragment_splash_screen_dialog.apContentLayout
import plus.adaptive.sdk.R
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.APSplashScreen
import plus.adaptive.sdk.data.models.APSplashScreenViewDataModel
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ext.show
import plus.adaptive.sdk.ui.dialogs.APDialogFragment
import plus.adaptive.sdk.ui.splashscreen.vm.APSplashScreenDialogViewModel
import plus.adaptive.sdk.ui.splashscreen.vm.APSplashScreenDialogViewModelFactory
import plus.adaptive.sdk.utils.drawAPLayersOnLayout
import plus.adaptive.sdk.utils.safeRun


internal class APSplashScreenDialog : DialogFragment(), APDialogFragment {

    companion object {
        private const val DEFAULT_SHOW_TIME_IN_SECS = 3

        private const val EXTRA_SPLASH_SCREEN = "extra_splash_screen"
        private const val EXTRA_OPTIONS = "extra_options"
        private const val EXTRA_AP_VIEW_ID = "extra_ap_view_id"

        @JvmStatic
        fun newInstance(
            splashScreen: APSplashScreen,
            options: APSplashScreenViewDataModel.Options,
            apViewId: String
        ) = APSplashScreenDialog().apply {
            arguments = bundleOf(
                EXTRA_SPLASH_SCREEN to splashScreen,
                EXTRA_OPTIONS to options,
                EXTRA_AP_VIEW_ID to apViewId
            )
        }
    }


    private lateinit var splashScreen: APSplashScreen
    private lateinit var options: APSplashScreenViewDataModel.Options
    private lateinit var viewModel: APSplashScreenDialogViewModel
    private lateinit var apViewId: String

    private var viewControllerDelegate: APSplashScreenViewControllerDelegateProtocol? = null
    private var countDownTimer: CountDownTimer? = null
    private var showTimeInMillis = 0L
    private var millisUntilFinished = 0L

    private val onDismissListeners = mutableSetOf<APDialogFragment.OnDismissListener>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.APSplashScreenDialogTheme)

        (arguments?.getSerializable(EXTRA_SPLASH_SCREEN) as? APSplashScreen)?.let {
            this.splashScreen = it
        }
        (arguments?.getSerializable(EXTRA_OPTIONS) as? APSplashScreenViewDataModel.Options)?.let {
            this.options = it
        }
        this.apViewId = arguments?.getString(EXTRA_AP_VIEW_ID) ?: ""

        if (!::splashScreen.isInitialized || !::options.isInitialized) {
            dismiss()
            return
        }

        val viewModelFactory = APSplashScreenDialogViewModelFactory(context, splashScreen)
        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(APSplashScreenDialogViewModel::class.java)

        viewModel.increaseSplashScreenWatchedCount()

        APAnalytics.logEvent(
            APAnalyticsEvent(
                name = "shown-splash-screen",
                campaignId = splashScreen.campaignId,
                apViewId = apViewId,
                params = mapOf("splashScreenId" to splashScreen.id)
            )
        )
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

        val showTimeInSeconds = splashScreen.showTime?.toInt() ?: DEFAULT_SHOW_TIME_IN_SECS
        this.showTimeInMillis = showTimeInSeconds * 1000L
        this.millisUntilFinished = showTimeInMillis

        apSkipTextView.text = getString(R.string.ap_skip, showTimeInSeconds)
        apSkipBtnLayout.setOnClickListener {
            skipSplashScreen()
        }

        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KEYCODE_BACK) {
                if (event.action != KeyEvent.ACTION_DOWN) {
                    skipSplashScreen()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        countDownTimer = object: CountDownTimer(showTimeInMillis, 100L) {
            override fun onTick(millisUntilFinished: Long) {
                this@APSplashScreenDialog.millisUntilFinished = millisUntilFinished
                val timeLeft = maxOf(((millisUntilFinished + 900) / 1000).toInt(), 1)
                apSkipTextView?.text = getString(R.string.ap_skip, timeLeft)
            }

            override fun onFinish() {
                this@APSplashScreenDialog.millisUntilFinished = 0L
                dismiss()
            }
        }

        drawSplashScreen()

        apSplashScreenLayout.addOnLayoutChangeListener(apSplashScreenLayoutChangeListener)

        if (splashScreen.status == APSplashScreen.Status.DRAFT) {
            apTagTextView.show()
        } else {
            apTagTextView.hide()
        }

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
            splashScreen.actions?.let { actions ->
                if (actions.isNotEmpty()) {
                    apContentCardView?.setOnClickListener {
                        APAnalytics.logEvent(
                            APAnalyticsEvent(
                                name = "action-splash-screen",
                                campaignId = splashScreen.campaignId,
                                apViewId = apViewId,
                                params = mapOf("splashScreenId" to splashScreen.id)
                            )
                        )
                        viewControllerDelegate?.runActions(actions)
                        dismiss()
                    }
                }
            }
            countDownTimer?.start()
        }
    }

    private fun skipSplashScreen() {
        APAnalytics.logEvent(
            APAnalyticsEvent(
                name = "skipped-splash-screen",
                campaignId = splashScreen.campaignId,
                apViewId = apViewId,
                params = mapOf(
                    "splashScreenId" to splashScreen.id,
                    "watchedTime" to (showTimeInMillis - millisUntilFinished) / 1000.0
                )
            )
        )
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        countDownTimer?.cancel()
        onDismissListeners.forEach { it.onDismiss() }
    }

    override fun addOnDismissListener(listener: APDialogFragment.OnDismissListener) {
        this.onDismissListeners.add(listener)
    }

    override fun removeOnDismissListener(listener: APDialogFragment.OnDismissListener) {
        this.onDismissListeners.remove(listener)
    }

    override fun clearAllOnDismissListeners() {
        this.onDismissListeners.clear()
    }

    fun setViewControllerDelegate(delegate: APSplashScreenViewControllerDelegateProtocol?) {
        this.viewControllerDelegate = delegate
    }
}