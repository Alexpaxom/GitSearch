package com.alexpaxom.gitsearch.domain.interactors.search

import com.alexpaxom.gitsearch.R
import com.alexpaxom.gitsearch.app.baseelements.BaseStore
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchEvent
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchState
import com.alexpaxom.gitsearch.app.features.search.viewmodel.SearchFragmentViewModel
import com.alexpaxom.gitsearch.data.repositories.GitSearchRepository
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ScreenScope
class SearchInteractor @Inject constructor(
    private val gitSearchRepository: GitSearchRepository,
) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun search(
        event: SearchEvent.LoadPage,
        store: BaseStore<SearchState, SearchEvent>
    ) {
        gitSearchRepository.searchRepositories(
            searchString = event.searchString,
            page = event.pageNum,
            perPage = event.elementsPerPage,
            useCache = event.pageNum == SearchFragmentViewModel.START_PAGE,
            refreshCache = event.pageNum == SearchFragmentViewModel.START_PAGE
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread(), true)
            .subscribeBy(
                onNext = {
                    val typedRepositoriesList = setItemListType(it)

                    store.processEvent(SearchEvent.SearchResult(
                        sortRepositories(typedRepositoriesList),
                        event.pageNum
                    ))
                },
                onError = {
                    store.processEvent(SearchEvent.SearchError(it))
                }
            )
            .addTo(compositeDisposable)
    }

    private fun setItemListType(
        cachedData: CacheWrapper<List<RepositoryCard>>
    ):
    CacheWrapper<List<RepositoryCard>> {

        return cachedData.mapWrapped { list ->
            list.map { repository ->
                repository.copy(typeId = R.layout.rw_elem_repository_info)
            }
        }
    }

    private fun sortRepositories(
        cachedData: CacheWrapper<List<RepositoryCard>>
    ):
            CacheWrapper<List<RepositoryCard>> {

        return cachedData.mapWrapped { list ->
            list.sortedBy {
                    repositoryCard ->  repositoryCard.name
            }
        }
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}