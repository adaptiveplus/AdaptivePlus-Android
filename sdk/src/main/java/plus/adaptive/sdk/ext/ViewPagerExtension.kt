package plus.adaptive.sdk.ext

import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

/**
 * Method to set page transition animation
 * duration to view pager
 *
 * @param newDuration - duration in milliseconds
 */
internal fun ViewPager.setTransitionDuration(newDuration: Int) {
    val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
    mScroller.isAccessible = true
    mScroller.set(this, object: Scroller(context) {
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, newDuration)
        }
    })
}