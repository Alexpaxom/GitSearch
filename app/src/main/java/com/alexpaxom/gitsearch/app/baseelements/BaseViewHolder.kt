package com.alexpaxom.homework_2.app.features.baseelements.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alexpaxom.homework_2.data.models.ListItem

abstract class BaseViewHolder<T: ListItem>(
        val binding: ViewBinding
    ): RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(model: T)
}