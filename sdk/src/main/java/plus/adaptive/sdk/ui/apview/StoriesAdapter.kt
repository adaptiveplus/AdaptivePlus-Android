package plus.adaptive.sdk.ui.apview

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import plus.adaptive.sdk.R
import plus.adaptive.sdk.data.DELAY_BETWEEN_CLICKS
import plus.adaptive.sdk.ui.apview.vm.APEntryPointViewModelProvider
import kotlinx.android.synthetic.main.ap_layout_entry_item.view.*
import plus.adaptive.sdk.core.analytics.APAnalytics
import plus.adaptive.sdk.data.models.APAnalyticsEvent
import plus.adaptive.sdk.data.models.components.APComponent
import plus.adaptive.sdk.data.models.components.APTextComponent
import plus.adaptive.sdk.data.models.story.APOuterStyles
import plus.adaptive.sdk.data.models.story.Campaign
import plus.adaptive.sdk.ext.hide
import plus.adaptive.sdk.ext.show
import plus.adaptive.sdk.utils.StorySizeConst.CIRCLE_STORY_L
import plus.adaptive.sdk.utils.StorySizeConst.CIRCLE_STORY_M
import plus.adaptive.sdk.utils.StorySizeConst.CIRCLE_STORY_S
import plus.adaptive.sdk.utils.createStoryAction
import plus.adaptive.sdk.utils.drawCircleStoryOnLayout
import plus.adaptive.sdk.utils.drawStoryOnLayout
import plus.adaptive.sdk.utils.safeRun
import java.io.Serializable
import kotlin.math.roundToInt


internal class StoriesAdapter(
    dataSet: List<Campaign>,
    private val apEntryPointViewModelProvider: APEntryPointViewModelProvider
) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    private val dataSet: MutableList<Campaign> = ArrayList(dataSet)
    private var options = StoriesOptions(0.0, 0.0, 0.0)
    private var scaleFactor: Float = 1f
    private var lastTimeClicked = 0L
    private var showCampaignBorder = false


    class StoriesOptions(
        val width: Double,
        val height: Double,
        val cornerRadius: Double
    )


    fun updateDataSet(list: List<Campaign>, showBorder: Boolean) {
        showCampaignBorder = showBorder
        val diffCallback = StoryDiffCallback(oldList = this.dataSet, newList = list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataSet.clear()
        dataSet.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateEntryOptions(options: StoriesOptions, scaleFactor: Float) {
        this.options = options
        this.scaleFactor = scaleFactor
        notifyDataSetChanged()
    }

    fun positionOfEntryPoint(id: String) : Int {
        return dataSet.indexOfFirst { it.id == id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ap_story_item, parent, false
            )
        )
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(campaign: Campaign) = with(itemView) {
            val viewModel = apEntryPointViewModelProvider.getStoriesViewModel(campaign)
            val isCircle = (options.height.toDouble()/options.cornerRadius).roundToInt() == 2
            viewModel?.prepare()
            val actionsList = listOf(createStoryAction(campaign))
            itemView.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastTimeClicked > DELAY_BETWEEN_CLICKS) {
                    lastTimeClicked = SystemClock.elapsedRealtime()
                    campaign.body.story?.run {
                        APAnalytics.logEvent(
                            APAnalyticsEvent(
                                name = "action-outer-image",
                                campaignId = campaign.id,
                                apViewId = viewModel?.getAPViewId(),
                                params = mapOf("storyId" to id)
                            )
                        )
                    }
                    viewModel?.runActions(actionsList)
                }
            }

            val heightPadding = if(isCircle){
                when(campaign.body.story?.body?.outerStyles?.outerSize){
                    APOuterStyles.OuterSize.S -> CIRCLE_STORY_S
                    APOuterStyles.OuterSize.M -> CIRCLE_STORY_M
                    APOuterStyles.OuterSize.L -> CIRCLE_STORY_L
                    else -> 0
                }
            } else {
                0
            }
            apEntryCardView.layoutParams = LinearLayout.LayoutParams(
                (options.width * scaleFactor).toInt(),
                ((options.height + heightPadding) * scaleFactor).toInt()
            )
            if(!isCircle)
                apEntryCardView.radius = (options.cornerRadius * scaleFactor).toFloat()

            val apEntryLayoutConstraintSet = ConstraintSet()
            apEntryLayoutConstraintSet.clone(apEntryLayout)
            apEntryLayoutConstraintSet.constrainWidth(apEntryLayersLayout.id, options.width.toInt())
            apEntryLayoutConstraintSet.constrainHeight(apEntryLayersLayout.id, options.height.toInt() + heightPadding)
            apEntryLayoutConstraintSet.setScaleX(apEntryLayersLayout.id, scaleFactor)
            apEntryLayoutConstraintSet.setScaleY(apEntryLayersLayout.id, scaleFactor)
            apEntryLayoutConstraintSet.applyTo(apEntryLayout)
            campaign.body.story?.body?.let {
                val component = StoryComponent(
                    outerText = it.outerText,
                    outerBorderColor = it.outerBorderColor,
                    outerStyles = it.outerStyles!!,
                    outerImageUrl = it.outerImageUrl,
                    showBorder = showCampaignBorder,
                )
                safeRun {
                    if(options.height == options.width){
                        if(isCircle){
                            drawCircleStoryOnLayout(apEntryLayersLayout, component, viewModel)
                        } else {
                            drawStoryOnLayout(apEntryLayersLayout, component, viewModel, true)
                        }
                    } else {
                        drawStoryOnLayout(apEntryLayersLayout, component, viewModel)
                    }
                }
            }

            if (campaign.status == Campaign.Status.DRAFT) {
                apTagTextView.show()
            } else {
                apTagTextView.hide()
            }
        }
    }


    data class StoryComponent(
        val outerText: APTextComponent.APLocale,
        val outerBorderColor: String?,
        val outerStyles: APOuterStyles,
        val outerImageUrl: String?,
        val showBorder: Boolean
    ) : Serializable, APComponent
}