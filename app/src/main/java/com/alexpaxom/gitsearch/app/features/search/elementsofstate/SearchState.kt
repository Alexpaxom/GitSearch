package com.alexpaxom.gitsearch.app.features.search.elementsofstate

import com.alexpaxom.gitsearch.app.baseelements.BaseState
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard

data class SearchState (
    val searchResultList: List<RepositoryCard> = listOf(),
    val isEmptyLoading: Boolean = false,
    val currentPage: Int = 1,
    val isNextPageLoading: Boolean = false,
    val isError: Boolean = false
): BaseState