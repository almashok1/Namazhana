package kz.farabicorporation.namazhana.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.arch.models.OutcomeState
import kz.farabicorporation.namazhana.arch.utils.onError
import kz.farabicorporation.namazhana.arch.utils.onSuccess
import kotlin.coroutines.CoroutineContext

class Stated<T>(
    private val scope: CoroutineScope,
    private val context: CoroutineContext = scope.coroutineContext,
    private val debounce: Long = 0,
    private val action: (suspend () -> Outcome<T>)? = null
) {

    @OptIn(FlowPreview::class)
    private val _result = MutableStateFlow<OutcomeState<T>>(OutcomeState.Idle).apply {
        if (debounce != 0L) debounce(debounce)
    }
    val flow: Flow<OutcomeState<T>> get() = _result

    val current: OutcomeState<T> get() = _result.value

    fun request(): Stated<T> {
        action?.let { request(it) }
        return this
    }

    fun request(action: suspend () -> Outcome<T>): Stated<T> {
        scope.launch(context) {
            _result.value = OutcomeState.Loading
            action.invoke().onSuccess { data ->
                _result.value = OutcomeState.Success(data)
            }.onError { error ->
                _result.value = OutcomeState.Error(error)
            }
        }
        return this
    }

    fun post(newOutcomeState: OutcomeState<T>): Stated<T> {
        _result.value = newOutcomeState
        return this
    }
}