package com.alexpaxom.gitsearch.app.features.search.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexpaxom.gitsearch.app.baseelements.BaseStore
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchEvent
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchState
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.domain.interactors.search.ImmutableListUtils
import com.alexpaxom.gitsearch.domain.interactors.search.SearchInteractor

class SearchFragmentViewModel : ViewModel(), BaseStore<SearchState, SearchEvent> {

    private var lastSearchedString:String = ""
    private var allPageLoaded: Boolean = false

    private val _currentState: MutableLiveData<SearchState> = MutableLiveData()
    val viewState: LiveData<SearchState>
        get() = _currentState
    private val currentState: SearchState
        get() = _currentState.value ?: SearchState()

    private val searchInteractor: SearchInteractor = SearchInteractor(this)

    private val listUtils: ImmutableListUtils<RepositoryCard> = ImmutableListUtils()

    override fun processEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.Search -> {
                setState(
                    currentState.copy(
                        searchResultList = listOf(),
                        currentPage = START_PAGE,
                        isEmptyLoading = true
                    )
                )
                lastSearchedString = event.searchString
                searchInteractor.search(
                    SearchEvent.LoadPage(
                        event.searchString,
                        START_PAGE,
                        COUNT_ELEMENTS_PER_PAGE
                    )
                )
            }
            //TODO сделать централизованный обработчик ошибок
            is SearchEvent.SearchError -> Log.e("TEST", event.error)
            is SearchEvent.SearchResult -> {
                if(event.gitSearchResult.data.isEmpty()) {
                    allPageLoaded = true

                    setState(
                        currentState.copy(
                            isNextPageLoading = false
                        )
                    )

                    return
                }

                setState(
                    currentState.copy(
                        searchResultList = listUtils.append(
                            currentList = currentState.searchResultList,
                            newList = event.gitSearchResult.data
                        ),
                        isEmptyLoading = event.gitSearchResult is CacheWrapper.CachedData,
                        isNextPageLoading = false,
                        currentPage = event.pageNum
                    )
                )
            }
            is SearchEvent.LoadNextPage -> {
                if(currentState.isNextPageLoading || allPageLoaded)
                    return

                setState(
                    currentState.copy(
                        isNextPageLoading = true
                    )
                )

                searchInteractor.search(
                    SearchEvent.LoadPage(
                        lastSearchedString,
                        currentState.currentPage+1,
                        COUNT_ELEMENTS_PER_PAGE
                    )
                )
            }
        }
    }

    override fun setState(state: SearchState) {
        _currentState.value = state
    }

    override fun onCleared() {
        searchInteractor.onDestroy()
        super.onCleared()
    }

    companion object {
        const val COUNT_ELEMENTS_BEFORE_START_LOAD = 5
        const val START_PAGE = 1
        const val COUNT_ELEMENTS_PER_PAGE = 10
    }
}