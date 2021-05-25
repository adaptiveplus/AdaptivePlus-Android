package plus.adaptive.sdk.core.managers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.data.listeners.APCustomActionListener
import plus.adaptive.sdk.data.models.actions.*
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.actions.APCustomAction
import plus.adaptive.sdk.data.models.actions.APOpenWebLinkAction
import plus.adaptive.sdk.data.models.actions.APSendSMSAction
import plus.adaptive.sdk.data.models.actions.APShowStoryAction
import plus.adaptive.sdk.ui.ViewControllerDelegateProtocol
import plus.adaptive.sdk.ui.apview.vm.APViewVMDelegateProtocol
import plus.adaptive.sdk.ui.dialogs.WebViewDialog
import plus.adaptive.sdk.ui.stories.APStoriesDialog


internal class APActionsManagerImpl(
    private val viewControllerDelegate: ViewControllerDelegateProtocol,
    private val apViewVMDelegate: APViewVMDelegateProtocol?
) : APActionsManager {

    private var apCustomActionListener: APCustomActionListener? = null
    private var apStories: List<APStory>? = null


    override fun setAPStories(apStories: List<APStory>?) {
        this.apStories = apStories
    }

    override fun setAPCustomActionListener(listener: APCustomActionListener?) {
        this.apCustomActionListener = listener
    }

    override fun runAction(action: APAction) {
        when (action) {
            is APOpenWebLinkAction -> openWebView(action)
            is APCustomAction -> runAPCustomAction(action)
            is APShowStoryAction -> showAPStory(action)
            is APSendSMSAction -> sendSms(action)
            is APCallPhoneAction -> callPhone(action)
            else -> {}
        }
    }

    private fun openWebView(action: APOpenWebLinkAction) {
        if (action.isWebView == true) {
            apViewVMDelegate?.pauseAPStories()

            val webViewDialog = WebViewDialog.newInstance(action.url).apply {
                addOnDismissListener {
                    apViewVMDelegate?.resumeAPStories()
                }
            }
            viewControllerDelegate.showDialog(webViewDialog)
        } else {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
                viewControllerDelegate.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun runAPCustomAction(action: APCustomAction) {
        action.parameters?.let {
            viewControllerDelegate.dismissAllDialogs()
            apCustomActionListener?.onRun(it)
        }
    }

    private fun showAPStory(action: APShowStoryAction) {
        apStories?.let { stories ->
            val storyIndex = stories.indexOfFirst { it.id == action.story.id }

            if (storyIndex != -1 && apViewVMDelegate != null) {
                try {
                    val apStoriesDialog = APStoriesDialog
                        .newInstance(stories, storyIndex, apViewVMDelegate)
                    viewControllerDelegate.showDialog(apStoriesDialog)
                } catch (e: IllegalStateException) {
                    APCrashlytics.logCrash(e)
                    e.printStackTrace()
                }
            }
        }
    }

    private fun sendSms(action: APSendSMSAction) {
        val uri = Uri.parse("smsto:${action.phoneNumber}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", action.message)
        viewControllerDelegate.startActivity(intent)
    }

    private fun callPhone(action: APCallPhoneAction) {
        val uri = Uri.parse("tel:${action.phoneNumber}")
        val intent = Intent(Intent.ACTION_DIAL, uri)
        viewControllerDelegate.startActivity(intent)
    }
}