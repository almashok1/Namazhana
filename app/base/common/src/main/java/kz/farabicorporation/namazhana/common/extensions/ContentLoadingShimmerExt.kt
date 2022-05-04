package kz.farabicorporation.namazhana.common.extensions

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout

/**
 * Starts shimmer if view is VISIBLE and it's lifecycle is onResume otherwise stops
 * */
fun ViewBinding.setUpShimmerVisibility() {
    if (root is ShimmerFrameLayout) {
        (root as ShimmerFrameLayout).setUpShimmerVisibility()
    }
}

/**
 * Starts shimmer if view is VISIBLE and it's lifecycle is onResume otherwise stops
 * */
fun ShimmerFrameLayout.setUpShimmerVisibility() {
    viewTreeObserver.addOnGlobalLayoutListener {
        if (visibility == View.VISIBLE) {
            startShimmer()
        } else {
            stopShimmer()
        }
    }
    findViewTreeLifecycleOwner()?.lifecycle?.addObserver(
        object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume(){
                /* Start shimmer onResume if previously was visible */
                if (visibility == View.VISIBLE) startShimmer()
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause(){
                /* Stop shimmer onPause */
                stopShimmer()
            }
        }
    )
}