package kz.farabicorporation.namazhana.arch.models

sealed class OutcomeState<out T> {

    object Idle: OutcomeState<Nothing>()

    object Loading: OutcomeState<Nothing>()

    data class Success<out T>(val data: T? = null): OutcomeState<T>()

    data class Error(val error: Outcome.Error) : OutcomeState<Nothing>()

}