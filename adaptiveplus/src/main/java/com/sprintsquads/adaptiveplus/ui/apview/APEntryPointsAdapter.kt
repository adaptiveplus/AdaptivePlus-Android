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
import com.sprintsquads.adaptiveplus.data.models.APEntryPoint
import com.sprintsquads.adaptiveplus.ui.apview.vm.APEntryPointViewModelProvider
import com.sprintsquads.adaptiveplus.utils.drawAPLayersOnLayout
import kotlinx.android.synthetic.main.ap_layout_entry_item.view.*


internal class APEntryPointsAdapter(
    dataSet: List<APEntryPoint>,
    private val apEntryPointViewModelProvider: APEntryPointViewModelProvider
) : RecyclerView.Adapter<APEntryPointsAdapter.EntryViewHolder>() {

    private val dataSet: MutableList<APEntryPoint> = ArrayList(dataSet)
    private var options: EntryOptions = EntryOptions(0.0, 0.0, 0.0)
    private var scaleFactor: Float = 1f
    private var lastTimeClicked = 0L


    class EntryOptions(
        val width: Double,
        val height: Double,
        val cornerRadius: Double
    )


    fun updateDataSet(entryPoints: List<APEntryPoint>) {
        val diffCallback = APEntryPointDiffCallback(oldEntryPoints = this.dataSet, newEntryPoints = entryPoints)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        dataSet.clear()
        dataSet.addAll(entryPoints)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateEntryOptions(options: EntryOptions, scaleFactor: Float) {
        this.options = options
        this.scaleFactor = scaleFactor
        notifyDataSetChanged()
    }

    fun positionOfEntryPoint(id: String) : Int {
        return dataSet.indexOfFirst { it.id == id }
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

        fun bind(entryPoint: APEntryPoint) = with(itemView) {
            val viewModel = apEntryPointViewModelProvider.getAPEntryPointViewModel(entryPoint)
            viewModel?.prepare()

            apEntryCardView.setOnClickListener {
                if (SystemClock.elapsedRealtime() - lastTimeClicked > DELAY_BETWEEN_CLICKS) {
                    lastTimeClicked = SystemClock.elapsedRealtime()
                    viewModel?.runActions(entryPoint.actions)
                }
            }

            apEntryCardView.layoutParams = LinearLayout.LayoutParams(
                (options.width * scaleFactor).toInt(),
                (options.height * scaleFactor).toInt()
            )
            apEntryCardView.radius = (options.cornerRadius * scaleFactor).toFloat()

            drawAPLayersOnLayout(apEntryLayout, entryPoint.layers, scaleFactor, viewModel)
        }
    }
}