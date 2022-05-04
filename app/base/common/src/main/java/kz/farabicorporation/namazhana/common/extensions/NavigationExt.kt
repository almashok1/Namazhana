package kz.farabicorporation.namazhana.common.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kz.farabicorporation.namazhana.common.R

fun NavController.navigateWithAnim(
    @IdRes resId: Int,
    navOptions: NavOptions.Builder = getSlideAnimNavBuilder(),
    @IdRes popUpTo: Int? = null,
    inclusive: Boolean = false
) = navigate(resId, null, navOptions.apply {
    if (popUpTo != null) {
        setPopUpTo(popUpTo, inclusive)
    }
}.build())

fun getSlideAnimNavBuilder() = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_left)
    .setExitAnim(R.anim.wait_anim)
    .setPopEnterAnim(R.anim.wait_anim)
    .setPopExitAnim(R.anim.slide_right)

fun <T> Fragment.observeSavedState(key: String, handle: (T) -> Unit) {
    findNavController().currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<T>(key)
        ?.observe(viewLifecycleOwner) {
            handle(it)
        }
}

fun <T> Fragment.observePrevSavedState(key: String, handle: (T) -> Unit) {
    findNavController().previousBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<T>(key)
        ?.observe(viewLifecycleOwner) {
            handle(it)
        }
}

fun <T> Fragment.removeSavedStateObserver(key: String, handle: (T) -> Unit) {
    findNavController().currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<T>(key)
        ?.removeObserver(handle)
}

fun <T> Fragment.removeAllSavedStateObservers(key: String) {
    findNavController().currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<T>(key)
        ?.removeObservers(viewLifecycleOwner)
}

fun <T> Fragment.setToPrevSavedState(key: String, data: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
}

fun <T> Fragment.setToCurrentSavedState(key: String, data: T) {
    findNavController().currentBackStackEntry?.savedStateHandle?.set(key, data)
}

fun NavController.navigateSafe(@IdRes resId: Int, args: Bundle? = null) {
    val destinationId = currentDestination?.getAction(resId)?.destinationId
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        destinationId?.let {
            currentNode?.findNode(destinationId)?.let { navigate(resId, args) }
        }
    }
}

fun NavController.navigateSafe(directions: NavDirections) {
    navigateSafe(directions.actionId, directions.arguments)
}
