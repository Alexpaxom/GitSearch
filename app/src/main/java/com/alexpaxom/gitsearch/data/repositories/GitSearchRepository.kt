package com.alexpaxom.gitsearch.data.repositories

import com.alexpaxom.gitsearch.data.cachedatabase.daos.RepositoryCardDAO
import com.alexpaxom.gitsearch.data.cachedatabase.daos.UserCardDAO
import com.alexpaxom.gitsearch.data.remoteapi.GitHubApiSearchRequests
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import io.reactivex.Observable
import javax.inject.Inject

@ScreenScope
class GitSearchRepository @Inject constructor(
    private val gitHubApiSearchRequests: GitHubApiSearchRequests,
    private val repositoryDao: RepositoryCardDAO,
    private val usersDao: UserCardDAO
) {
    fun searchRepositories(
        searchString: String,
        page: Int,
        perPage: Int,
        useCache: Boolean = false,
        refreshCache: Boolean = false,
    ): Observable<CacheWrapper<List<RepositoryCard>>> {
        return Observable.create { emiter ->

            // Берем кешированные данные с предыдущих запросов
            if (useCache) {
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
                val apiRepositories: List<RepositoryCard> = gitHubApiSearchRequests.getRepositories(
                    searchString,
                    page.toString(),
                    perPage.toString(),
                ).execute().body()?.repositoriesCards ?: listOf()

                // Сохраняем все результаты в БД для кеша
                if (refreshCache) {
                    repositoryDao.deleteAll()
                    usersDao.deleteAll()

                    repositoryDao.insertAll(apiRepositories)
                }

                emiter.onNext(CacheWrapper.OriginalData(apiRepositories))

                emiter.onComplete()

            } catch (e: Exception) {
                if (!emiter.isDisposed) emiter.onError(e)
            }
        }
    }
}