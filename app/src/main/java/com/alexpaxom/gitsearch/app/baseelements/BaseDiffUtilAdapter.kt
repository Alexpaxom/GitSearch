package com.alexpaxom.homework_2.app.features.baseelements.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.alexpaxom.homework_2.data.models.ListItem

abstract class BaseDiffUtilAdapter<P: ListItem>(
    val holdersFactory: BaseHolderFactory
): RecyclerView.Adapter<BaseViewHolder<ListItem>>() {

    open var dataList: List<P>
        get() = diffUtil.currentList
        set(value) {
            diffUtil.submitList(value, doAfterUpdateData)
        }

    protected var diffUtil: AsyncListDiffer<P> = AsyncListDiffer(this, BaseDiffUtilCallback<P>())

    private var doAfterUpdateData: Runnable? = null


    override fun getItemViewType(position: Int): Int {
        return dataList[position].typeId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ListItem> {
        return holdersFactory(parent, viewType)
    }

    open fun setAfterUpdateDataCallBack(callback: Runnable) {
        this.doAfterUpdateData = callback
    }

    open fun detachAfterUpdateDataCallBack() {
        this.doAfterUpdateData = null
    }

    open fun updateItem(pos: Int, newItem: P) {
        val list: MutableList<P> = mutableListOf()
        list.addAll(diffUtil.currentList)
        list[pos] = newItem

        diffUtil.submitList(list)
    }

    open fun addItem(newItem: P) {
        val list: MutableList<P> = mutableListOf()
        list.addAll(diffUtil.currentList)
        list.add(newItem)

        diffUtil.submitList(list)
    }

    open fun removeItem(pos: Int) {
        val list: MutableList<P> = mutableListOf()
        list.addAll(diffUtil.currentList)
        list.removeAt(pos)

        diffUtil.submitList(list)
    }

    open fun removeAll() {
        diffUtil.submitList(listOf())
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ListItem>, position: Int) {
        holder.bind(diffUtil.currentList[position])
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.count()
    }
}

