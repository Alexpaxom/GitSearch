package com.alexpaxom.gitsearch.app.features.search.adapters

import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.databinding.RwElemRepositoryInfoBinding
import com.alexpaxom.homework_2.app.features.baseelements.adapters.BaseViewHolder

class RepositoryCardViewHolder(private val rwElemRepositoryInfoBinding: RwElemRepositoryInfoBinding) :
    BaseViewHolder<RepositoryCard>(rwElemRepositoryInfoBinding) {
    override fun bind(model: RepositoryCard) {
        rwElemRepositoryInfoBinding.rwElemRepositoryTitle.text = model.name
        rwElemRepositoryInfoBinding.rwElemRepositoryDescription.text = model.description
        rwElemRepositoryInfoBinding.rwElemRepositoryForks.text = model.forksCount.toString()
    }
}