package plus.adaptive.sdk.ui.stories

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager


internal class APStoriesViewPager : ViewPager {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v != this && v is ViewPager) {
            return false
        }

        return super.canScroll(v, checkV, dx, x, y)
    }
}