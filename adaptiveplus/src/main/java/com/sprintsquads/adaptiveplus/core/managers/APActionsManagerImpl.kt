package com.sprintsquads.adaptiveplus.core.managers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelDelegate
import com.sprintsquads.adaptiveplus.ui.dialogs.WebViewDialog


internal class APActionsManagerImpl(
    private val fragmentActivity: FragmentActivity,
    private val fragmentManager: FragmentManager,
    private val apViewModelDelegate: APViewModelDelegate
) : APActionsManager {

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
//            APAction.Kind.SHOW_POPUP_VIEW -> openPopUpDialog(action, campaignId)
//            APAction.Kind.CUSTOM -> runCustomAction(action)
//            APAction.Kind.SHOW_STORY -> showStory(action)
        }
    }

    private fun openWebView(action: APAction) {
        (action.params?.get("url") as? String)?.let { url ->
            if (url.startsWith("http")) {
                apViewModelDelegate.pauseAPStories()

                val webViewDialog = WebViewDialog.newInstance(
                    url,
                    object: WebViewDialog.Delegate {
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
}