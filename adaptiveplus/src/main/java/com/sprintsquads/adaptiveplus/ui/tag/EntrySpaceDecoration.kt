package com.sprintsquads.adaptiveplus.ui.tag

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


internal class EntrySpaceDecoration(
    private val space: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) > 0) {
            outRect.left = space
        }
    }
}