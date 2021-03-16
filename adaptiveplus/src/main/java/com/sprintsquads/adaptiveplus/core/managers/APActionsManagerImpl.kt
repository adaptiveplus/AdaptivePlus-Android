package com.sprintsquads.adaptiveplus.core.managers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegate
import com.sprintsquads.adaptiveplus.ui.dialogs.WebViewDialog
import com.sprintsquads.adaptiveplus.ui.stories.APStoriesDialog


internal class APActionsManagerImpl(
    private val fragmentActivity: FragmentActivity,
    private val fragmentManager: FragmentManager,
    private val apViewModelDelegate: APViewModelDelegate
) : APActionsManager {

    companion object {
        private const val PARAM_URL = "url"
        private const val PARAM_STORY_ID = "storyId"
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
        when (action.kind) {
            APAction.Kind.OPEN_WEB_LINK -> openWebView(action)
            APAction.Kind.CUSTOM -> runAPCustomAction(action)
            APAction.Kind.SHOW_STORY -> showAPStory(action)
        }
    }

    private fun openWebView(action: APAction) {
        (action.params?.get(PARAM_URL) as? String)?.let { url ->
            if (url.startsWith("http")) {
                apViewModelDelegate.pauseAPStories()

                val webViewDialog = WebViewDialog.newInstance(
                    url,
                    object: WebViewDialog.LifecycleListener {
                        override fun onDismiss() {
                            apViewModelDelegate.resumeAPStories()
                        }
                    })
                webViewDialog.show(fragmentManager, webViewDialog.tag)
            } else {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    fragmentActivity.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun runAPCustomAction(action: APAction) {
        action.params?.let {
            dismissAllDialogs()
            apCustomAction?.onRun(it)
        }
    }

    private fun dismissAllDialogs() {
        try {
            for (fragment in fragmentManager.fragments) {
                if (fragment != null && fragment is DialogFragment) {
                    fragment.dismiss()
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun showAPStory(action: APAction) {
        (action.params?.get(PARAM_STORY_ID) as? String)?.let { storyId ->
            val storyIndex = apStories?.indexOfFirst { it.id == storyId }

            if (storyIndex != null && storyIndex != -1 && apStories != null) {
                try {
                    val apStoriesDialog = APStoriesDialog
                        .newInstance(apStories!!, storyIndex).apply {
                            setAPViewModelDelegate(apViewModelDelegate)
                        }
                    apStoriesDialog.show(fragmentManager, apStoriesDialog.tag)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }
}