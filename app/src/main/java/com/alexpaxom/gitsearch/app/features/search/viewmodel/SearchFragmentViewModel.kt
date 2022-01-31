package com.alexpaxom.gitsearch.app.features.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexpaxom.gitsearch.app.baseelements.BaseStore
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchEffect
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchEvent
import com.alexpaxom.gitsearch.app.features.search.elementsofstate.SearchState
import com.alexpaxom.gitsearch.app.helpers.ErrorsHandler
import com.alexpaxom.gitsearch.app.helpers.LiveDataEvent
import com.alexpaxom.gitsearch.app.helpers.SharPrefUtil
import com.alexpaxom.gitsearch.di.screen.ScreenScope
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.domain.interactors.search.ImmutableListUtils
import com.alexpaxom.gitsearch.domain.interactors.search.SearchInteractor
import javax.inject.Inject

@ScreenScope
class SearchFragmentViewModel @Inject constructor(
    private val searchInteractor: SearchInteractor,
    private val errorsHandler: ErrorsHandler,
    private val sharPrefUtil: SharPrefUtil

) : ViewModel(), BaseStore<SearchState, SearchEvent> {

    private var lastSearchedString: String = ""
    private var allPageLoaded: Boolean = false

    private val _currentState: MutableLiveData<SearchState> = MutableLiveData()
    val viewState: LiveData<SearchState>
        get() = _currentState
    private val currentState: SearchState
        get() = _currentState.value ?: SearchState()

    private val _effect: MutableLiveData<LiveDataEvent<SearchEffect>> = MutableLiveData()
    val effect: LiveData<LiveDataEvent<SearchEffect>>
        get() = _effect

    private val listUtils: ImmutableListUtils<RepositoryCard> = ImmutableListUtils()

    init {
        lastSearchedString = sharPrefUtil.getFromPref(LAST_SEARCH_STRING_KEY)
        processSearchEvent(SearchEvent.Search(lastSearchedString))
    }


    override fun processEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.Search -> processSearchEvent(event)
            is SearchEvent.SearchError -> processSearchErrorEvent(event)
            is SearchEvent.SearchResult -> processSearchResultEvent(event)
            is SearchEvent.LoadNextPage -> processLoadNextPageEvent(event)
            is SearchEvent.InternetConnectionEvent -> processInternetConnectionEvent(event)
        }
    }

    private fun processSearchEvent(event: SearchEvent.Search) {
        allPageLoaded = false

        sharPrefUtil.addToPref(LAST_SEARCH_STRING_KEY, event.searchString)

        setState(
            currentState.copy(
                searchResultList = listOf(),
                nextPage = START_PAGE,
                isEmptyLoading = true
            )
        )
        lastSearchedString = event.searchString
        searchInteractor.search(
            event = SearchEvent.LoadPage(
                event.searchString,
                START_PAGE,
                COUNT_ELEMENTS_PER_PAGE
            ),
            store = this
        )
    }

    private fun processSearchErrorEvent(event: SearchEvent.SearchError) {
        errorsHandler.processError(event.error)

        setState(
            currentState.copy(
                isNextPageLoading = false,
                isEmptyLoading = false
            )
        )

        _effect.value = LiveDataEvent(
            SearchEffect.ShowError(
                event.error.localizedMessage
                    ?: event.error.message
                    ?: "Error when try search!"
            )
        )
    }

    private fun processSearchResultEvent(event: SearchEvent.SearchResult) {
        if (event.gitSearchResult.data.isEmpty()) {
            allPageLoaded = true

            setState(
                currentState.copy(
                    isNextPageLoading = false,
                    isEmptyLoading = false
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
                nextPage = event.pageNum + 1
            )
        )
    }

    private fun processLoadNextPageEvent(event: SearchEvent.LoadNextPage) {
        if (currentState.isNextPageLoading || allPageLoaded || currentState.isEmptyLoading)
            return

        setState(
            currentState.copy(
                isNextPageLoading = true
            )
        )

        searchInteractor.search(
            event = SearchEvent.LoadPage(
                lastSearchedString,
                currentState.nextPage,
                COUNT_ELEMENTS_PER_PAGE
            ),
            store = this
        )
    }

    private fun processInternetConnectionEvent(event: SearchEvent.InternetConnectionEvent) {
        if (event.hasInternetConnection == currentState.hasInternetConnection)
            return

        setState(
            currentState.copy(
                hasInternetConnection = event.hasInternetConnection,
                isNextPageLoading = false,
                isEmptyLoading = false
            )
        )
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

        private const val LAST_SEARCH_STRING_KEY = "LAST_SEARCH_STRING"
    }
}