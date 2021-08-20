package com.fitsionary.momspt.presentation.base

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import com.fitsionary.momspt.util.listener.OnItemClickListener

abstract class BaseRecyclerViewAdapter<B : ViewDataBinding, T : Any>(
    itemDiffCallback: BaseDiffCallback<T> = BaseDiffCallback(),
    @LayoutRes private val layoutResId: Int,
    private val bindingVariableItemId: Int? = null,
    private val bindingVariableListenerId: Int? = null
) : ListAdapter<T, BaseViewHolder<B, T>>(itemDiffCallback) {

    var onItemClickListener = object : OnItemClickListener<T> {
        override fun onClick(item: T) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<B, T>(
            layoutResId = layoutResId,
            parent = parent,
            bindingVariableItemId = bindingVariableItemId,
            bindingVariableListenerId = bindingVariableListenerId
        ) {}

    override fun onBindViewHolder(holder: BaseViewHolder<B, T>, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, onItemClickListener)
    }
}

