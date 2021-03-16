package com.sprintsquads.adaptiveplus.ui.apview

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sprintsquads.adaptiveplus.R
import com.sprintsquads.adaptiveplus.data.DELAY_BETWEEN_CLICKS
import com.sprintsquads.adaptiveplus.data.models.APEntry
import com.sprintsquads.adaptiveplus.utils.drawEntry
import kotlinx.android.synthetic.main.ap_layout_entry_item.view.*


internal class APEntryPointsAdapter(
    dataSet: List<APEntry>
) : RecyclerView.Adapter<APEntryPointsAdapter.EntryViewHolder>() {

    private val dataSet: MutableList<APEntry> = ArrayList(dataSet)
    private var options: EntryOptions = EntryOptions(0.0, 0.0, 0.0)
    private var scaleFactor: Float = 1f
    private var lastTimeClicked = 0L


    class EntryOptions(
        val width: Double,
        val height: Double,
        val cornerRadius: Double
    )


    fun updateDataSet(entries: List<APEntry>) {
        val diffCallback = APEntryPointDiffCallback(oldEntries = this.dataSet, newEntries = entries)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataSet.clear()
        dataSet.addAll(entries)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateEntryOptions(options: EntryOptions, scaleFactor: Float) {
        this.options = options
        this.scaleFactor = scaleFactor
        notifyDataSetChanged()
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

        fun bind(entry: APEntry) = with(itemView) {
            apEntryCardView.layoutParams = LinearLayout.LayoutParams(
                (options.width * scaleFactor).toInt(),
                (options.height * scaleFactor).toInt()
            )
            apEntryCardView.radius = (options.cornerRadius * scaleFactor).toFloat()

            drawEntry(apEntryLayout, entry, scaleFactor)
        }
    }
}