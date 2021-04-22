package plus.adaptive.sdk.core.managers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import plus.adaptive.sdk.data.models.APStory
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.actions.APCustomAction
import plus.adaptive.sdk.data.models.actions.APOpenWebLinkAction
import plus.adaptive.sdk.data.models.actions.APShowStoryAction
import plus.adaptive.sdk.data.listeners.APCustomActionListener
import plus.adaptive.sdk.ui.apview.APViewDelegateProtocol
import plus.adaptive.sdk.ui.apview.vm.APViewModelDelegateProtocol
import plus.adaptive.sdk.ui.dialogs.WebViewDialog
import plus.adaptive.sdk.ui.stories.APStoriesDialog


internal class APActionsManagerImpl(
    private val apViewDelegate: APViewDelegateProtocol,
    private val apViewModelDelegate: APViewModelDelegateProtocol
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
            else -> {}
        }
    }

    private fun openWebView(action: APOpenWebLinkAction) {
        if (action.url.startsWith("http")) {
            apViewModelDelegate.pauseAPStories()

            val webViewDialog = WebViewDialog.newInstance(
                action.url,
                object: WebViewDialog.LifecycleListener {
                    override fun onDismiss() {
                        apViewModelDelegate.resumeAPStories()
                    }
                })
            apViewDelegate.showDialog(webViewDialog)
        } else {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
                apViewDelegate.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun runAPCustomAction(action: APCustomAction) {
        action.parameters?.let {
            apViewDelegate.dismissAllDialogs()
            apCustomActionListener?.onRun(it)
        }
    }

    private fun showAPStory(action: APShowStoryAction) {
        apStories?.let { stories ->
            val storyIndex = stories.indexOfFirst { it.id == action.story.id }

            if (storyIndex != -1) {
                try {
                    val apStoriesDialog = APStoriesDialog
                        .newInstance(stories, storyIndex, apViewModelDelegate)
                    apViewDelegate.showDialog(apStoriesDialog)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }
}