package com.alexpaxom.gitsearch.app.helpers

import android.util.Log

class ErrorsHandler {
    fun processError(error: Throwable, critical: Boolean = false) {
        Log.e("ERROR", error.stackTraceToString())

        if(critical)
            throw error
    }
}