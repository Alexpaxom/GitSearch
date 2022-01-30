package com.alexpaxom.gitsearch.di.screen

import androidx.lifecycle.ViewModel
import com.alexpaxom.gitsearch.app.features.mainwindow.viewmodel.MainActivityViewModel
import com.alexpaxom.gitsearch.app.features.repositorydetails.viewmodel.RepositoryDetailsViewModel
import com.alexpaxom.gitsearch.app.features.search.viewmodel.SearchFragmentViewModel
import com.alexpaxom.gitsearch.data.cachedatabase.CacheDatabase
import com.alexpaxom.gitsearch.data.cachedatabase.daos.RepositoryCardDAO
import com.alexpaxom.gitsearch.data.cachedatabase.daos.UserCardDAO
import com.alexpaxom.gitsearch.data.remoteapi.GitHubApiSearchRequests
import com.alexpaxom.gitsearch.data.remoteapi.GitHubApiUsers
import com.alexpaxom.gitsearch.di.helpers.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

@Module(
    includes = [ScreenModule.ScreenBindingModule::class]
)
class ScreenModule {

    // Retrofit API provides

    @Provides
    fun provideGitHubApiSearchRequests(retrofit: Retrofit): GitHubApiSearchRequests {
        return retrofit.create(GitHubApiSearchRequests::class.java)
    }

    @Provides
    fun provideGitHubApiUsers(retrofit: Retrofit): GitHubApiUsers {
        return retrofit.create(GitHubApiUsers::class.java)
    }


    // Database providers

    @Provides
    fun provideRepositoryCardDAO(cacheDatabase: CacheDatabase): RepositoryCardDAO {
        return cacheDatabase.repositoriesDao()
    }

    @Provides
    fun provideUserCardDAO(cacheDatabase: CacheDatabase): UserCardDAO {
        return cacheDatabase.usersDao()
    }

    @Module
    interface ScreenBindingModule {
        @Binds
        @IntoMap
        @ViewModelKey(MainActivityViewModel::class)
        fun bindMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

        @Binds
        @IntoMap
        @ViewModelKey(RepositoryDetailsViewModel::class)
        fun bindRepositoryDetailsViewModel(viewModel: RepositoryDetailsViewModel): ViewModel

        @Binds
        @IntoMap
        @ViewModelKey(SearchFragmentViewModel::class)
        fun bindSearchFragmentViewModel(viewModel: SearchFragmentViewModel): ViewModel
    }
}