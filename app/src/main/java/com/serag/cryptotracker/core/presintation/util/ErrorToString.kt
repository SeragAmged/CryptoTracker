package com.serag.cryptotracker.core.presintation.util

import android.content.Context
import com.serag.cryptotracker.R
import com.serag.cryptotracker.core.domain.util.DatabaseError
import com.serag.cryptotracker.core.domain.util.Error
import com.serag.cryptotracker.core.domain.util.NetworkError

fun Error.toString(context: Context): String {
    val resId =
        when (this) {
            NetworkError.REQUEST_TIMEOUT -> R.string.error_request_timeout
            NetworkError.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
            NetworkError.NO_INTERNET -> R.string.error_no_internet
            NetworkError.SERVER_ERROR -> R.string.error_unknown
            NetworkError.SERIALIZATION -> R.string.error_serialization
            NetworkError.UNKNOWN -> R.string.error_unknown
            DatabaseError.UNKNOWN -> R.string.something_went_wrong_reinstall_the_app
            else -> R.string.error_unknown
        }
    return context.getString(resId)
}
