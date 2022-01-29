package com.alexpaxom.gitsearch.app.features.search.adapters

import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.homework_2.app.features.baseelements.adapters.BaseDiffUtilAdapter

class SearchListAdapter(searchListFactory: SearchListFactory) :
    BaseDiffUtilAdapter<RepositoryCard>(searchListFactory) {
}