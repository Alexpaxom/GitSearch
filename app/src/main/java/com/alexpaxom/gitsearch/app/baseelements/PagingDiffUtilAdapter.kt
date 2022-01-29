package com.alexpaxom.homework_2.app.features.baseelements.adapters

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PagingRecyclerUtil(
    recyclerView: RecyclerView
) {

    abstract fun checkLoadData(bottomPos: Int, topPos: Int)

    init {
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            var prevBottom = -1
            var prevTop = -1

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                if(layoutManager != null) {
                    val newBottomPos = layoutManager.findLastVisibleItemPosition()
                    val newTopPos = layoutManager.findFirstVisibleItemPosition()

                    // Если значения не поменялись незачем проверять их на условие загрузки новой страницы
                    if(prevBottom == newBottomPos && prevTop == newTopPos)
                        return

                    prevBottom = newBottomPos
                    prevTop = newTopPos


                    checkLoadData(
                        bottomPos = newBottomPos,
                        topPos = newTopPos
                    )
                }
                else
                    error("Paging adapter can work only with LinearLayoutManager!")
            }
        })
    }
}
