package kz.farabicorporation.namazhana.common

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.farabicorporation.namazhana.arch.models.Outcome
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel : ViewModel(), KoinComponent {

    protected val context: Context by inject()

    private val _vmActionFragment = MutableLiveData<Event<VmActionFragment>>()
    val vmActionFragment: LiveData<Event<VmActionFragment>> = _vmActionFragment

    private val _vmActionActivity = MutableLiveData<Event<VmActionActivity>>()
    val vmActionActivity: LiveData<Event<VmActionActivity>> = _vmActionActivity

    private val jobs by lazy(LazyThreadSafetyMode.NONE) {
        hashMapOf<String, Job>()
    }

    private val coroutineScopeIO by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    fun <T : Any> Outcome<T>.handleError(action: (() -> Unit)? = null) {
        if (this is Outcome.Success) return

        val defaultMessage = context.getString(R.string.general_error_message)

        val message = when (this) {
            is Outcome.Error.ResponseError -> this.message
            is Outcome.Error.UnknownError -> this.message
            else -> defaultMessage
        } ?: defaultMessage

        AppMessages.sendSnackbarMessage(message, action)
    }

    protected fun launchIO(action: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) { action() }

    protected fun launchDefault(action: suspend () -> Unit) = viewModelScope.launch(Dispatchers.Default) { action() }

    protected fun launchNotBoundIO(action: suspend () -> Unit) = coroutineScopeIO.launch(Dispatchers.IO) { action() }

    protected fun withFragment(block: (Fragment) -> Unit) {
        VmActionFragment(block).invokeAction()
    }

    protected fun VmActionFragment.invokeAction() {
        val isUiThread = android.os.Looper.getMainLooper().isCurrentThread
        if (isUiThread) {
            _vmActionFragment.value = Event(this)
        } else {
            _vmActionFragment.postValue(Event(this))
        }
    }

    protected fun VmActionActivity.invokeAction() {
        val isUiThread = android.os.Looper.getMainLooper().isCurrentThread
        if (isUiThread) {
            _vmActionActivity.value = Event(this)
        } else {
            _vmActionActivity.postValue(Event(this))
        }
    }

    protected fun Job.bindSubscribe(name: String) {
        if (jobs.containsKey(name)) {
            jobs[name]?.cancel()
        } else {
            jobs[name] = this
        }
    }
}

interface VmAction

class VmActionFragment(
    var action: ((Fragment) -> Unit)? = null
): VmAction {
    operator fun invoke(fragment: Fragment) {
        action?.invoke(fragment)
        action = null
    }

}

class VmActionActivity(
    var action: ((AppCompatActivity) -> Unit)? = null
): VmAction {
    operator fun invoke(activity: AppCompatActivity) {
        action?.invoke(activity)
        action = null
    }

}