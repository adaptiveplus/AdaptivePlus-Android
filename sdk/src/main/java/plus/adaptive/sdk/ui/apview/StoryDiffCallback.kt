package plus.adaptive.sdk.ui.apview

import androidx.recyclerview.widget.DiffUtil
import plus.adaptive.sdk.data.models.story.Campaign


internal class StoryDiffCallback(
    private val oldList: List<Campaign>,
    private val newList: List<Campaign>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        return oldList[oldItemPos].id == newList[newItemPos].id
    }

    override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        val oldItem = oldList[oldItemPos]
        val newItem = newList[newItemPos]

        return oldItem.body.story?.body?.snaps?.size == newItem.body.story?.body?.snaps?.size
    }
}