package com.sprintsquads.adaptiveplus.ui.tag

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.DELAY_BETWEEN_CLICKS
import com.sprintsquads.adaptiveplus.data.models.AdaptiveEntry
import com.sprintsquads.adaptiveplus.data.models.AdaptiveTagTemplate
import com.sprintsquads.adaptiveplus.utils.getColorFromHex
import kotlinx.android.synthetic.main.ap_layout_entry_item.view.*


internal class AdaptiveEntriesAdapter(
    dataSet: List<AdaptiveEntry>
) : RecyclerView.Adapter<AdaptiveEntriesAdapter.EntryViewHolder>() {

    private val dataSet: MutableList<AdaptiveEntry>
    private var options: AdaptiveTagTemplate.Options? = null
    private var lastTimeClicked = 0L

    init {
        this.dataSet = ArrayList(dataSet)
    }


    fun updateDataSet(entries: List<AdaptiveEntry>) {
        val diffCallback = AdaptiveEntryDiffCallback(oldEntries = this.dataSet, newEntries = entries)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataSet.clear()
        dataSet.addAll(entries)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTagOptions(
        options: AdaptiveTagTemplate.Options,
        isForceUpdate: Boolean = false
    ) {
        this.options = options

        if (isForceUpdate) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ap_layout_entry_item, parent, false
            )
        )
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    inner class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastTimeClicked > DELAY_BETWEEN_CLICKS) {
                    lastTimeClicked = SystemClock.elapsedRealtime()
                    // TODO: run actions
                    // listener?.onClick(adapterPosition)
                }
            }
        }

        fun bind(item: AdaptiveEntry) = with(itemView) {
            // TODO: implement
            apEntryLayout.layoutParams = ConstraintLayout.LayoutParams(
                options?.width?.toInt() ?: 0,
                options?.height?.toInt() ?: 0
            )

            apEntryLayout.addView(
                View(context).apply {
                    setBackgroundColor(getColorFromHex("#FF0000"))
                },
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }
    }
}