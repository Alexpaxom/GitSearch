package com.alexpaxom.gitsearch.app.features.search.adapters

import com.alexpaxom.gitsearch.domain.entities.RepositoryCard
import com.alexpaxom.gitsearch.databinding.RwElemRepositoryInfoBinding
import com.alexpaxom.homework_2.app.features.baseelements.adapters.BaseViewHolder

class RepositoryCardViewHolder(
    private val rwElemRepositoryInfoBinding: RwElemRepositoryInfoBinding,
    private val onRepositoryClick: (position: Int) -> Unit
) : BaseViewHolder<RepositoryCard>(rwElemRepositoryInfoBinding) {

    init {
        rwElemRepositoryInfoBinding.root.setOnClickListener {
            onRepositoryClick( adapterPosition )
        }
    }

    override fun bind(model: RepositoryCard) {
        rwElemRepositoryInfoBinding.rwElemRepositoryTitle.text = model.name
        model.description?.let {
            val descrption = it.substring(0, it.length.coerceAtMost(MAX_DESCRIPTION_CHARACTER_SIZE))
                rwElemRepositoryInfoBinding.rwElemRepositoryDescription.text =
                    "$descrption${if(descrption.length < it.length) "..." else ""}"
        }
        rwElemRepositoryInfoBinding.rwElemRepositoryForks.text = model.forksCount.toString()
    }

    companion object {
        private const val MAX_DESCRIPTION_CHARACTER_SIZE = 200
    }
}