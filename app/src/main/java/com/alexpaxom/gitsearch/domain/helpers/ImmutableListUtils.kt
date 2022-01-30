package com.alexpaxom.gitsearch.domain.interactors.search

import com.alexpaxom.homework_2.data.models.ListItem

class ImmutableListUtils<T> {
    fun append(
        currentList: List<T>,
        newList: List<T>
    ): List<T> {
        val resultList = arrayListOf<T>()
        resultList.addAll(currentList)
        resultList.addAll(newList)

        return resultList
    }
}