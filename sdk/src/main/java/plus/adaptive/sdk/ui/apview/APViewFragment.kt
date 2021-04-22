package plus.adaptive.sdk.ui.apview

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import plus.adaptive.sdk.R
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.core.managers.APActionsManager
import plus.adaptive.sdk.core.providers.provideAPActionsManager
import plus.adaptive.sdk.core.providers.provideNetworkServiceManager
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.actions.APAction
import plus.adaptive.sdk.data.models.APViewDataModel
import plus.adaptive.sdk.data.models.EventObserver
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ext.show
import plus.adaptive.sdk.AdaptivePlusSDK
import plus.adaptive.sdk.data.listeners.APCustomActionListener
import plus.adaptive.sdk.ui.apview.vm.APViewModel
import plus.adaptive.sdk.ui.apview.vm.APViewModelFactory
import plus.adaptive.sdk.utils.getAPStoriesList
import plus.adaptive.sdk.utils.isAPViewDataModelNullOrEmpty
import kotlinx.android.synthetic.main.ap_fragment_ap_view.*


internal class APViewFragment : Fragment(), APViewDelegateProtocol {

    companion object {
        private const val MILLISECONDS_PER_INCH = 100f

        private const val EXTRA_AP_VIEW_ID = "extra_ap_view_id"
        private const val EXTRA_AP_HAS_DRAFTS = "extra_ap_has_drafts"

        @JvmStatic
        fun newInstance(
            apViewId: String,
            apHasDrafts: Boolean
        ) = APViewFragment().apply {
            arguments = bundleOf(
                EXTRA_AP_VIEW_ID to apViewId,
                EXTRA_AP_HAS_DRAFTS to apHasDrafts
            )
        }
    }


    private lateinit var viewModel: APViewModel
    private lateinit var apViewId: String
    private var apHasDrafts: Boolean? = null
    private lateinit var entryPointsAdapter: APEntryPointsAdapter

