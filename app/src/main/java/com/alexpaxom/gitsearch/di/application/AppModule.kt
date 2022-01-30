package com.alexpaxom.gitsearch.di.application

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.alexpaxom.gitsearch.app.App
import com.alexpaxom.gitsearch.data.cachedatabase.CacheDatabase
import com.alexpaxom.gitsearch.di.helpers.ViewModelFactory
import com.alexpaxom.gitsearch.di.screen.ScreenComponent
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module(
    includes = [AppModule.BindingModule::class],
    subcomponents = [ScreenComponent::class]
)
class AppModule {

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        return httpLoggingInterceptor
    }

    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    fun provide(context: Context): CacheDatabase {
        return Room.databaseBuilder(
            context,
            CacheDatabase::class.java, "cacheDatabase"
        ).build()
    }

    @Module
    interface BindingModule {
        @Binds
        fun bind(application: App): Context

        @Binds
        fun bindViewModelFactory(
            factory: ViewModelFactory
        ): ViewModelProvider.Factory
    }


}