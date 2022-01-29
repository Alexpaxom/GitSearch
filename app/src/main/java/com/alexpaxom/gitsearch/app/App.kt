package com.alexpaxom.gitsearch.app

import android.app.Application

class App: Application() {


    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object {
         lateinit var application: Application
            private set
    }

}