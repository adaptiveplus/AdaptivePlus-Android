package com.sprintsquads.adaptiveplus.ui.entry

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.sprintsquads.adaptiveplus.data.models.AdaptiveEntry
import com.sprintsquads.adaptiveplus.ui.tag.vm.AdaptiveTagViewModelDelegate


internal class AdaptiveEntriesView : ConstraintLayout {

    private var tagViewModelDelegate: AdaptiveTagViewModelDelegate? = null
    private lateinit var entries: List<AdaptiveEntry>

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        tagViewModelDelegate: AdaptiveTagViewModelDelegate,
        entries: List<AdaptiveEntry>
    ) : super(context) {
        this.tagViewModelDelegate = tagViewModelDelegate
        this.entries = entries

        initView()
    }

    private fun initView() {
        // TODO: implement
    }

    fun updateEntries(entries: List<AdaptiveEntry>) {
        // TODO: implement
    }
}