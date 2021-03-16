package com.sprintsquads.adaptiveplusqaapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.sprintsquads.adaptiveplus.sdk.data.APCustomAction
import com.sprintsquads.adaptiveplus.sdk.ui.AdaptivePlusView
import com.sprintsquads.adaptiveplusqaapp.R
import com.sprintsquads.adaptiveplusqaapp.data.APSdkEnvironment
import com.sprintsquads.adaptiveplusqaapp.ui.dialogs.AddNewAPViewDialog
import com.sprintsquads.adaptiveplusqaapp.utils.getEnvByName
import com.sprintsquads.adaptiveplusqaapp.utils.removeAPView
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
            apViewsLayout.children.forEach {
                (it as? AdaptivePlusView)?.refresh()
            }
            Handler().postDelayed({
                mSwipeRefresher?.isRefreshing = false
            }, 1000)
        }

        setAdaptivePlusViews()
    }

    private fun setAdaptivePlusViews() {
        val envName = arguments?.getString(EXTRA_ENV_NAME)

        if (context == null || envName == null) {
            return
        }

        getEnvByName(context!!, envName)?.let { configs ->
            context?.let { ctx ->
                apViewsSpinner.adapter = ArrayAdapter<String>(
                    context!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    configs.apViews.map { it.id }
                )

                apViewsLayout?.removeAllViews()

                for (apView in configs.apViews) {
                    val apViewNameTxtView = TextView(ctx).apply {
                        val paddingSz = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
                        setPadding(paddingSz, paddingSz, paddingSz, paddingSz)

                        text = "APView: ${apView.id}"
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        setTextColor(ContextCompat.getColor(context, R.color.colorOnSecondary))
                    }
                    apViewsLayout?.addView(apViewNameTxtView)

                    when {
                        else -> {
                            val apView = AdaptivePlusView(ctx).apply {
                                id = ViewCompat.generateViewId()
                                setAdaptivePlusViewId(apView.id)
                                setAdaptiveCustomActionCallback(object:
                                    APCustomAction {
                                    override fun onRun(params: HashMap<String, Any>) {
                                        val name = params["name"]?.toString()
                                        context?.toast("Custom action: $name")
                                    }
                                })

                                if (apView.loadingType == APSdkEnvironment.APView.LoadingType.BANNERS_FULLSCREEN) {
                                    val redundantHeight = TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP, 56f, resources.displayMetrics
                                    ).toInt()

                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        resources.displayMetrics.heightPixels - redundantHeight
                                    )
                                }
                            }
                            apViewsLayout?.addView(apView)
                        }
                    }
                }

                addNewAPViewBtn?.setOnClickListener {
                    showAddNewAPViewDialog(configs.name)
                }

                deleteAPViewBtn.setOnClickListener {
                    context?.let { ctx ->
                        val checkDialog = AlertDialog.Builder(ctx)
                            .setMessage("Delete ${apViewsSpinner.selectedItem} APView?")
                            .setPositiveButton("YES") { _, _ ->
                                removeAPView(ctx, configs.name, apViewsSpinner.selectedItem.toString())
                                setAdaptivePlusViews()
                            }
                            .setNegativeButton("NO") { _, _ -> }
                            .create()
                        checkDialog.show()
                    }
                }
            }
        }
    }

    private fun showAddNewAPViewDialog(envName: String) {
        val dialog = AddNewAPViewDialog.newInstance(envName, object: AddNewAPViewDialog.InteractionInterface {
            override fun onDismiss() {
                setAdaptivePlusViews()
            }
        })
        dialog.show(childFragmentManager, dialog.tag)
    }
}