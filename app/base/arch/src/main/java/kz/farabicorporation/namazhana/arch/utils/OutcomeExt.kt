package kz.farabicorporation.namazhana.arch.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kz.farabicorporation.namazhana.arch.models.Outcome

inline fun<T, R> Outcome<T>.map(
    transform: (T?) -> R? = { null }
): Outcome<R> {
    return when(this) {
        is Outcome.Success -> Outcome.Success(transform(data), code)
        is Outcome.Error.ResponseError -> Outcome.Error.ResponseError(message, params)
        Outcome.Error.ConnectionError -> Outcome.Error.ConnectionError
        is Outcome.Error.UnknownError -> Outcome.Error.UnknownError(message)
    }
}

suspend inline fun<T, R> Outcome<T>.transform(
    crossinline transform: suspend (T?) -> Outcome<R>
): Outcome<R> {
    return when(this) {
        is Outcome.Success -> transform(this.data)
        is Outcome.Error.ResponseError -> Outcome.Error.ResponseError(message, params)
        Outcome.Error.ConnectionError -> Outcome.Error.ConnectionError
        is Outcome.Error.UnknownError -> Outcome.Error.UnknownError(message)
    }
}

fun<T> Outcome<T>.mapCheckNull(): Outcome<T> {
    return if (this is Outcome.Success && this.data == null)
        Outcome.Error.UnknownError()
    else this
}

fun<T> Outcome<T>.onSuccess(
    action: (data: T?) -> Unit
): Outcome<T> {
    if (this is Outcome.Success) action.invoke(this.data)
    return this
}

fun<T> Outcome<T>.onError(
    action: (error: Outcome.Error) -> Unit
): Outcome<T> {
    if (this is Outcome.Error) action.invoke(this)
    return this
}

fun<T> Outcome<T>.onResponseError(
    action: (error: Outcome.Error.ResponseError, code: Int?) -> Unit
): Outcome<T> {
    if (this is Outcome.Error.ResponseError) action.invoke(this, params.status)
    return this
}

fun<T> Outcome<T>.onConnectionError(
    action: () -> Unit
): Outcome<T> {
    if (this is Outcome.Error.ConnectionError) action.invoke()
    return this
}

fun<T> Outcome<T>.onUnknownError(
    action: () -> Unit
): Outcome<T> {
    if (this is Outcome.Error.UnknownError) action.invoke()
    return this
}

fun<T> Outcome<T>.onComplete(
    action: (Outcome<T>) -> Unit
): Outcome<T> {
    action(this)
    return this
}

fun<T> Outcome<T>.updateStateFlow(
    flow: MutableStateFlow<Outcome<T>>
): Outcome<T> {
    flow.value = this
    return this
}

fun<T> Outcome<T>.updateSharedFlow(
    flow: MutableSharedFlow<Outcome<T>>
): Outcome<T> {
    if (this is Outcome.Success) flow.tryEmit(this)
    return this
}

fun<T> Outcome<T>.successOrNull(): T? {
    return if (this is Outcome.Success<T>) data else null
}

suspend fun<T> Outcome<T>.doOnSuccess(
    action: suspend (data: T?) -> Unit
): Outcome<T> {
    if (this is Outcome.Success<T>) action.invoke(data)
    return this
}

suspend fun<T> Outcome<T>.doOnComplete(
    action: suspend () -> Unit
): Outcome<T> {
    action()
    return this
}

suspend fun<T> Outcome<T>.doOnError(
    action: suspend (error: Outcome.Error) -> Unit
): Outcome<T> {
    if (this is Outcome.Error) action.invoke(this)
    return this
}

suspend fun<T> Outcome<T>.doOnResponseError(
    action: suspend (error: Outcome.Error.ResponseError, code: Int?) -> Unit
): Outcome<T> {
    if (this is Outcome.Error.ResponseError) action.invoke(this, params.status)
    return this
}

suspend fun<T> Outcome<T>.doOnConnectionError(
    action: suspend () -> Unit
): Outcome<T> {
    if (this is Outcome.Error.ConnectionError) action.invoke()
    return this
}

suspend fun<T> Outcome<T>.doOnUnknownError(
    action: suspend (message: String?) -> Unit
): Outcome<T> {
    if (this is Outcome.Error.UnknownError) action.invoke(message)
    return this
}

fun<T> T.asSuccess() = Outcome.Success(this)