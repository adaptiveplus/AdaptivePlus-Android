package com.sprintsquads.adaptiveplus.ui.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.core.managers.AdaptiveActionsManager
import com.sprintsquads.adaptiveplus.core.providers.provideAdaptiveActionsManager
import com.sprintsquads.adaptiveplus.data.models.AdaptiveAction
import com.sprintsquads.adaptiveplus.data.models.AdaptiveTagTemplate
import com.sprintsquads.adaptiveplus.data.models.EventObserver
import com.sprintsquads.adaptiveplus.data.models.network.RequestState
import com.sprintsquads.adaptiveplus.extensions.hide
import com.sprintsquads.adaptiveplus.extensions.show
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.sdk.data.AdaptiveCustomAction
import com.sprintsquads.adaptiveplus.ui.entry.AdaptiveEntriesView
import com.sprintsquads.adaptiveplus.ui.tag.vm.AdaptiveTagViewModel
import com.sprintsquads.adaptiveplus.ui.tag.vm.AdaptiveTagViewModelFactory
import com.sprintsquads.adaptiveplus.utils.isTagTemplateNullOrEmpty
import kotlinx.android.synthetic.main.ap_fragment_adaptive_tag.*


internal class AdaptiveTagFragment : Fragment() {

    companion object {
        private const val EXTRA_TAG_ID = "extra_tag_id"

        @JvmStatic
        fun newInstance(
            tagId: String,
        ) = AdaptiveTagFragment().apply {
            arguments = bundleOf(
                EXTRA_TAG_ID to tagId
            )
        }
    }


    private lateinit var viewModel: AdaptiveTagViewModel
    private lateinit var tagId: String

    private var adaptiveEntriesView: AdaptiveEntriesView? = null

    private var actionsManager: AdaptiveActionsManager? = null
    private var customActionCallback: AdaptiveCustomAction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!::tagId.isInitialized) {
            tagId = arguments?.getString(EXTRA_TAG_ID) ?: ""
        }

        activity?.let {
            val viewModelFactory = AdaptiveTagViewModelFactory(it.application)
            viewModel = ViewModelProvider(this, viewModelFactory).get(AdaptiveTagViewModel::class.java)

            actionsManager = provideAdaptiveActionsManager(it, childFragmentManager, viewModel)
            customActionCallback?.let { callback ->
                actionsManager?.setAdaptiveCustomActionCallback(callback)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_adaptive_tag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        checkSdkIsStarted()

        hideTagFragment()

        // Mock is used only for development and testing purposes, not for release
        if (context?.packageName == "com.sprintsquads.adaptiveplusqaapp" &&
                tagId.startsWith("mock")
        ) {
            viewModel.loadMockTemplateFromAssets(tagId)
        }
        else {
            refresh()
        }
    }

    override fun onDestroyView() {
        adaptiveEntriesView = null
        super.onDestroyView()
    }

    private fun setupObservers() {
        view?.let {
            AdaptivePlusSDK().getTokenLiveData().observe(viewLifecycleOwner, tokenObserver)
            viewModel.tagTemplateLiveData.observe(viewLifecycleOwner, tagTemplateObserver)
            viewModel.actionEventLiveData.observe(viewLifecycleOwner, actionEventObserver)
        }
    }

    private val tokenObserver = Observer<String?> { token ->
        checkSdkIsStarted()

        if (token != null) {
            viewModel.requestTemplate(tagId)
        }
        else if (AdaptivePlusSDK().getTokenRequestState() == RequestState.ERROR) {
            if (viewModel.tagTemplateLiveData.value == null) {
                hideTagFragment()
            } else {
                showTagFragment()
            }
        }
    }

    private val tagTemplateObserver = Observer<AdaptiveTagTemplate?> { template ->
        checkSdkIsStarted()

        if (isTagTemplateNullOrEmpty(template)) {
            hideTagFragment()
        }
        else {
            showTagFragment()
            drawComponent(template!!)
        }
    }

    private val actionEventObserver =
            EventObserver<Pair<AdaptiveAction, String>> {
                actionsManager?.runAction(action = it.first, campaignId = it.second)
            }

    fun refresh() {
        if (!::viewModel.isInitialized) {
            return
        }

        if (viewModel.tagTemplateLiveData.value == null) {
            viewModel.loadTemplateFromCache(tagId)
        }

        val sdk = AdaptivePlusSDK()
        val tokenLiveData = sdk.getTokenLiveData()

        if (tokenLiveData.value == null) {
            context?.let { sdk.authorize(it) }
        }
        else {
            viewModel.requestTemplate(tagId)
        }
    }

    fun setAdaptiveCustomActionCallback(callback: AdaptiveCustomAction) {
        this.customActionCallback = callback
        actionsManager?.setAdaptiveCustomActionCallback(callback)
    }

    fun setTagId(tagId: String) {
        this.tagId = tagId

        refresh()
    }

    private fun drawComponent(template: AdaptiveTagTemplate) {
        if (context == null || view == null) return

        showTagFragment()

        if (adaptiveEntriesView != null) {
            adaptiveEntriesView?.updateEntries(template.entries)
        } else {
            apEntriesContainer.removeAllViews()

            context?.let { ctx ->
                AdaptiveEntriesView(ctx, viewModel, template.entries).run {
                    adaptiveEntriesView = this
                    addViewToAdaptiveContainer(this)
                }
            }
        }
    }

    private fun addViewToAdaptiveContainer(view: View) {
        val layoutParams =
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

        apEntriesContainer.addView(view, layoutParams)
    }

    private fun checkSdkIsStarted() {
        if (!AdaptivePlusSDK().isStarted()) {
            hideTagFragment()
        }
    }

    private fun hideTagFragment() {
        apAdaptiveTagFragment?.hide()
    }

    private fun showTagFragment() {
        if (AdaptivePlusSDK().isStarted()) {
            apAdaptiveTagFragment?.show()
        }
    }
}