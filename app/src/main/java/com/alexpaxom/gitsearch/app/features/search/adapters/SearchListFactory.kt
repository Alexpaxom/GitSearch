package com.alexpaxom.gitsearch.app.features.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.alexpaxom.gitsearch.R
import com.alexpaxom.gitsearch.databinding.RwElemRepositoryInfoBinding
import com.alexpaxom.homework_2.app.features.baseelements.adapters.BaseHolderFactory
import com.alexpaxom.homework_2.app.features.baseelements.adapters.BaseViewHolder

class SearchListFactory: BaseHolderFactory() {
    override fun createViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when(viewType) {
            R.layout.rw_elem_repository_info -> RepositoryCardViewHolder(
                RwElemRepositoryInfoBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
            )
            else -> error("Bad type search list adapter holder!")
        }
    }
}