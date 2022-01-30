package com.alexpaxom.gitsearch.di.application

import com.alexpaxom.gitsearch.app.App
import com.alexpaxom.gitsearch.di.screen.ScreenComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun getScreenComponent(): ScreenComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            application: App
        ): AppComponent
    }
}