package kz.farabicorporation.namazhana.common.extensions

import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.onScrollEnd(action: () -> Unit){
    setOnScrollChangeListener { _, _, _, _, _ ->
        layoutManager?.let { manager ->
            val totalItemCount = manager.itemCount
            val lastVisibleItemPosition = when(manager) {
                is LinearLayoutManager ->  manager.findLastVisibleItemPosition()
                is GridLayoutManager -> manager.findLastVisibleItemPosition()
                else -> 0
            }
            val endHasBeenReached = lastVisibleItemPosition + 1 >= totalItemCount
            if (totalItemCount > 0 && endHasBeenReached) action()
        }
    }
}

fun NestedScrollView.onScrollEnd(action: () -> Unit){
    setOnScrollChangeListener { _, _, _, _, _ ->
        if (childCount>0 && getChildAt(0).bottom <= (height + scrollY))
            action()
    }
}

fun RecyclerView.setUpLayoutManager(isHorizontal: Boolean = false, reverseLayout: Boolean = false){
    layoutManager = LinearLayoutManager(
            context,
            if (!isHorizontal) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL,
            reverseLayout
    )
}