package com.alexpaxom.gitsearch.di.screen

import com.alexpaxom.gitsearch.app.features.mainwindow.activities.MainActivity
import com.alexpaxom.gitsearch.app.features.repositorydetails.fragments.RepositoryDetailsFragment
import com.alexpaxom.gitsearch.app.features.search.fragments.SearchFragment
import dagger.Subcomponent

@Subcomponent(modules = [ScreenModule::class])
@ScreenScope
interface ScreenComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(repositoryDetailsFragment: RepositoryDetailsFragment)
    fun inject(searchFragment: SearchFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): ScreenComponent
    }
}