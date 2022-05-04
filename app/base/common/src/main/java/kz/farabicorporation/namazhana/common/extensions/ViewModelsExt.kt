package kz.farabicorporation.namazhana.common.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.arch.models.OutcomeState
import kz.farabicorporation.namazhana.data.Stated
import kotlin.coroutines.CoroutineContext

fun<T> ViewModel.getState(
    context: CoroutineContext = viewModelScope.coroutineContext,
    debounce: Long = 0,
    action: (suspend () -> Outcome<T>)? = null
) = Stated(scope = viewModelScope, context = context, debounce = debounce, action = action)

inline val<T> Stated<T>.liveData: LiveData<OutcomeState<T>> get() = flow.asLiveData()