    private var apActionsManager: APActionsManager? = null
    private var customActionListener: APCustomActionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!::apViewId.isInitialized) {
            apViewId = arguments?.getString(EXTRA_AP_VIEW_ID) ?: ""
        }

        if (apHasDrafts == null) {
            apHasDrafts = arguments?.getBoolean(EXTRA_AP_HAS_DRAFTS, false)
        }

        APAnalytics.logEvent(
            APAnalyticsEvent(
                name = "launch-apView",
                apViewId = apViewId
            )
        )

        activity?.let {
            val viewModelFactory = APViewModelFactory(it)
            viewModel = ViewModelProvider(this, viewModelFactory).get(APViewModel::class.java)

            apActionsManager = provideAPActionsManager(this, viewModel)
            customActionListener?.let { callback ->
                apActionsManager?.setAPCustomActionListener(callback)
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
        setupAPEntryPointsRecyclerView()

        apViewFragmentLayout.addOnLayoutChangeListener(apViewFragmentLayoutChangeListener)

        refresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        viewModel.onPause()
        super.onPause()
    }

    private fun setupAPEntryPointsRecyclerView() {
        val layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false)
        apEntryPointsRecyclerView.layoutManager = layoutManager
        apEntryPointsRecyclerView.adapter = entryPointsAdapter
        apEntryPointsRecyclerView.addOnScrollListener(
            object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        autoMagnetize()
                        updateVisibleEntryPointsPositionRange()
                        viewModel.onResume()
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        viewModel.onPause()
                    }
                }
            }
        )
    }

    private fun setupObservers() {
        view?.let {
            val networkManager = provideNetworkServiceManager()
            networkManager.getTokenLiveData().observe(viewLifecycleOwner, tokenObserver)

            AdaptivePlusSDK().isStartedLiveData().observe(viewLifecycleOwner, isSdkStartedObserver)
            viewModel.apViewDataModelLiveData.observe(viewLifecycleOwner, apViewDataModelObserver)
            viewModel.actionEventLiveData.observe(viewLifecycleOwner, actionEventObserver)
            viewModel.magnetizeEntryPointEventLiveData.observe(viewLifecycleOwner, magnetizeEntryPointEventObserver)
        }
    }

    private val tokenObserver = Observer<String?> { token ->
        if (token != null) {
            viewModel.requestAPViewDataModel(apViewId, apHasDrafts ?: false)
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

    private val actionEventObserver = EventObserver<APAction> {
        apActionsManager?.runAction(action = it)
    }

    private val magnetizeEntryPointEventObserver =
        EventObserver<String> {
            val adapterPosition = entryPointsAdapter.positionOfEntryPoint(it)
            magnetizeToPosition(maxOf(adapterPosition, 0))
        }

    private fun autoMagnetize() {
        if (viewModel.apViewDataModelLiveData.value?.options?.magnetize == true) {
            (apEntryPointsRecyclerView?.layoutManager as? LinearLayoutManager)?.run {
                val firstVisiblePos = findFirstVisibleItemPosition()
                val firstCompletelyVisiblePos = findFirstCompletelyVisibleItemPosition()

                if (firstVisiblePos != firstCompletelyVisiblePos) {
                    magnetizeToPosition(firstVisiblePos)
                }
            }
        }
    }

    private fun magnetizeToPosition(adapterPosition: Int) {
        context?.let {
            val layoutManager = apEntryPointsRecyclerView?.layoutManager as? LinearLayoutManager
            val smoothScroller = object: LinearSmoothScroller(context) {
                override fun getHorizontalSnapPreference(): Int {
                    return SNAP_TO_START
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                }
            }
            smoothScroller.targetPosition = adapterPosition
            layoutManager?.startSmoothScroll(smoothScroller)
        }
    }

    private fun updateVisibleEntryPointsPositionRange() {
        val layoutManager = apEntryPointsRecyclerView?.layoutManager as? LinearLayoutManager
        val firstCompVisiblePos = maxOf(
            layoutManager?.findFirstCompletelyVisibleItemPosition() ?: 0,
            layoutManager?.findFirstVisibleItemPosition() ?: 0)
        val lastCompVisiblePos = maxOf(
            layoutManager?.findLastCompletelyVisibleItemPosition() ?: 0,
            firstCompVisiblePos)
        viewModel.setVisibleEntryPointsPositionRange(firstCompVisiblePos..lastCompVisiblePos)
    }

    fun refresh() {
        if (!::viewModel.isInitialized) {
            return
        }

        // Mock is used only for development and testing purposes, not for release
        if (context?.packageName == "plus.adaptive.qaapp" &&
            apViewId.startsWith("mock") &&
            apViewId.length <= 6
        ) {
            viewModel.loadAPViewMockDataModelFromAssets(apViewId)
            return
        }

        if (viewModel.apViewDataModelLiveData.value == null) {
            viewModel.loadAPViewDataModelFromCache(apViewId)
        }

        val networkManager = provideNetworkServiceManager()
        val tokenLiveData = networkManager.getTokenLiveData()

        if (tokenLiveData.value == null) {
            context?.let { AdaptivePlusSDK().authorize(it) }
        }
        else {
            viewModel.requestAPViewDataModel(apViewId, apHasDrafts ?: false)
        }
    }

    fun setAPCustomActionListener(listener: APCustomActionListener) {
        this.customActionListener = listener
        apActionsManager?.setAPCustomActionListener(listener)
    }

    fun setAPViewId(apViewId: String) {
        this.apViewId = apViewId

        refresh()
    }

    fun setHasDrafts(hasDrafts: Boolean) {
        this.apHasDrafts = hasDrafts
    }

    fun scrollToStart() {
        magnetizeToPosition(0)
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
                apEntryPointsRecyclerView.setPadding(
                    (left * scaleFactor).toInt(),
                    (top * scaleFactor).toInt(),
                    (right * scaleFactor).toInt(),
                    (bottom * scaleFactor).toInt())
            }

            while (apEntryPointsRecyclerView.itemDecorationCount > 0) {
                apEntryPointsRecyclerView.removeItemDecorationAt(0)
            }
            apEntryPointsRecyclerView.addItemDecoration(
                APEntryPointSpaceDecoration((options.spacing * scaleFactor).toInt()))

            entryPointsAdapter.updateEntryOptions(
                options = APEntryPointsAdapter.EntryOptions(
                    width = options.width,
                    height = options.height,
                    cornerRadius = options.cornerRadius
                ),
                scaleFactor = scaleFactor
            )

            val layoutManager = apEntryPointsRecyclerView?.layoutManager as? LinearLayoutManager
            layoutManager?.scrollToPosition(0)
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

    override fun showDialog(dialogFragment: DialogFragment) {
        dialogFragment.show(childFragmentManager, dialogFragment.tag)
    }

    override fun dismissAllDialogs() {
        try {
            for (fragment in childFragmentManager.fragments) {
                if (fragment != null && fragment is DialogFragment) {
                    fragment.dismiss()
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}