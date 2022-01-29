package com.alexpaxom.gitsearch.app.features.search.elementsofstate

import com.alexpaxom.gitsearch.app.baseelements.BaseEvent
import com.alexpaxom.gitsearch.domain.entities.CacheWrapper
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard

sealed interface SearchEvent: BaseEvent {
    class Search(val searchString: String): SearchEvent
    class LoadPage(val searchString: String, val pageNum: Int, val elementsPerPage: Int)
    class LoadNextPage(): SearchEvent
    class SearchResult(val gitSearchResult: CacheWrapper<List<RepositoryCard>>, val pageNum:Int): SearchEvent
    class SearchError(val error: String): SearchEvent
}