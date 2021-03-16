package com.sprintsquads.adaptiveplus.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sprintsquads.adaptiveplus.R
import kotlinx.android.synthetic.main.ap_fragment_webview_dialog.*


internal class WebViewDialog : BottomSheetDialogFragment() {

    companion object {
        private const val EXTRA_URL = "url"

        @JvmStatic
        fun newInstance(url: String, delegate: Delegate? = null) =
            WebViewDialog().apply {
                arguments = bundleOf(EXTRA_URL to url)
                this.delegate = delegate
            }
    }


    interface Delegate {
        fun onDismiss()
    }


    private var delegate: Delegate? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.APBottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog

            setupFullHeight(bottomSheetDialog)

            bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING &&
                                (apWebView?.scrollY ?: 0) > 0
                            ) {
                                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }
                    })
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)!!
        val behavior = bottomSheetDialog.behavior
        val layoutParams = bottomSheet.layoutParams as ViewGroup.LayoutParams

        val windowHeight = getWindowHeight()
        layoutParams.height = windowHeight
        bottomSheet.layoutParams = layoutParams
        behavior.peekHeight = 0
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED ||
                    newState == BottomSheetBehavior.STATE_HALF_EXPANDED
                ) {
                    dismiss()
                }
            }
        })
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_webview_dialog, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = arguments?.getString(EXTRA_URL)

        if (url.isNullOrEmpty()) {
            dismiss()
            return
        }

        apTitleTextView.text = url

        apCloseButton.setOnClickListener {
            dismiss()
        }

        apWebView.settings.javaScriptEnabled = true
        apWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        apWebView.settings.setSupportZoom(true)
        apWebView.settings.builtInZoomControls = true
        apWebView.settings.displayZoomControls = false
        apWebView.settings.allowFileAccess = true
        apWebView.settings.useWideViewPort = true
        apWebView.settings.loadWithOverviewMode = true

        apWebView.isHorizontalScrollBarEnabled = false
        apWebView.isFocusable = true

        apWebView.webChromeClient = WebChromeClient()
        apWebView.webViewClient = WebViewClient()

        apWebView.loadUrl(url)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        delegate?.onDismiss()
    }
}