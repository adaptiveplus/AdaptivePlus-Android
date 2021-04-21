package plus.adaptive.sdk.core.glide

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition


internal open class GlideWrappingTarget<Z>(
    private val target: Target<Z>
) : Target<Z> {

    override fun onStart() {
        target.onStart()
    }

    override fun onStop() {
        target.onStop()
    }

    override fun onDestroy() {
        target.onDestroy()
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        target.onLoadStarted(placeholder)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        target.onLoadFailed(errorDrawable)
    }

    override fun onResourceReady(resource: Z, transition: Transition<in Z>?) {
        resource?.let { target.onResourceReady(resource, transition) }
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        target.onLoadCleared(placeholder)
    }

    override fun getSize(cb: SizeReadyCallback) {
        target.getSize(cb)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
        target.removeCallback(cb)
    }

    override fun setRequest(request: Request?) {
        target.request = request
    }

    override fun getRequest(): Request? {
        return target.request
    }
}