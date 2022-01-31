package com.alexpaxom.gitsearch.app.features.search.adapters

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.homework_2.app.features.baseelements.adapters.BaseDiffUtilAdapter

class SearchListAdapter(searchListFactory: SearchListFactory) :
    BaseDiffUtilAdapter<RepositoryCard>(searchListFactory) {

    init {
        diffUtil = AsyncListDiffer(this, SearchListDiffUtil())
    }

    class SearchListDiffUtil: DiffUtil.ItemCallback<RepositoryCard>() {
        override fun areItemsTheSame(oldItem: RepositoryCard, newItem: RepositoryCard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RepositoryCard, newItem: RepositoryCard): Boolean {
            return oldItem == newItem
        }
    }
}