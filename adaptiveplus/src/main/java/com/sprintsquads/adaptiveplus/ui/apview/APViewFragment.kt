package com.sprintsquads.adaptiveplus.ui.apview

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
import com.sprintsquads.adaptiveplus.core.managers.APActionsManager
import com.sprintsquads.adaptiveplus.core.providers.provideAPActionsManager
import com.sprintsquads.adaptiveplus.data.models.APAction
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.EventObserver
import com.sprintsquads.adaptiveplus.data.models.network.RequestState
import com.sprintsquads.adaptiveplus.extensions.hide
import com.sprintsquads.adaptiveplus.extensions.show
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModel
import com.sprintsquads.adaptiveplus.ui.apview.vm.APViewModelFactory
import com.sprintsquads.adaptiveplus.utils.getAPStoriesList
import com.sprintsquads.adaptiveplus.utils.isAPViewDataModelNullOrEmpty
import kotlinx.android.synthetic.main.ap_fragment_ap_view.*


internal class APViewFragment : Fragment() {

    companion object {
        private const val EXTRA_AP_VIEW_ID = "extra_ap_view_id"

        @JvmStatic
        fun newInstance(
            apViewId: String,
        ) = APViewFragment().apply {
            arguments = bundleOf(
                EXTRA_AP_VIEW_ID to apViewId
            )
        }
    }


    private lateinit var viewModel: APViewModel
    private lateinit var apViewId: String
    private lateinit var entryPointsAdapter: APEntryPointsAdapter

    private var apActionsManager: APActionsManager? = null
    private var customActionCallback: APCustomAction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!::apViewId.isInitialized) {
            apViewId = arguments?.getString(EXTRA_AP_VIEW_ID) ?: ""
        }

        activity?.let {
            val viewModelFactory = APViewModelFactory(it.application)
            viewModel = ViewModelProvider(this, viewModelFactory).get(APViewModel::class.java)

            apActionsManager = provideAPActionsManager(it, childFragmentManager, viewModel)
            customActionCallback?.let { callback ->
                apActionsManager?.setAPCustomAction(callback)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ap_fragment_ap_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        updateAPViewFragmentVisibility()

        entryPointsAdapter = APEntryPointsAdapter(listOf(), viewModel)
        val layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false)
        apEntriesRecyclerView.layoutManager = layoutManager
        apEntriesRecyclerView.adapter = entryPointsAdapter

        apViewFragmentLayout.addOnLayoutChangeListener(apViewFragmentLayoutChangeListener)

        // Mock is used only for development and testing purposes, not for release
        if (context?.packageName == "com.sprintsquads.adaptiveplusqaapp" &&
                apViewId.startsWith("mock")
        ) {
            viewModel.loadAPViewMockDataModelFromAssets(apViewId)
        }
        else {
            refresh()
        }
    }

    private fun setupObservers() {
        view?.let {
            AdaptivePlusSDK().getTokenLiveData().observe(viewLifecycleOwner, tokenObserver)
            AdaptivePlusSDK().isStartedLiveData().observe(viewLifecycleOwner, isSdkStartedObserver)
            viewModel.apViewDataModelLiveData.observe(viewLifecycleOwner, apViewDataModelObserver)
            viewModel.actionEventLiveData.observe(viewLifecycleOwner, actionEventObserver)
        }
    }

    private val tokenObserver = Observer<String?> { token ->
        if (token != null) {
            viewModel.requestAPViewDataModel(apViewId)
        }
        else if (AdaptivePlusSDK().getTokenRequestState() == RequestState.ERROR) {
            updateAPViewFragmentVisibility()
        }
    }

    private val isSdkStartedObserver = Observer<Boolean> {
        updateAPViewFragmentVisibility()
    }

    private val apViewDataModelObserver = Observer<APViewDataModel?> { dataModel ->
        updateAPViewFragmentVisibility()
        apActionsManager?.setAPStories(getAPStoriesList(dataModel))

        if (!isAPViewDataModelNullOrEmpty(dataModel)) {
            drawAPView(dataModel!!)
        }
    }

    private val actionEventObserver =
            EventObserver<Pair<APAction, String>> {
                apActionsManager?.runAction(action = it.first, campaignId = it.second)
            }

    fun refresh() {
        if (!::viewModel.isInitialized) {
            return
        }

        if (viewModel.apViewDataModelLiveData.value == null) {
            viewModel.loadAPViewDataModelFromCache(apViewId)
        }

        val sdk = AdaptivePlusSDK()
        val tokenLiveData = sdk.getTokenLiveData()

        if (tokenLiveData.value == null) {
            context?.let { sdk.authorize(it) }
        }
        else {
            viewModel.requestAPViewDataModel(apViewId)
        }
    }

    fun setAPCustomAction(callback: APCustomAction) {
        this.customActionCallback = callback
        apActionsManager?.setAPCustomAction(callback)
    }

    fun setAPViewId(apViewId: String) {
        this.apViewId = apViewId

        refresh()
    }

    private fun drawAPView(apViewDataModel: APViewDataModel) {
        if (context == null || view == null) return

        entryPointsAdapter.updateDataSet(apViewDataModel.entryPoints)

        updateEntriesViewOptions()
    }

    private val apViewFragmentLayoutChangeListener = View.OnLayoutChangeListener {
        v, _, _, _, _, oldLeft, _, oldRight, _ ->

        val oldWidth = oldRight - oldLeft
        if (v.width != oldWidth) {
            updateEntriesViewOptions()
        }
    }

    private fun updateEntriesViewOptions() {
        viewModel.apViewDataModelLiveData.value?.options?.let { options ->
            val baseScreenWidth = maxOf(options.screenWidth, 0.001)
            val scaleFactor = (apViewFragmentLayout.width / baseScreenWidth).toFloat()

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
                APEntryPointSpaceDecoration((options.spacing * scaleFactor).toInt()))

            entryPointsAdapter.updateEntryOptions(
                options = APEntryPointsAdapter.EntryOptions(
                    width = options.width,
                    height = options.height,
                    cornerRadius = options.cornerRadius
                ),
                scaleFactor = scaleFactor
            )
        }
    }

    private fun updateAPViewFragmentVisibility() {
        if (AdaptivePlusSDK().isStartedLiveData().value == true &&
            !isAPViewDataModelNullOrEmpty(viewModel.apViewDataModelLiveData.value)
        ) {
            apViewFragmentLayout?.show()
        } else {
            apViewFragmentLayout?.hide()
        }
    }
}