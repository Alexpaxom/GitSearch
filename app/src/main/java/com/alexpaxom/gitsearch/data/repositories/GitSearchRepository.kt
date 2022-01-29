package com.alexpaxom.gitsearch.data.repositories

import com.alexpaxom.gitsearch.data.cachedatabase.SingletonDatabase
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.data.remoteapi.GitHubApiRequests
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class GitSearchRepository
{
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

    private val gitHubApiRequests: GitHubApiRequests = retrofit.create(GitHubApiRequests::class.java)

    private val repositoryDao = SingletonDatabase.cacheDatabase.repositoriesDao()
    private val usersDao = SingletonDatabase.cacheDatabase.usersDao()

    fun searchRepositories(
        searchString:String,
        page:Int,
        perPage:Int,
        useCache: Boolean = false,
        refreshCache: Boolean = false,
    ): Observable<CacheWrapper<List<RepositoryCard>>> {
        return Observable.create { emiter ->

            // Берем кешированные данные с предыдущих запросов
            if(useCache) {
                val cacheRepositories = repositoryDao
                    .getAll()
                    .filter {
                        // Ищем в сохраненном кеше строку соотвествующую запросу
                        it.description?.contains(searchString, ignoreCase = true) ?: false ||
                                it.name?.contains(searchString, ignoreCase = true) ?: false
                    }
                if (cacheRepositories.isNotEmpty())
                    emiter.onNext(CacheWrapper.OriginalData(cacheRepositories))
            }

            try {
                // Запрашиваем данные с сервера и возвращаем
                val apiRepositories: List<RepositoryCard> = gitHubApiRequests.getRepositories(
                    searchString,
                    page.toString(),
                    perPage.toString(),
                ).execute().body()?.repositoriesCards ?: listOf()

                if(refreshCache) {
                    repositoryDao.deleteAll()
                    usersDao.deleteAll()
                }
                // Сохраняем все результаты в БД для кеша
                repositoryDao.insertAll(apiRepositories)

                emiter.onNext(CacheWrapper.OriginalData(apiRepositories))

                emiter.onComplete()

            } catch (e: Exception) {
                if(!emiter.isDisposed) emiter.onError(e)
            }
        }
    }
}