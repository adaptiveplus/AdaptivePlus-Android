package com.sprintsquads.adaptiveplus.ui.tag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var entriesAdapter: AdaptiveEntriesAdapter

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

        updateTagFragmentVisibility()

        entriesAdapter = AdaptiveEntriesAdapter(listOf())
        val layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false)
        apEntriesRecyclerView.layoutManager = layoutManager
        apEntriesRecyclerView.adapter = entriesAdapter

        apAdaptiveTagFragmentLayout.addOnLayoutChangeListener(tagFragmentLayoutChangeListener)

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

    private fun setupObservers() {
        view?.let {
            AdaptivePlusSDK().getTokenLiveData().observe(viewLifecycleOwner, tokenObserver)
            AdaptivePlusSDK().isStartedLiveData().observe(viewLifecycleOwner, isSdkStartedObserver)
            viewModel.tagTemplateLiveData.observe(viewLifecycleOwner, tagTemplateObserver)
            viewModel.actionEventLiveData.observe(viewLifecycleOwner, actionEventObserver)
        }
    }

    private val tokenObserver = Observer<String?> { token ->
        if (token != null) {
            viewModel.requestTemplate(tagId)
        }
        else if (AdaptivePlusSDK().getTokenRequestState() == RequestState.ERROR) {
            updateTagFragmentVisibility()
        }
    }

    private val isSdkStartedObserver = Observer<Boolean> {
        updateTagFragmentVisibility()
    }

    private val tagTemplateObserver = Observer<AdaptiveTagTemplate?> { template ->
        if (isTagTemplateNullOrEmpty(template)) {
            updateTagFragmentVisibility()
        }
        else {
            updateTagFragmentVisibility()
            drawTag(template!!)
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

    private fun drawTag(template: AdaptiveTagTemplate) {
        if (context == null || view == null || template.options.isViewless) return

        updateTagFragmentVisibility()

        entriesAdapter.updateDataSet(template.entries)

        updateEntriesViewOptions()
    }

    private val tagFragmentLayoutChangeListener = View.OnLayoutChangeListener {
        v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            updateEntriesViewOptions()
        }
    }

    private fun updateEntriesViewOptions() {
        viewModel.tagTemplateLiveData.value?.options?.let { options ->
            val templateScreenWidth = maxOf(options.screenWidth, 0.001)
            val scaleFactor = (apAdaptiveTagFragmentLayout.width / templateScreenWidth).toFloat()

            options.padding.run {
                apEntriesRecyclerView.setPadding(
                    (left * scaleFactor).toInt(),
                    (top * scaleFactor).toInt(),
                    (right * scaleFactor).toInt(),
                    (bottom * scaleFactor).toInt())
            }

            while (apEntriesRecyclerView.itemDecorationCount > 0) {
                apEntriesRecyclerView.removeItemDecorationAt(0)
            }
            apEntriesRecyclerView.addItemDecoration(
                EntrySpaceDecoration((options.spacing * scaleFactor).toInt()))

            entriesAdapter.updateEntryOptions(
                options = AdaptiveEntriesAdapter.EntryOptions(
                    width = options.width,
                    height = options.height,
                    cornerRadius = options.cornerRadius
                ),
                scaleFactor = scaleFactor
            )
        }
    }

    private fun updateTagFragmentVisibility() {
        if (AdaptivePlusSDK().isStartedLiveData().value == true &&
            !isTagTemplateNullOrEmpty(viewModel.tagTemplateLiveData.value)
        ) {
            apAdaptiveTagFragmentLayout?.show()
        } else {
            apAdaptiveTagFragmentLayout?.hide()
        }
    }
}