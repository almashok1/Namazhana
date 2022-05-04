package kz.farabicorporation.namazhana.common

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kz.farabicorporation.namazhana.common.extensions.makeSnackBar

object AppMessages {

    private val _snackbarMessages = MutableLiveData<Event<Pair<String, (() -> Unit)?>>>()
    val snackbarMessages: LiveData<Event<Pair<String, (() -> Unit)?>>> = _snackbarMessages

    fun sendSnackbarMessage(message: String, action: (()->Unit)? = null) {
        _snackbarMessages.postValue(Event(message to action))
    }

}

fun AppCompatActivity.showSnackbar(
    pair: Pair<String, (() -> Unit)?>,
    navBarHeight: Int
) {
    val f = supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.lastOrNull()
    val root = if (f is BottomSheetDialogFragment) f.dialog?.window?.decorView else f?.view
    val marginBottom = if (f is BottomSheetDialogFragment) navBarHeight else 0
    makeSnackBar(pair.first, action = { pair.second }, root = root, marginBottom = marginBottom).show()
}
