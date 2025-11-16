package com.serag.cryptotracker.core.domain.util

typealias DomainError = Error

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : DomainError, out D>(val error: E, val data: D? = null) :
        Result<D, E>
}

inline fun <T, E : Error, R> Result<T, E>.map(map: (T?) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error, map(data))
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { }
}

inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E : Error> Result<T, E>.onError(action: (E, T?) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> {
            action(error, data)
            this
        }

        is Result.Success -> this
    }
}

inline fun <T, E : Error> Result<T, E>.onResult(action: (E?, T?) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> {
            action(error, data)
            this
        }

        is Result.Success -> {
            action(null, data)
            this
        }
    }

}

typealias EmptyResult<E> = Result<Unit, E>