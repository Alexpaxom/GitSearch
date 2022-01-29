package com.alexpaxom.gitsearch.data.repositories

import com.alexpaxom.gitsearch.data.cachedatabase.SingletonDatabase
import com.alexpaxom.gitsearch.data.remoteapi.GitHubApiUsers
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.UserCard
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class GitUsersRepository {

    private val httpLoggingInterceptor = HttpLoggingInterceptor()
        .apply {
            HttpLoggingInterceptor.Level.BODY
        }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    private val gitHubApiUsers: GitHubApiUsers = retrofit.create(GitHubApiUsers::class.java)

    private val usersDao = SingletonDatabase.cacheDatabase.usersDao()

    fun getUserById(
        userId: Long,
        useCache: Boolean = false,
        refreshCache: Boolean = false,
    ): Observable<CacheWrapper<UserCard>> {
        return Observable.create { emiter ->

            if(useCache) {
                val cachedUser = usersDao.getUserByID(userId)
                if(cachedUser != null) {
                    emiter.onNext(CacheWrapper.CachedData(cachedUser))
                }
            }

            try {
                // Запрашиваем данные с сервера и возвращаем
                val apiUserCard: UserCard = gitHubApiUsers.getUserById(
                    userId
                ).execute().body() ?: UserCard(id = 0)

                if(refreshCache) {
                    usersDao.insert(apiUserCard)
                }

                emiter.onNext(CacheWrapper.OriginalData(apiUserCard))

                emiter.onComplete()

            } catch (e: Exception) {
                if (!emiter.isDisposed) emiter.onError(e)
            }
        }
    }
}