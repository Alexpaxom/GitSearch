package com.alexpaxom.gitsearch.data.repositories

import com.alexpaxom.gitsearch.data.cachedatabase.daos.UserCardDAO
import com.alexpaxom.gitsearch.data.remoteapi.GitHubApiUsers
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.UserCard
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

@ScreenScope
class GitUsersRepository @Inject constructor(
    private val gitHubApiUsers: GitHubApiUsers,
    private val usersDao: UserCardDAO,
) {

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



    fun getUserById(
        userId: Long,
        useCache: Boolean = false,
        refreshCache: Boolean = false,
    ): Observable<CacheWrapper<UserCard>> {
        return Observable.create { emiter ->
            try {

                // Берем кешированные данные с предыдущих запросов
                if(useCache) {
                    val cachedUser = usersDao.getUserByID(userId)
                    if(cachedUser != null) {
                        emiter.onNext(CacheWrapper.CachedData(cachedUser))
                    }
                }

                // Запрашиваем данные с сервера и возвращаем
                val apiUserCard: UserCard = gitHubApiUsers.getUserById(
                    userId
                ).execute().body() ?: UserCard(id = 0)

                // Сохраняем все результаты в БД для кеша
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