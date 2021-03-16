package com.sprintsquads.adaptiveplus.ui.apview

import androidx.recyclerview.widget.DiffUtil
import com.sprintsquads.adaptiveplus.data.models.APEntry


internal class APEntryPointDiffCallback(
    private val oldEntries: List<APEntry>,
    private val newEntries: List<APEntry>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldEntries.size

    override fun getNewListSize(): Int = newEntries.size

    override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        return oldEntries[oldItemPos].options.id == newEntries[newItemPos].options.id
    }

    override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        val oldItem = oldEntries[oldItemPos]
        val newItem = newEntries[newItemPos]

        return oldItem.layers.size == newItem.layers.size &&
            oldItem.actions.size == newItem.actions.size
    }
}