package com.alexpaxom.gitsearch.app.helpers

import android.util.Log
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import javax.inject.Inject

@ScreenScope
class ErrorsHandler @Inject constructor(){
    fun processError(error: Throwable, critical: Boolean = false) {
        Log.e("ERROR", error.stackTraceToString())

        if(critical)
            throw error
    }
}