package com.sprintsquads.adaptiveplus.ui.apview

import androidx.recyclerview.widget.DiffUtil
import com.sprintsquads.adaptiveplus.data.models.APEntryPoint


internal class APEntryPointDiffCallback(
    private val oldEntryPoints: List<APEntryPoint>,
    private val newEntryPoints: List<APEntryPoint>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldEntryPoints.size

    override fun getNewListSize(): Int = newEntryPoints.size

    override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        return oldEntryPoints[oldItemPos].id == newEntryPoints[newItemPos].id
    }

    override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        val oldItem = oldEntryPoints[oldItemPos]
        val newItem = newEntryPoints[newItemPos]

        return oldItem.layers.size == newItem.layers.size &&
            oldItem.actions.size == newItem.actions.size
    }
}