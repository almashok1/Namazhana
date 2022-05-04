package kz.farabicorporation.namazhana.arch.utils

import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.arch.models.OutcomeState

fun<T> OutcomeState<T>.onSuccess(
    action: (data: T?) -> Unit
): OutcomeState<T> {
    if (this is OutcomeState.Success) action.invoke(this.data)
    return this
}

fun<T> OutcomeState<T>.onError(
    action: (error: Outcome.Error) -> Unit
): OutcomeState<T> {
    if (this is OutcomeState.Error) action.invoke(error)
    return this
}

fun<T> OutcomeState<T>.onResponseError(
    action: (error: Outcome.Error.ResponseError) -> Unit
): OutcomeState<T> {
    if (this is OutcomeState.Error && this.error is Outcome.Error.ResponseError) action.invoke(error)
    return this
}

fun<T> OutcomeState<T>.onIdle(
    action: () -> Unit
): OutcomeState<T> {
    if (this is OutcomeState.Idle) action.invoke()
    return this
}

/**
 * Runs action only once when state is [OutcomeState.Loading]
 * */
fun<T> OutcomeState<T>.onLoading(
    action: () -> Unit
): OutcomeState<T> {
    if (this is OutcomeState.Loading) action.invoke()
    return this
}

/**
 * Runs action every time when state changes and calls boolean of [OutcomeState.Loading]
 * */
fun<T> OutcomeState<T>.isLoading(
    action: (Boolean) -> Unit
): OutcomeState<T> {
    action.invoke(this is OutcomeState.Loading)
    return this
}