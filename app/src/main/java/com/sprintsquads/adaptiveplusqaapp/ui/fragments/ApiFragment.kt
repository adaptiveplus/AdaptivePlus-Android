package com.sprintsquads.adaptiveplusqaapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.sprintsquads.adaptiveplus.sdk.data.AdaptiveCustomAction
import com.sprintsquads.adaptiveplus.sdk.ui.AdaptiveTag
import com.sprintsquads.adaptiveplusqaapp.R
import com.sprintsquads.adaptiveplusqaapp.data.AdaptiveSdkEnvironment
import com.sprintsquads.adaptiveplusqaapp.ui.dialogs.AddNewTagDialog
import com.sprintsquads.adaptiveplusqaapp.ui.dialogs.OnboardingDialog
import com.sprintsquads.adaptiveplusqaapp.utils.getEnvByName
import com.sprintsquads.adaptiveplusqaapp.utils.removeTag
import com.sprintsquads.adaptiveplusqaapp.utils.toast
import kotlinx.android.synthetic.main.fragment_api_page.*


class ApiFragment : Fragment() {

    companion object {
        private const val EXTRA_ENV_NAME = "extra_env_name"

        @JvmStatic
        fun newInstance(envName: String) = ApiFragment().apply {
            arguments = bundleOf(EXTRA_ENV_NAME to envName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_api_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSwipeRefresher.setOnRefreshListener {
            tagsLayout.children.forEach {
                (it as? AdaptiveTag)?.refresh()
            }
            Handler().postDelayed({
                mSwipeRefresher?.isRefreshing = false
            }, 1000)
        }

        setAdaptiveTags()
    }

    private fun setAdaptiveTags() {
        val envName = arguments?.getString(EXTRA_ENV_NAME)

        if (context == null || envName == null) {
            return
        }

        getEnvByName(context!!, envName)?.let { configs ->
            context?.let { ctx ->
                tagsSpinner.adapter = ArrayAdapter<String>(
                    context!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    configs.tags.map { it.id }
                )

                tagsLayout?.removeAllViews()

                for (tag in configs.tags) {
                    val tagNameTxtView = TextView(ctx).apply {
                        val paddingSz = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
                        setPadding(paddingSz, paddingSz, paddingSz, paddingSz)

                        text = "TAG: ${tag.id}"
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        setTextColor(Color.BLACK)
                    }
                    tagsLayout?.addView(tagNameTxtView)

                    when {
//                        tag.isInstructions == true -> {
//                            val apViewlessTag = activity?.let { fragmentActivity ->
//                                AdaptiveViewlessTag(fragmentActivity, childFragmentManager).apply {
//                                    setAdaptiveCustomActionCallback(
//                                        object: AdaptiveCustomAction {
//                                            override fun onRun(params: HashMap<String, Any>) {
//                                                val name = params["name"]?.toString()
//                                                context?.toast("Custom action: $name")
//                                            }
//                                        }
//                                    )
//                                    setOnStoriesFinishedCallback {
//                                        context?.toast("On instructions finished")
//                                    }
//                                }
//                            }
//
//                            val marginPx = TypedValue.applyDimension(
//                                TypedValue.COMPLEX_UNIT_DIP, 16f, ctx.resources.displayMetrics
//                            ).toInt()
//
//                            val instructionsLayout = LinearLayout(ctx).apply {
//                                orientation = LinearLayout.HORIZONTAL
//                            }
//                            val preloadBtn = Button(ctx).apply {
//                                layoutParams = LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT
//                                ).apply {
//                                    weight = 1f
//                                    setMargins(marginPx, 0, marginPx, marginPx)
//                                }
//                                text = "Preload Tag By Id"
//                                setOnClickListener {
//                                    apViewlessTag?.preloadTagById(tag.id)
//                                }
//                            }
//                            val showStoryBtn = Button(ctx).apply {
//                                layoutParams = LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT
//                                ).apply {
//                                    weight = 1f
//                                    setMargins(marginPx, 0, marginPx, marginPx)
//                                }
//                                text = "Show Instructions"
//                                setOnClickListener {
//                                    apViewlessTag?.showStoryByTagId(tag.id)
//                                }
//                            }
//
//                            instructionsLayout.addView(preloadBtn)
//                            instructionsLayout.addView(showStoryBtn)
//                            tagsLayout?.addView(instructionsLayout)
//                        }
//                        tag.isOnboarding == true -> {
//                            val apViewlessTag = activity?.let { fragmentActivity ->
//                                AdaptiveViewlessTag(fragmentActivity, childFragmentManager).apply {
//                                    setAdaptiveCustomActionCallback(object:
//                                        AdaptiveCustomAction {
//                                        override fun onRun(params: HashMap<String, Any>) {
//                                            val name = params["name"]?.toString()
//                                            context?.toast("Custom action: $name")
//                                        }
//                                    })
//                                }
//                            }
//
//                            val marginPx = TypedValue.applyDimension(
//                                TypedValue.COMPLEX_UNIT_DIP, 16f, ctx.resources.displayMetrics
//                            ).toInt()
//
//                            val onboardingLayout = LinearLayout(ctx).apply {
//                                orientation = LinearLayout.HORIZONTAL
//                            }
//                            val preloadBtn = Button(ctx).apply {
//                                layoutParams = LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT
//                                ).apply {
//                                    weight = 1f
//                                    setMargins(marginPx, 0, marginPx, marginPx)
//                                }
//                                text = "Preload Tag By Id"
//                                setOnClickListener {
//                                    apViewlessTag?.preloadTagById(tag.id)
//                                }
//                            }
//                            val showOnboardingBtn = Button(ctx).apply {
//                                layoutParams = LinearLayout.LayoutParams(
//                                    LinearLayout.LayoutParams.MATCH_PARENT,
//                                    LinearLayout.LayoutParams.WRAP_CONTENT
//                                ).apply {
//                                    weight = 1f
//                                    setMargins(marginPx, 0, marginPx, marginPx)
//                                }
//                                text = "Show Onboarding"
//                                setOnClickListener {
//                                    apViewlessTag?.getOnboardingData(tag.id)?.let { onboardingItems ->
//                                        val onboardingDialog = OnboardingDialog.newInstance(
//                                            onboardingItems,
//                                            object: OnboardingDialog.InteractionInterface {
//                                                override fun onRunActions(onboardingItemIndex: Int) {
//                                                    apViewlessTag.runOnboardingActions(tag.id, onboardingItemIndex)
//                                                }
//
//                                                override fun onDismiss() { }
//                                            }
//                                        )
//                                        onboardingDialog.show(childFragmentManager, onboardingDialog.tag)
//                                    }
//                                }
//                            }
//
//                            onboardingLayout.addView(preloadBtn)
//                            onboardingLayout.addView(showOnboardingBtn)
//                            tagsLayout?.addView(onboardingLayout)
//                        }
                        else -> {
                            val adaptiveTag = AdaptiveTag(ctx).apply {
                                id = ViewCompat.generateViewId()
                                setAdaptiveTagId(tag.id)
                                setHasBookmarks(tag.hasBookmarks ?: false)
                                setAdaptiveCustomActionCallback(object:
                                    AdaptiveCustomAction {
                                    override fun onRun(params: HashMap<String, Any>) {
                                        val name = params["name"]?.toString()
                                        context?.toast("Custom action: $name")
                                    }
                                })

                                if (tag.loadingType == AdaptiveSdkEnvironment.Tag.LoadingType.BANNERS_FULLSCREEN) {
                                    val redundantHeight = TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 56f, resources.displayMetrics
                                    ).toInt()

                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        resources.displayMetrics.heightPixels - redundantHeight
                                    )
                                }
                            }
                            tagsLayout?.addView(adaptiveTag)
                        }
                    }
                }

                addNewTagBtn?.setOnClickListener {
                    showAddNewTagDialog(configs.name)
                }

                deleteTagBtn.setOnClickListener {
                    context?.let { ctx ->
                        val checkDialog = AlertDialog.Builder(ctx)
                            .setMessage("Delete ${tagsSpinner.selectedItem} tag?")
                            .setPositiveButton("YES") { _, _ ->
                                removeTag(ctx, configs.name, tagsSpinner.selectedItem.toString())
                                setAdaptiveTags()
                            }
                            .setNegativeButton("NO") { _, _ -> }
                            .create()
                        checkDialog.show()
                    }
                }
            }
        }
    }

    private fun showAddNewTagDialog(envName: String) {
        val dialog = AddNewTagDialog.newInstance(envName, object: AddNewTagDialog.InteractionInterface {
            override fun onDismiss() {
                setAdaptiveTags()
            }
        })
        dialog.show(childFragmentManager, dialog.tag)
    }
}