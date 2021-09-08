package plus.adaptive.qaapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_api_page.*
import plus.adaptive.qaapp.R
import plus.adaptive.qaapp.ui.dialogs.AddNewAPViewDialog
import plus.adaptive.qaapp.utils.getEnvByName
import plus.adaptive.qaapp.utils.removeAPView
import plus.adaptive.qaapp.utils.toast
import plus.adaptive.sdk.ui.AdaptivePlusInstruction
import plus.adaptive.sdk.ui.AdaptivePlusView


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
            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    mSwipeRefresher?.isRefreshing = false
                }, 1000)
            }
        }

        setAdaptivePlusViews()
    }

    private fun setAdaptivePlusViews() {
        val envName = arguments?.getString(EXTRA_ENV_NAME)

        if (context == null || envName == null) {
            return
        }

        getEnvByName(requireContext(), envName)?.let { configs ->
            context?.let { ctx ->
                apViewsSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    configs.apViews.map { it.id }
                )

                apViewsLayout?.removeAllViews()

                for (apViewModel in configs.apViews) {
                    if(apViewModel.isInstruction == true){
                        activity?.apply {
                            val apButton = Button(context)
                            val instruction = AdaptivePlusInstruction.newInstance(
                                this,
                                childFragmentManager
                            )
                            instruction.preloadTagById(apViewModel.id)
                            instruction.setAPCustomActionListener {
                                val name = it["name"]?.toString()
                                context?.toast("Custom action: $name")
                            }
                            instruction.setOnStoriesFinishedCallback {
                                context?.toast("finish")
                                apButton.isEnabled = true
                            }
                            apButton.id = ViewCompat.generateViewId()
                            apButton.text = "SHOW INSTRUCTION"
                            apButton.setPadding(10,10,10,10)
                            apButton.setOnClickListener {
                                it.isEnabled = false
                                instruction.showInstruction()
                            }
                            val rewindImage = ImageView(ctx).apply {
                                val paddingSz = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                                ).toInt()
                                setPadding(paddingSz, paddingSz, paddingSz, paddingSz)

                                setImageResource(R.drawable.ic_rewind)
                            }
                            val apViewNameTxtView = TextView(ctx).apply {
                                val paddingSz = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
                                ).toInt()
                                setPadding(0, paddingSz, paddingSz, paddingSz)

                                text = "APView: ${apViewModel.id}"
                                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                                setTextColor(ContextCompat.getColor(context, R.color.colorOnSecondary))
                                setTextIsSelectable(true)
                            }
                            val apViewHeader = LinearLayout(ctx).apply {
                                orientation = LinearLayout.HORIZONTAL
                                gravity = Gravity.CENTER_VERTICAL

                                val icSize = TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics
                                ).toInt()
                                addView(rewindImage, LinearLayout.LayoutParams(icSize, icSize))
                                val txtViewLayoutParams = LinearLayout.LayoutParams(
                                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                                addView(apViewNameTxtView, txtViewLayoutParams)
                            }
                            apViewsLayout?.addView(apViewHeader)
                            apViewsLayout.addView(apButton)
                        }
                    } else {
                        val rewindImage = ImageView(ctx).apply {
                            val paddingSz = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                            ).toInt()
                            setPadding(paddingSz, paddingSz, paddingSz, paddingSz)

                            setImageResource(R.drawable.ic_rewind)
                        }
                        val apViewNameTxtView = TextView(ctx).apply {
                            val paddingSz = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
                            ).toInt()
                            setPadding(0, paddingSz, paddingSz, paddingSz)

                            text = "APView: ${apViewModel.id}"
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            setTextColor(ContextCompat.getColor(context, R.color.colorOnSecondary))
                            setTextIsSelectable(true)
                        }
                        val apViewHeader = LinearLayout(ctx).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL

                            val icSize = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics
                            ).toInt()
                            addView(rewindImage, LinearLayout.LayoutParams(icSize, icSize))
                            val txtViewLayoutParams = LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            addView(apViewNameTxtView, txtViewLayoutParams)
                        }
                        apViewsLayout?.addView(apViewHeader)

                        val apView = AdaptivePlusView(ctx).apply {
                            id = ViewCompat.generateViewId()
                            setAdaptivePlusViewId(apViewModel.id)
                            setHasDrafts(apViewModel.hasDrafts ?: false)
                            setAPCustomActionListener { params ->
                                val name = params["name"]?.toString()
                                context?.toast("Custom action: $name")
                            }
                        }
                        apViewsLayout?.addView(apView)

                        rewindImage.setOnClickListener {
                            apView.scrollToStart()
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
        val dialog = AddNewAPViewDialog.newInstance(envName).apply {
            setOnDismissListener { setAdaptivePlusViews() }
        }
        dialog.show(childFragmentManager, dialog.tag)
    }
}