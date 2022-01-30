package com.alexpaxom.gitsearch.app

import android.app.Application
import com.alexpaxom.gitsearch.di.application.AppComponent
import com.alexpaxom.gitsearch.di.application.DaggerAppComponent

class App: Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        application = this
    }

    companion object {
         lateinit var application: Application
            private set
    }
}