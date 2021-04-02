package com.sprintsquads.adaptiveplus.core.managers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction
import com.sprintsquads.adaptiveplus.ui.apview.APViewDelegateProtocol
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegateProtocol
import com.sprintsquads.adaptiveplus.ui.dialogs.WebViewDialog
import com.sprintsquads.adaptiveplus.ui.stories.APStoriesDialog
import com.sprintsquads.adaptiveplus.utils.deserializeAPActionParams


internal class APActionsManagerImpl(
    private val apViewDelegate: APViewDelegateProtocol,
    private val apViewModelDelegate: APViewModelDelegateProtocol
) : APActionsManager {

    companion object {
        private const val PARAM_URL = "url"
    }


    private var apCustomAction: APCustomAction? = null
    private var apStories: List<APStory>? = null


    override fun setAPStories(apStories: List<APStory>?) {
        this.apStories = apStories
    }

    override fun setAPCustomAction(apCustomAction: APCustomAction?) {
        this.apCustomAction = apCustomAction
    }

    override fun runAction(action: APAction, campaignId: String) {
        when (action.type) {
            APAction.Type.OPEN_WEB_LINK -> openWebView(action)
            APAction.Type.CUSTOM -> runAPCustomAction(action)
            APAction.Type.SHOW_STORY -> showAPStory(action)
        }
    }

    private fun openWebView(action: APAction) {
        (action.parameters?.get(PARAM_URL) as? String)?.let { url ->
            if (url.startsWith("http")) {
                apViewModelDelegate.pauseAPStories()

                val webViewDialog = WebViewDialog.newInstance(
                    url,
                    object: WebViewDialog.LifecycleListener {
                        override fun onDismiss() {
                            apViewModelDelegate.resumeAPStories()
                        }
                    })
                apViewDelegate.showDialog(webViewDialog)
            } else {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    apViewDelegate.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun runAPCustomAction(action: APAction) {
        action.parameters?.let {
            apViewDelegate.dismissAllDialogs()
            apCustomAction?.onRun(it)
        }
    }

    private fun showAPStory(action: APAction) {
        deserializeAPActionParams(action)

        (action.parameters?.get("story") as? APStory)?.let { story ->
            val storyIndex = apStories?.indexOfFirst { it.id == story.id }

            if (storyIndex != null && storyIndex != -1 && apStories != null) {
                try {
                    val apStoriesDialog = APStoriesDialog
                        .newInstance(apStories!!, storyIndex, apViewModelDelegate)
                    apViewDelegate.showDialog(apStoriesDialog)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }
}